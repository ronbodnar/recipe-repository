import { Component, inject, signal } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { GroupService } from '../group.service';
import { Group, GroupMember } from '../group.types';
import { FluidContainerComponent } from '@shared/ui/fluid-container/fluid-container.component';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { FullPageLoaderComponent } from '@shared/ui/full-page-loader.component';
import { MatIconModule } from '@angular/material/icon';
import { DatePipe } from '@angular/common';
import { ImageService } from '@core/services/image.service';
import { PaginatedTableComponent } from '@shared/ui/paginated-table/paginated-table.component';
import { PaginatedTableConfig } from '@shared/ui/paginated-table/paginated-table.config';
import { catchError, tap } from 'rxjs';
import { logError } from '@shared/utils/logging';
import { TableUsernameAvatarComponent } from '@shared/ui/paginated-table/components/table-username-avatar/table-username-avatar.component';
import { GroupNotFoundComponent } from '../components/group-not-found/group-not-found.component';

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

  private _group = signal<Group | null>(null);
  private _members = signal<GroupMember[]>([]);
  private _loading = signal(true);

  readonly group = this._group.asReadonly();
  readonly members = this._members.asReadonly();
  readonly loading = this._loading.asReadonly();

  readonly tableConfig = signal<PaginatedTableConfig<GroupMember>>({
    elevation: 4,
    pagination: true,
    filterable: true,
    orderable: true,
    defaultSort: { property: 'userAccount.username', direction: 'asc' },
    fetchData: (params: Record<string, unknown>) =>
      this.groupService.loadGroupMembers(this._group()!.id, params).pipe(
        tap((data) => {
          console.log('Fetched group members:', data);
        }),
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
    ],
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

  goBack(): void {
    this.router.navigate(['/app/groups/list']);
  }

  getProfileImage(profileImageId: string): string {
    return this.imageService.getImageUrl(profileImageId, 'PROFILE');
  }
}
