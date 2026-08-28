import { Injectable } from '@angular/core';
import { SecurityUser } from '../../features/users/interfaces/security-user.interface';

@Injectable({
  providedIn: 'root',
})
export class StorageService {
  constructor() {}

  public setUser(user: SecurityUser): void {
    window.sessionStorage.setItem('user', JSON.stringify(user));
  }

  public getUser(): SecurityUser | null {
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
