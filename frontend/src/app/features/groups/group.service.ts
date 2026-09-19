import { inject, Injectable } from '@angular/core';
import { Group, GroupMember } from './group.types';
import { FetchApiService } from '@core/services/fetch-api.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class GroupService {
  private readonly fetchApi = inject(FetchApiService);

  loadGroups(parameters: Record<string, unknown>) {
    return this.fetchApi.fetchPaginatedData<Group>(`groups`, {
      parameters: {
        ...parameters,
      },
    });
  }

  loadGroup(groupId: number): Observable<Group> {
    return this.fetchApi.fetch<Group>(`groups/${groupId}`, {
      method: 'GET',
    });
  }

  loadGroupMembers(groupId: number, parameters: Record<string, unknown>) {
    return this.fetchApi.fetchPaginatedData<GroupMember>(`groups/${groupId}/members`, {
      parameters: {
        ...parameters,
      },
    });
  }

  saveGroup(group: Group, groupId?: number): Observable<Group> {
    return this.fetchApi.fetch<Group>(`groups${groupId ? `/${groupId}` : ''}`, {
      method: groupId ? 'PUT' : 'POST',
      requestBody: group,
    });
  }

  deleteGroup(groupId: number): Observable<void> {
    return this.fetchApi.fetch<void>(`groups/${groupId}`, {
      method: 'DELETE',
    });
  }
}
