import { Injectable } from '@angular/core';
import { UserAccount } from '@features/users/user.types';

type PreferenceKey = 'theme' | 'language' | 'sidenav.expandedSections' | 'sidenav.isOpen';

@Injectable({
  providedIn: 'root',
})
export class StorageService {
  constructor() {}

  /**
   * Stores preferences in localStorage. Preference values are converted to JSON strings and are later retrieved as the original type.
   * Do not use JSON.stringify on the value before passing it to this method, as it will be stringified again.
   *
   * @param key The preference key to store the value under.
   * @param value The value to store. Can be of any type. JSON.stringify is used to convert the value to a string for storage.
   */
  public setPreference<T>(key: PreferenceKey, value: T): void {
    window.localStorage.setItem(key, JSON.stringify(value));
  }

  /**
   * Retrieves preferences from localStorage. Preferences are returned as the original type they were stored as.
   *
   * @param key The preference key to retrieve the value for.
   * @returns The value stored under the preference key, or null if the preference does not exist.
   */
  public getPreference<T>(key: PreferenceKey): T | null {
    const item = window.localStorage.getItem(key);
    return item ? (JSON.parse(item) as T) : null;
  }

  /**
   * Removes a preference from localStorage.
   *
   * @param key The preference key to remove.
   */
  public removePreference(key: PreferenceKey): void {
    window.localStorage.removeItem(key);
  }

  /**
   * Sets the user account in sessionStorage. The UserAccount object is stored as a JSON string and can be retrieved later using getUserAccount().
   *
   * @param user The UserAccount object to store in sessionStorage.
   */
  public setUserAccount(user: UserAccount): void {
    window.sessionStorage.setItem('user', JSON.stringify(user));
  }

  /**
   * Retrieves the user account from sessionStorage and returns it as a UserAccount object.
   *
   * @returns The UserAccount object stored in sessionStorage, or null if no user account is stored.
   */
  public getUserAccount(): UserAccount | null {
    const userJson = window.sessionStorage.getItem('user');

    if (userJson) {
      const user = Object.assign({}, JSON.parse(userJson));
      return user;
    }
    return null;
  }

  /**
   * Clears all data from localStorage and sessionStorage.
   */
  public clear(): void {
    window.localStorage.clear();
    window.sessionStorage.clear();
  }
}
