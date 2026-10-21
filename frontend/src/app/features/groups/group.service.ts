import { inject, Injectable } from '@angular/core';
import { Group, GroupInvitation, GroupMember } from './group.types';
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

  loadGroupInvitations(groupId: number, parameters: Record<string, unknown>) {
    return this.fetchApi.fetchPaginatedData<GroupInvitation>(`groups/${groupId}/invitations`, {
      parameters: {
        ...parameters,
      },
    });
  }

  cancelInvitation(groupId: number, invitationId: number): Observable<void> {
    return this.fetchApi.fetch<void>(`groups/${groupId}/invitations/${invitationId}/cancel`, {
      method: 'POST',
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

  inviteMember(groupId: number, usernameOrEmail: string): Observable<void> {
    return this.fetchApi.fetch<void>(`groups/${groupId}/invitations`, {
      method: 'POST',
      requestBody: {
        usernameOrEmail,
      },
    });
  }

  respondToInvitation(groupId: number, invitationId: string, accept: boolean): Observable<void> {
    return this.fetchApi.fetch<void>(`groups/${groupId}/invitations/${invitationId}/${accept}`, {
      method: 'POST',
    });
  }

  removeMember(groupId: number, memberId: number): Observable<void> {
    return this.fetchApi.fetch<void>(`groups/${groupId}/members/${memberId}`, {
      method: 'DELETE',
    });
  }
}
