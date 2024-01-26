import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { User } from './user';
import { Observable } from 'rxjs';

@Injectable()
export class UserService {

  private readonly url: string;

  constructor(private http: HttpClient) {
    this.url = 'http://localhost:8080/users';
  }

  public findAll(): Observable<User[]> {
    return this.http.get<User[]>(this.url);
  }

  public remove(user: User): Observable<User> {
    return this.http.delete<User>(`${this.url}/${user.id}`);
  }

  public save(user: User): Observable<User> {
    return this.http.post<User>(this.url, user);
  }
}
