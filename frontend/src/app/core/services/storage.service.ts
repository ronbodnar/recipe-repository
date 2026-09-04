import { Injectable } from '@angular/core';
import { UserAccount } from '@features/users/user.types';

@Injectable({
  providedIn: 'root',
})
export class StorageService {
  constructor() {}

  public setUserAccount(user: UserAccount): void {
    window.sessionStorage.setItem('user', JSON.stringify(user));
  }

  public getUserAccount(): UserAccount | null {
    const userJson = window.sessionStorage.getItem('user');

    if (userJson) {
      const user = Object.assign({}, JSON.parse(userJson));
      return user;
    }
    return null;
  }

  public clear(): void {
    window.sessionStorage.clear();
  }
}
