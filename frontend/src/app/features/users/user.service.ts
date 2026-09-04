import { inject, Injectable } from '@angular/core';
import { FetchApiService } from '@core/services/fetch-api.service';
import { UserAccountSummary } from './user.types';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private readonly fetchApi = inject(FetchApiService);

  loadUserSummary(subject: string) {
    return this.fetchApi.getData<UserAccountSummary>(`identity/users/${subject}/summary`);
  }
}
