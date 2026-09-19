import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { FluidContainerComponent } from '@shared/ui/fluid-container/fluid-container.component';
import { MatInputTextComponent, MatInputTextareaComponent } from '@ng-modular-forms/material';
import { ButtonComponent } from '@shared/ui/button/button.component';
import { Group } from '../group.types';
import { TranslatePipe } from '@ngx-translate/core';
import { GroupService } from '../group.service';
import { ActivatedRoute, Router } from '@angular/router';
import { logDebug, logError } from '@shared/utils/logging';
import { FullPageLoaderComponent } from '@shared/ui/full-page-loader.component';
import { GroupNotFoundComponent } from '../components/group-not-found/group-not-found.component';
import { SnackbarService, SnackbarType } from '@shared/ui/snackbar.component';

@Component({
  selector: 'app-edit-group',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FluidContainerComponent,
    MatInputTextComponent,
    MatInputTextareaComponent,
    ButtonComponent,
    TranslatePipe,
    FullPageLoaderComponent,
    GroupNotFoundComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './edit-group.component.html',
})
export class EditGroupComponent {
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly snackbar = inject(SnackbarService);
  private readonly groupService = inject(GroupService);

  private _loading = signal(true);
  private _loadedGroup = signal<Group | null>(null);
  private _validated = signal(false);
  private _status = signal<'idle' | 'submitting' | 'submitFailed' | 'notFound' | 'loadFailed'>(
    'idle',
  );

  readonly loadedGroup = this._loadedGroup.asReadonly();
  readonly validated = this._validated.asReadonly();
  readonly status = this._status.asReadonly();
  readonly loading = this._loading.asReadonly();

  form = new FormGroup({
    name: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.maxLength(255)],
    }),
    description: new FormControl('', {
      nonNullable: true,
      validators: [Validators.maxLength(500)],
    }),
  });

  constructor() {
    const groupId = this.route.snapshot.paramMap.get('id');
    const state = this.router.currentNavigation()?.extras.state as { group?: Group } | undefined;

    if (state?.group) {
      logDebug('Using provided group from route state:', state);
      this._loadedGroup.set(state.group);
      this.form.setValue({
        name: state.group.name,
        description: state.group.description,
      });
      this._loading.set(false);
      return;
    }

    if (groupId && groupId !== 'new') {
      this.loadGroup(Number(groupId));
    } else {
      if (!groupId || Number.isNaN(Number(groupId))) {
        logError(`Invalid group ID: ${groupId}`);
      }
      this._loading.set(false);
    }
  }

  submit(): void {
    this._validated.set(true);

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this._status.set('submitting');

    const group = this.form.getRawValue() as Group;

    this.groupService.saveGroup(group, this.loadedGroup()?.id).subscribe({
      next: (group) => {
        this._status.set('idle');
        console.log('Group saved successfully:', group);
      },
      error: (err) => {
        this._status.set('submitFailed');
        console.error('Failed to save group:', err);
      },
    });

    console.log('Form submitted:', this.form.getRawValue());
  }

  goBack(): void {
    if (this.loadedGroup()) {
      this.router.navigate(['/app/groups/details', this.loadedGroup()!.id], {
        state: { group: this.loadedGroup() },
      });
    } else {
      this.router.navigate(['/app/groups/list']);
    }
  }

  private loadGroup(groupId: number): void {
    this.groupService.loadGroup(groupId).subscribe({
      next: (group) => {
        this._loadedGroup.set(group);
        this.form.setValue({
          name: group.name,
          description: group.description,
        });
        this._loading.set(false);
      },
      error: (error) => {
        console.error('Failed to load group:', error);
        switch (error.status) {
          case 403:
            logDebug(
              'Current user is not authorized to edit this group, redirecting to my groups.',
            );
            this.router.navigate([`/app/groups/list`]);
            setTimeout(() =>
              this.snackbar.openSnackBar(SnackbarType.ERROR, 'errors.notAuthorized'),
            );
            return;

          case 400:
          case 404:
            this._status.set('notFound');
            this._loading.set(false);
            return;

          default:
            logDebug('An unexpected error occurred while loading the group:', error);
            this._loading.set(false);
            this._status.set('loadFailed');
            return;
        }
      },
    });
  }
}
