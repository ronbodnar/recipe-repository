import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { User } from './users/user';

import { StorageService } from './storage.service';
import { LoginComponent } from './users/login/login.component';
import { RegisterComponent } from './users/register/register.component';

import { EMPTY, Observable, catchError, map, tap } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthenticationService {
  private loginComponent!: LoginComponent;
  private registerComponent!: RegisterComponent;

  private authenticatedUser!: User | null;

  //private readonly authUrl: string = 'https://ec2.ronbodnar.com:8443/auth'
  private readonly authUrl: string = 'http://localhost:8080/auth';

  constructor(
    private http: HttpClient,
    private router: Router,
    private storageService: StorageService
  ) {}

  // Perform pre-authentication checks for the front-end.
  checkAuthentication(): Observable<never | void> {
    // Skip if already authenticated
    if (this.authenticatedUser !== undefined) return EMPTY;

    // Set the authenticatedUser from session storage if it exists.
    if (this.storageService.getUser() !== null) {
      this.authenticatedUser = this.storageService.getUser();
      return EMPTY;
    }

    // Perform an authentication check with the server.
    return this.http.get(`${this.authUrl}/user`).pipe(
      map((res: any) => {
        if (res !== null) {
          // If the user is authenticated, a Principal is received and the authenticated user is set for the session.
          if (res.principal) {
            this.authenticatedUser = new User();
            this.authenticatedUser.setFromJson(res.principal);

            this.storageService.setUser(this.authenticatedUser);
          }
        }
      }), catchError(() => EMPTY)
    );
  }

  // Authenticate with the back-end and set session authentication.
  authenticate(username: string, password: string) {
    return this.http
      .post(`${this.authUrl}/login`, { username: username, password: password })
      .pipe(
        tap((response: any) => {
          // Instantiate a new User and set variables from the JSON response
          let user = new User();
          user.setFromJson(response);

          // Remove the user data from the session
          this.authenticatedUser = user;
          this.storageService.setUser(user);
        }), catchError(error => this.loginComponent.handleError(error.error))
      );
  }

  // Deauthenticate and clear session storage.
  deauthenticate() {
    return this.http.post(`${this.authUrl}/revoke`, {}).pipe(
      map(() => {
        // Remove the user data from the session
        this.authenticatedUser = null;
        this.storageService.clear();
      }), catchError(() => EMPTY)
    );
  }

  // Register a new user account.
  register(registerRequest: any) {
    return this.http.post(`${this.authUrl}/register`, registerRequest).pipe(
      catchError(error => this.registerComponent.handleErrors(error.error))
    );
  }

  getAuthenticatedUser(): User | null {
    return this.authenticatedUser;
  }

  isAuthenticated(): boolean {
    return this.authenticatedUser != null;
  }

  setLoginComponent(loginComponent: LoginComponent): void {
    this.loginComponent = loginComponent;
  }

  setRegisterComponent(registerComponent: RegisterComponent): void {
    this.registerComponent = registerComponent;
  }
}
