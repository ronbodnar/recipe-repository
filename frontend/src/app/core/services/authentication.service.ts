import { effect, inject, Injectable, signal } from '@angular/core';
import { KEYCLOAK_EVENT_SIGNAL, KeycloakEventType } from 'keycloak-angular';
import Keycloak from 'keycloak-js';
import { environment } from '@env';
import { FetchApiService } from './fetch-api.service';
import { StorageService } from './storage.service';
import { UserAccount } from '@features/users/user.types';

@Injectable({
  providedIn: 'root',
})
export class AuthenticationService {
  private readonly fetchApi = inject(FetchApiService);
  private readonly storageService = inject(StorageService);

  private readonly keycloak = inject(Keycloak);
  private readonly keycloakSignal = inject(KEYCLOAK_EVENT_SIGNAL);

  readonly isAuthenticated = signal(this.keycloak.authenticated ?? false);

  readonly authUser = signal<UserAccount | null>(this.storageService.getUserAccount());

  constructor() {
    effect(() => {
      const event = this.keycloakSignal();

      switch (event.type) {
        case KeycloakEventType.Ready:
        case KeycloakEventType.AuthSuccess:
        case KeycloakEventType.AuthRefreshSuccess:
        case KeycloakEventType.AuthLogout:
        case KeycloakEventType.AuthRefreshError:
          this.syncUser();
          break;

        default:
          if (!environment.production) {
            console.log('Unhandled Keycloak event received:', event);
          }
          break;
      }
    });
  }

  login(): Promise<void> {
    return this.keycloak.login();
  }

  register(): Promise<void> {
    return this.keycloak.register();
  }

  async logout(): Promise<void> {
    await this.keycloak.logout();
  }

  update(user: UserAccount): void {
    this.authUser.set(user);
    this.storageService.setUserAccount(user);
  }

  private syncUser(): void {
    const authenticated = this.keycloak.authenticated ?? false;

    this.isAuthenticated.set(authenticated);

    if (!authenticated) {
      this.authUser.set(null);
      return;
    }

    this.fetchApi.getData<UserAccount>('identity/me').subscribe({
      next: (user) => {
        if (!user) {
          console.error('Authenticated UserAccount data is null or undefined.');
          this.authUser.set(null);
          return;
        }

        const userAccount = this.createEnrichedUserAccount(user);

        console.log('Received enriched authenticated user data:', userAccount);

        this.authUser.set(userAccount);
        this.storageService.setUserAccount(userAccount);
      },
      error: (error) => {
        console.error('Error fetching authenticated user:', error);
        this.authUser.set(null);
      },
    });
  }

  private createEnrichedUserAccount(user: UserAccount): UserAccount {
    const token = this.keycloak.tokenParsed;

    const resourceRoles =
      token?.['resource_access']?.[environment.keycloak.clientId]?.['roles'] ?? [];

    return {
      ...user,
      identityProviderSubject: token?.['sub'] ?? '',
      username: token?.['preferred_username'],
      email: token?.['email'],
      givenName: token?.['given_name'],
      familyName: token?.['family_name'],
      name: token?.['name'],
      roles: resourceRoles.map((role) => `permission:${role.toLowerCase()}`),
    };
  }
}
