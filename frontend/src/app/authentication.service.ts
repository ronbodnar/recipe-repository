import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { of, catchError } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthenticationService {
  private readonly authUrl: string;

  constructor(private http: HttpClient) {
    this.authUrl = 'http://localhost:8080/auth/login';
  }

  authenticate(username: string, password: string) {
    return this.http
      .post(`${this.authUrl}`, { username: username, password: password })
      .subscribe((response: any) => {
        if (response.message !== undefined) {
          console.log(response.message)
          return
        }
        console.log('User matched:');
        console.log(response);
      });
  }
}