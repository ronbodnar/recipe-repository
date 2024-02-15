import { Injectable } from '@angular/core';
import { User } from './users/user';

@Injectable({
  providedIn: 'root'
})
export class StorageService {

  constructor() { }

  public setUser(user: User) {
    window.localStorage.setItem('user', JSON.stringify(user));
  }

  public getUser(): User | null {
    let userJson = window.localStorage.getItem('user')

    // If the user is present in local storage, instantiate a new User from the JSON data.
    if (userJson) {
      let user = Object.assign(new User(), JSON.parse(userJson))
      return user;
    }
    return null;
  }

  public clear(): void {
    window.localStorage.clear()
  }

  
}
