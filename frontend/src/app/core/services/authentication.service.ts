import { computed, effect, inject, Injectable, signal } from '@angular/core';
import { UserAccount } from '@features/users/interfaces/user-account.interface';
import {
  KEYCLOAK_EVENT_SIGNAL,
  KeycloakEventType,
  ReadyArgs,
  typeEventArgs,
} from 'keycloak-angular';
import Keycloak from 'keycloak-js';

@Injectable({
  providedIn: 'root',
})
export class AuthenticationService {
  private readonly keycloak = inject(Keycloak);
  private readonly keycloakSignal = inject(KEYCLOAK_EVENT_SIGNAL);

  readonly isAuthenticated = signal(this.keycloak.authenticated ?? false);

  readonly authUser = signal<UserAccount | null>(this.createUserAccount());

  readonly assignedRoles = computed(() => this.authUser()?.roles ?? []);

  constructor() {
    effect(() => {
      const event = this.keycloakSignal();

      switch (event.type) {
        case KeycloakEventType.Ready:
          this.isAuthenticated.set(typeEventArgs<ReadyArgs>(event.args));
          this.syncUser();
          break;

        case KeycloakEventType.AuthSuccess:
        case KeycloakEventType.AuthRefreshSuccess:
          this.syncUser();
          break;

        case KeycloakEventType.AuthLogout:
        case KeycloakEventType.AuthRefreshError:
          this.isAuthenticated.set(false);
          this.authUser.set(null);
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

  private syncUser(): void {
    this.isAuthenticated.set(this.keycloak.authenticated ?? false);

    this.authUser.set(this.createUserAccount());
  }

  private createUserAccount(): UserAccount | null {
    const token = this.keycloak.tokenParsed;

    return token ? this.toUserAccount(token) : null;
  }

  private toUserAccount(token: Keycloak.KeycloakTokenParsed): UserAccount {
    return {
      id: token['sub'] ?? '',
      username: token['preferred_username'] ?? '',
      email: token['email'] ?? '',
      givenName: token['given_name'] ?? '',
      familyName: token['family_name'] ?? '',
      name: token['name'] ?? '',
      roles: token['realm_access']?.['roles'] ?? [],
    };
  }
}
