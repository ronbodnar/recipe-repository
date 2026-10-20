import { Component, computed, inject, signal } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { GroupService } from '../group.service';
import { Group, GroupInvitation, GroupMember } from '../group.types';
import { FluidContainerComponent } from '@shared/ui/fluid-container/fluid-container.component';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { FullPageLoaderComponent } from '@shared/ui/full-page-loader.component';
import { MatIconModule } from '@angular/material/icon';
import { DatePipe } from '@angular/common';
import { ImageService } from '@core/services/image.service';
import { PaginatedTableComponent } from '@shared/ui/paginated-table/paginated-table.component';
import {
  PaginatedTableConfig,
  TableColumn,
} from '@shared/ui/paginated-table/paginated-table.config';
import { catchError, tap } from 'rxjs';
import { logError } from '@shared/utils/logging';
import { TableUsernameAvatarComponent } from '@shared/ui/paginated-table/components/table-username-avatar/table-username-avatar.component';
import { GroupNotFoundComponent } from '../components/group-not-found/group-not-found.component';
import { ButtonComponent } from '@shared/ui/button/button.component';
import { AuthService } from '@features/auth/auth.service';
import { DialogService } from '@shared/ui/dialog/dialog.service';
import { SnackbarService, SnackbarType } from '@shared/ui/snackbar.component';
import {
  DELETE,
  RowAction,
  TableRowActionsComponent,
} from '@shared/ui/paginated-table/components/table-row-actions/table-row-actions.component';

@Component({
  selector: 'app-group-details',
  imports: [
    FluidContainerComponent,
    TranslatePipe,
    DatePipe,
    FullPageLoaderComponent,
    MatIconModule,
    PaginatedTableComponent,
    GroupNotFoundComponent,
    ButtonComponent,
  ],
  templateUrl: './group-details.component.html',
  styleUrl: './group-details.component.css',
})
export class GroupDetailsComponent {
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private imageService = inject(ImageService);
  private groupService = inject(GroupService);
  private translate = inject(TranslateService);
  private authService = inject(AuthService);
  private dialogService = inject(DialogService);
  private snackbarService = inject(SnackbarService);

  private _group = signal<Group | null>(null);
  private _members = signal<GroupMember[]>([]);
  private _loading = signal(true);
  private _refreshMemberTable = signal(0);
  private _refreshInvitationTable = signal(0);

  readonly group = this._group.asReadonly();
  readonly members = this._members.asReadonly();
  readonly loading = this._loading.asReadonly();

  readonly isGroupOwner = computed(() => {
    const group = this._group();
    const currentUser = this.authService.authUser();
    if (!group || !currentUser) {
      return false;
    }

    return group.ownerId === currentUser?.id;
  });

  readonly tableConfig = computed<PaginatedTableConfig<GroupMember>>(() => {
    this._refreshMemberTable();

    return {
      elevation: 4,
      pagination: true,
      filterable: true,
      orderable: true,
      defaultSort: { property: 'userAccount.username', direction: 'asc' },
      fetchData: (params: Record<string, unknown>) =>
        this.groupService.loadGroupMembers(this._group()!.id, params).pipe(
          tap((members) => console.log('Loaded members', members)),
          catchError((error) => {
            console.log('Error loading groups:', error);
            throw error;
          }),
        ),
      columns: [
        {
          title: 'Username',
          property: 'username',
          propertyPath: 'userAccount.username',
          angularComponent: TableUsernameAvatarComponent,
          angularComponentData: (member: GroupMember) => ({
            input: {
              username: member.username,
              profileImageUrl: member.profileImageId
                ? this.getProfileImage(member.profileImageId)
                : undefined,
            },
          }),
        },
        {
          title: 'Full Name',
          property: 'fullName',
          propertyPath: 'userAccount.givenName',
          transformValue: (value: string) => (value == null || value === '' ? '-' : value),
        },
        {
          title: 'Role',
          property: 'role',
          transformValue: (value: string) =>
            this.translate.instant(`groups.roles.${value.toLowerCase()}`),
        },
        {
          title: 'Member Since',
          property: 'joinedAt',
          filterable: false,
          transformValue: (value: string) => new Date(value).toLocaleDateString(),
        },
        {
          title: 'Actions',
          property: 'actions',
          filterable: false,
          orderable: false,
          visibile: this.isGroupOwner(),
          className: 'text-center',
          width: '10%',
          angularComponent: TableRowActionsComponent,
          angularComponentData: (row) => ({
            input: {
              rowData: row,
              actions: [
                {
                  ...DELETE,
                  icon: 'person_remove',
                  disabledFn: () => row.userId === this._group()!.ownerId,
                  onClick: () => this.promptConfirmRemoveMember(row.id),
                },
              ] as RowAction[],
            },
          }),
        },
      ] as TableColumn<GroupMember>[],
    };
  });

  readonly invitationTableConfig = computed<PaginatedTableConfig<GroupInvitation>>(() => {
    this._refreshInvitationTable();

    return {
      elevation: 4,
      pagination: true,
      filterable: true,
      orderable: true,
      defaultSort: { property: 'invitedUser.username', direction: 'asc' },
      fetchData: (params: Record<string, unknown>) =>
        this.groupService.loadGroupInvitations(this._group()!.id, params).pipe(
          tap((members) => console.log('Loaded invitations', members)),
          catchError((error) => {
            console.log('Error loading groups:', error);
            throw error;
          }),
        ),
      columns: [
        {
          title: 'Username',
          property: 'invitedUsername',
          propertyPath: 'invitedUser.username',
          angularComponent: TableUsernameAvatarComponent,
          angularComponentData: (invite: GroupInvitation) => ({
            input: {
              username: invite.invitedUsername,
              profileImageUrl: invite.invitedProfileImageId
                ? this.getProfileImage(invite.invitedProfileImageId)
                : undefined,
            },
          }),
        },
        {
          title: 'Invited By',
          property: 'invitedByUsername',
          propertyPath: 'invitedBy.username',
          angularComponent: TableUsernameAvatarComponent,
          angularComponentData: (invite: GroupInvitation) => ({
            input: {
              username: invite.invitedByUsername,
              profileImageUrl: invite.invitedByProfileImageId
                ? this.getProfileImage(invite.invitedByProfileImageId)
                : undefined,
            },
          }),
        },
        {
          title: 'Invitation Date',
          property: 'invitedAt',
          filterable: false,
          transformValue: (value: string) => new Date(value).toLocaleDateString(),
        },
        {
          title: 'Actions',
          property: 'actions',
          filterable: false,
          orderable: false,
          visible: this.isGroupOwner(),
          className: 'text-center',
          width: '10%',
          angularComponent: TableRowActionsComponent,
          angularComponentData: (row) => ({
            input: {
              rowData: row,
              actions: [
                {
                  ...DELETE,
                  label: 'Cancel Invitation',
                  tooltipLabel: 'Cancel Invitation',
                  icon: 'person_remove',
                  onClick: () => this.promptConfirmCancelInvitation(row.id),
                },
              ] as RowAction[],
            },
          }),
        },
      ] as TableColumn<GroupInvitation>[],
    };
  });

  constructor() {
    const groupId = this.route.snapshot.paramMap.get('id');
    if (!groupId || Number.isNaN(Number(groupId))) {
      logError(`Invalid group ID: ${groupId}`);
      this._loading.set(false);
      return;
    }

    this.groupService.loadGroup(Number(groupId)).subscribe({
      next: (group) => {
        console.log('Loaded group:', group);
        this._group.set(group);
        this._loading.set(false);
      },
      error: (error) => {
        console.log('Error loading group:', error);
        this._loading.set(false);
      },
    });
  }

  promptInviteMember(): void {
    const dialog = this.dialogService.openInputDialog(
      'groups.details.inviteMember.title',
      'groups.details.inviteMember.message',
      'text',
      {
        submitButtonLabel: 'groups.details.inviteMember.submitButton',
      },
    );
    dialog.afterClosed().subscribe((response) => {
      if (response) {
        this.groupService.inviteMember(this._group()!.id, response).subscribe({
          next: () => {
            this.snackbarService.openSnackBar(
              SnackbarType.SUCCESS,
              'groups.details.inviteMember.success',
            );
            this._refreshInvitationTable.update((v) => v + 1);
          },
          error: () => {
            this.snackbarService.openSnackBar(
              SnackbarType.ERROR,
              'groups.details.inviteMember.error',
            );
          },
        });
      }
    });
  }

  promptDeleteGroup(): void {
    const dialog = this.dialogService.openConfirmationDialog(
      'groups.details.deleteConfirmation.title',
      'groups.details.deleteConfirmation.message',
    );

    dialog.afterClosed().subscribe((response) => {
      if (response) {
        this.deleteGroup();
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/app/groups/list']);
  }

  getProfileImage(profileImageId: string): string {
    return this.imageService.getImageUrl(profileImageId, 'PROFILE');
  }

  private promptConfirmCancelInvitation(invitationId: number): void {
    const dialog = this.dialogService.openConfirmationDialog(
      'groups.details.cancelInvitation.title',
      'groups.details.cancelInvitation.message',
    );

    dialog.afterClosed().subscribe((response) => {
      if (response) {
        this.cancelInvitation(invitationId);
      }
    });
  }

  private promptConfirmRemoveMember(memberId: number): void {
    const dialog = this.dialogService.openConfirmationDialog(
      'groups.details.removeMember.title',
      'groups.details.removeMember.message',
    );

    dialog.afterClosed().subscribe((response) => {
      if (response) {
        this.removeGroupMember(memberId);
      }
    });
  }

  private removeGroupMember(memberId: number): void {
    const group = this._group();
    if (!group) {
      return;
    }

    this._loading.set(true);

    this.groupService.removeMember(group.id, memberId).subscribe({
      next: () => {
        this._loading.set(false);
        this._refreshMemberTable.update((v) => v + 1);
        console.log(`Removed group member with ID: ${memberId}`);
      },
      error: (error) => {
        console.log('Error removing group member:', error);
        this._loading.set(false);
      },
    });
  }

  private deleteGroup(): void {
    const group = this._group();
    if (!group) {
      return;
    }

    this._loading.set(true);

    this.groupService.deleteGroup(group.id).subscribe({
      next: () => {
        this._loading.set(false);
        this.router.navigate(['/app/groups/list']);
      },
      error: (error) => {
        console.log('Error deleting group:', error);
        this._loading.set(false);
      },
    });
  }

  private cancelInvitation(invitationId: number): void {
    const group = this._group();
    if (!group) {
      return;
    }

    this._loading.set(true);

    this.groupService.cancelInvitation(group.id, invitationId).subscribe({
      next: () => {
        this._loading.set(false);
        this._refreshInvitationTable.update((v) => v + 1);
        console.log(`Cancelled invitation with ID: ${invitationId}`);
      },
      error: (error) => {
        console.log('Error cancelling invitation:', error);
        this._loading.set(false);
      },
    });
  }
}
