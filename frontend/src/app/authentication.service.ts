import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { User } from './users/user';

import { StorageService } from './storage.service';

@Injectable({
  providedIn: 'root',
})
export class AuthenticationService {

  private authenticatedUser!: User | null;

  private readonly authUrl: string = 'http://localhost:8080/auth';

  constructor(private http: HttpClient, private router: Router, private storageService: StorageService) { }

  // Perform pre-authentication checks for the front-end
  checkIfAuthenticated() {
    // Skip if already authenticated
    if (this.authenticatedUser !== undefined)
      return

    // Set the authenticatedUser from session storage if it exists.
    if (this.storageService.getUser() !== null) {
      this.authenticatedUser = this.storageService.getUser()
      return
    }

    // Perform an authentication check with the server
    this.http.get(`${this.authUrl}/user`).subscribe((data: any) => {
      console.log(data)
      if (data !== null) {
        // If the user is authenticated, a Principal is received and the authenticated user is set for the session.
        if (data.principal) {
          this.authenticatedUser = new User()
          this.authenticatedUser.setFromJson(data.principal)

          this.storageService.setUser(this.authenticatedUser)
        }
      }
    });
  }

  // Authenticate with the back-end and set session authentication
  authenticate(username: string, password: string) {
    return this.http
      .post(`${this.authUrl}/login`, { username: username, password: password })
      .subscribe((response: any) => {
        // Instantiate a new User and set variables from the JSON response
        let user = new User()
        user.setFromJson(response)

        // Remove the user data from the session
        this.authenticatedUser = user
        this.storageService.setUser(user)

        // Redirect to the home page
        this.router.navigate(['/'])
      });
  }

  // Deauthenticate and clear session storage.
  deauthenticate() {
    return this.http.post(`${this.authUrl}/revoke`, {}).subscribe((response) => {
      // Remove the user data from the session
      this.authenticatedUser = null
      this.storageService.clear()

      // Redirect to the home page
      this.router.navigate(['/'])
    });
  }

  // Register a new user account
  register(registerRequest: any) {
    console.log(registerRequest)
    return this.http.post(`${this.authUrl}/register`, registerRequest).subscribe((response) => {
      console.log(response)
    });
  }

  getAuthenticatedUser(): User | null {
    return this.authenticatedUser
  }

  isAuthenticated(): boolean {
    return this.authenticatedUser != null
  }

}
