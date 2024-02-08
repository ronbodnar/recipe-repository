import { Injectable } from '@angular/core';
import { User } from './users/user';

@Injectable({
  providedIn: 'root'
})
export class StorageService {

  constructor() { }

  public setUser(user: User) {
    window.sessionStorage.setItem('user', JSON.stringify(user));
  }

  public getUser(): User | null {
    let userJson = window.sessionStorage.getItem('user')

    // If the user is present in session storage, instantiate a new User from the JSON data.
    if (userJson) {
      let user = Object.assign(new User(), JSON.parse(userJson))
      return user;
    }
    return null;
  }

  public clear(): void {
    window.sessionStorage.clear()
  }

  
}
