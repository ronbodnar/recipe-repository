import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { User } from './users/user';

import { StorageService } from './storage.service';
import { LoginComponent } from './users/login/login.component';

import { EMPTY, Observable, catchError, map } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthenticationService {

  private loginComponent!: LoginComponent;

  private authenticatedUser!: User | null;

  //private readonly authUrl: string = 'https://ronbodnar.com:8443/auth';
  //private readonly authUrl: string = 'http://ec2-18-221-117-214.us-east-2.compute.amazonaws.com:8080/auth';
  private readonly authUrl: string = 'http://localhost:8080/auth';

  constructor(
    private http: HttpClient,
    private router: Router,
    private storageService: StorageService
  ) {}

  // Perform pre-authentication checks for the front-end.
  checkAuthentication(): Observable<never|void> {
    // Skip if already authenticated
    if (this.authenticatedUser !== undefined) return EMPTY

    // Set the authenticatedUser from session storage if it exists.
    if (this.storageService.getUser() !== null) {
      this.authenticatedUser = this.storageService.getUser()
      return EMPTY
    }

    // Perform an authentication check with the server.
    return this.http.get(`${this.authUrl}/user`).pipe(
      map((res: any) => {
        console.log(res);
        if (res !== null) {
          // If the user is authenticated, a Principal is received and the authenticated user is set for the session.
          if (res.principal) {
            this.authenticatedUser = new User()
            this.authenticatedUser.setFromJson(res.principal)

            this.storageService.setUser(this.authenticatedUser)
          }
        }
      }), catchError((error) => {
        console.log('checkIfAuthenticated error:')
        console.log(error)
        return EMPTY
      })
    );
  }

  // Authenticate with the back-end and set session authentication.
  authenticate(username: string, password: string) {
    return this.http
      .post(`${this.authUrl}/login`, { username: username, password: password })
      .subscribe({
        error: (error: any) => {
          if (error && error.error) {
            this.loginComponent.handleError(error.error)
          }
          console.log('AUTH ERROR:')
          console.log(error)
        },
        next: (response: any) => {
          // Instantiate a new User and set variables from the JSON response
          let user = new User()
          user.setFromJson(response)

          // Remove the user data from the session
          this.authenticatedUser = user
          this.storageService.setUser(user)
        },
        complete: () => {
          this.loginComponent.onComplete()
        },
      });
  }

  // Deauthenticate and clear session storage.
  deauthenticate() {
    return this.http
      .post(`${this.authUrl}/revoke`, {})
      .subscribe(() => {
          // Remove the user data from the session
          this.authenticatedUser = null
          this.storageService.clear()

          // Redirect to the home page
          this.router.navigate(['/'])
        });
  }

  // Register a new user account.
  register(registerRequest: any) {
    return this.http
      .post(`${this.authUrl}/register`, registerRequest)
      .subscribe({
        error: (error: any) => {
          console.log('registration error(s):')
          console.log(error.error)
          let errors = error.error
        },
        next: (response: any) => {
        },
        complete: () => {
        }
      });
  }

  formatErrorMessage(error: any) : string {
    let errorMessages: any = {
      "Bad credentials": "Invalid username or password."
    }
    // Connection error cheap fix
    if (error instanceof ProgressEvent) {
      return 'Unable to reach login server.'
    }
    return errorMessages.hasOwnProperty(error) ? errorMessages[error] : error
  }

  getAuthenticatedUser(): User | null {
    return this.authenticatedUser
  }

  isAuthenticated(): boolean {
    return this.authenticatedUser != null
  }

  setLoginComponent(loginComponent: LoginComponent): void {
    this.loginComponent = loginComponent
  }
}
