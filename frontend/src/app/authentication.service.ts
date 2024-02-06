import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthenticationService {

  private authenticated: boolean = false;

  private readonly authUrl: string;

  constructor(private http: HttpClient, private router: Router) {
    this.authUrl = 'http://localhost:8080/auth'
  }

  // Perform pre-authentication checks for the front-end
  checkIfAuthenticated() {
    if (window.sessionStorage.getItem('user') !== null)
      this.authenticated = true

    if (!this.authenticated)
      this.http.get(`${this.authUrl}/user`).subscribe((data: any) => {
        if (data !== null) {
          this.authenticated = data.authenticated

          if (data.principal)
            window.sessionStorage.setItem('user', data.principal)
        }
      });
  }

  // Authenticate with the back-end and set session authentication
  authenticate(username: string, password: string) {
    return this.http
      .post(`${this.authUrl}/login`, { username: username, password: password })
      .subscribe((response: any) => {
        console.log('Auth response:')
        console.log(response)
        window.sessionStorage.setItem('user', JSON.stringify(response))
        this.authenticated = true
        this.router.navigate(['/'])
      });
  }

  // Deauthenticate and clear session storage.
  deauthenticate() {
    return this.http.post(`${this.authUrl}/revoke`, {}).subscribe((response) => {
      console.log('Deauth response:')
      console.log(response)
      this.authenticated = false
      window.sessionStorage.removeItem('user')
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

  // Check if the user is authenticated
  isAuthenticated(): boolean {
    return this.authenticated;
  }
}
