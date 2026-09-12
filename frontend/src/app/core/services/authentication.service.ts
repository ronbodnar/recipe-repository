import { computed, inject, Injectable, signal } from '@angular/core';
import { StorageService } from './storage.service';
import { Observable, catchError, finalize, of, shareReplay, tap, throwError } from 'rxjs';
import { FetchApiService } from './fetch-api.service';
import { ApiError } from '../models/api-error.model';
import { DeviceService } from './device.service';
import { CanActivateFn, Router } from '@angular/router';
import { authGuard } from '@core/guards/auth.guard';
import { UserAccount } from '@features/users/user.types';
import { UserRegistrationRequest } from '@features/auth/auth.types';

@Injectable({
  providedIn: 'root',
})
export class AuthenticationService {
  private authRequest$?: Observable<UserAccount | null>;

  private _authUser = signal<UserAccount | undefined | null>(undefined);

  public readonly authUser = this._authUser.asReadonly();

  private readonly router = inject(Router);
  private readonly deviceService = inject(DeviceService);
  private readonly storageService = inject(StorageService);
  private readonly fetchApiService = inject(FetchApiService);

  readonly isAuthenticated = computed(() => this.authUser() != null);

  readonly authenticatedRoles = computed(() => this.authUser()?.roles || []);

  checkAuthentication(): Observable<UserAccount | null> {
    const authUser = this._authUser();
    if (authUser !== undefined) {
      return of(authUser);
    }

    if (!this.authRequest$) {
      this.authRequest$ = this.fetchApiService.getData<UserAccount>('auth/me').pipe(
        tap((user) => {
          console.log('Authentication check successful, user:', user);
          this._authUser.set(user ?? null);
        }),
        catchError((error) => {
          console.log('Authentication check produced an error:', error);
          this.clearAuthentication();
          this._authUser.set(null);
          return of(null);
        }),
        shareReplay(1),
      );
    }

    return this.authRequest$;
  }

  login(username: string, password: string) {
    const body = {
      username: username,
      password: password,
      deviceId: this.deviceService.getDeviceId(),
    };

    return this.fetchApiService
      .fetch<UserAccount>('auth/login', { method: 'POST', requestBody: body })
      .pipe(
        catchError((error: ApiError) => throwError(() => error)),
        tap((response) => {
          this._authUser.set(response);
          this.storageService.setUserAccount(response);
        }),
      );
  }

  register(userData: UserRegistrationRequest) {
    const body = {
      ...userData,
      givenName: userData.givenName || null,
      familyName: userData.familyName || null,
      deviceId: this.deviceService.getDeviceId(),
    };

    return this.fetchApiService.postData<UserAccount>('auth/register', body).pipe(
      catchError((error: ApiError) => throwError(() => error)),
      tap((response) => {
        this._authUser.set(response);
        this.storageService.setUserAccount(response);
      }),
    );
  }

  logout() {
    return this.fetchApiService.fetch('auth/logout', { method: 'POST' }).pipe(
      finalize(() => {
        this.clearAuthentication();
        this.storageService.clear();

        const hasAuthGuard = this.routeHasGuard(authGuard);

        if (hasAuthGuard) {
          this.router.navigate(['/']);
        }
      }),
    );
  }

  refreshToken() {
    return this.fetchApiService.fetch<UserAccount>('auth/refresh', { method: 'GET' }).pipe(
      catchError((error: ApiError) => {
        this.clearAuthentication();
        return throwError(() => error);
      }),
      tap((authUser) => {
        if (!this._authUser() || authUser?.id !== this._authUser()?.id) {
          this._authUser.set(authUser);
        }
      }),
    );
  }

  clearAuthentication() {
    this._authUser.set(null);
  }

  setAuthUser(user: UserAccount) {
    this._authUser.set(user);
  }

  private routeHasGuard(guard: CanActivateFn): boolean {
    let route = this.router.routerState.snapshot.root;

    while (route) {
      if (route.routeConfig?.canActivate?.includes(guard)) {
        return true;
      }
      route = route.firstChild!;
    }

    return false;
  }
}
