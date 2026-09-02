import { Component, signal, inject, effect } from '@angular/core';
import { FormGroup, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { ErrorService } from '@core/errors/error.service';
import { ApiError } from '@core/models/api-error.model';
import { AuthenticationService } from '@core/services/authentication.service';
import { FetchApiService } from '@core/services/fetch-api.service';
import { InputTextComponent } from '@ng-modular-forms/core';
import { ButtonComponent } from '@shared/ui/button/button.component';
import {
  ImageSelectorComponent,
  ImageSelectorExistingImage,
  ImageSelectorRemovedImage,
} from '@shared/ui/image-selector/image-selector.component';

@Component({
  selector: 'app-settings-profile',
  imports: [ReactiveFormsModule, InputTextComponent, ButtonComponent, ImageSelectorComponent],
  templateUrl: './profile.component.html',
})
export class SettingsProfileComponent {
  private readonly fetchApi = inject(FetchApiService);
  private readonly errorService = inject(ErrorService);
  private readonly authService = inject(AuthenticationService);

  private _status = signal<'idle' | 'submitting' | 'success' | 'error'>('idle');

  readonly status = this._status.asReadonly();

  private readonly nameValidators = [
    Validators.required,
    Validators.minLength(2),
    Validators.pattern(/^[a-zA-Z\s'-]+$/),
  ];

  form = signal(
    new FormGroup({
      profileImage: new FormControl<File | null>(null),
      existingProfileImage: new FormControl<string | null>(
        this.authService.authUser()?.profileImageId ?? null,
      ),
      displayName: new FormControl(this.authService.authUser()?.displayName ?? '', {
        validators: this.nameValidators,
      }),
      email: new FormControl<string>(this.authService.authUser()?.email ?? '', {
        validators: [
          Validators.required,
          Validators.email,
          Validators.pattern(/^[^\s@]+@[^\s@]+\.[^\s@]+$/),
        ],
      }),
      givenName: new FormControl(this.authService.authUser()?.givenName ?? '', {
        validators: this.nameValidators,
      }),
      familyName: new FormControl(this.authService.authUser()?.familyName ?? '', {
        validators: this.nameValidators,
      }),
    }),
  );

  constructor() {
    effect(() => {
      const user = this.authService.authUser();

      if (!user) {
        console.error('No authenticated user found. Cannot populate profile form.');
        return;
      }

      this.form().patchValue({
        displayName: user.displayName,
        email: user.email,
        givenName: user.givenName,
        familyName: user.familyName,
      });
    });

    this.form().valueChanges.subscribe(() => {
      if (this._status() === 'error') {
        this._status.set('idle');
      }
    });
  }

  submit() {
    if (this.form().invalid) {
      this.form().markAllAsTouched();
      return;
    }

    const request = this.form().value;

    const formData = new FormData();
    if (request.profileImage) {
      formData.append('profileImage', request.profileImage);
    }

    formData.append(
      'profile',
      new Blob(
        [
          JSON.stringify({
            ...request,
            profileImage: undefined,
          }),
        ],
        { type: 'application/json' },
      ),
    );

    this._status.set('submitting');

    this.fetchApi.putData('identity/me', formData).subscribe({
      next: (response) => {
        console.log('Profile updated successfully:', response);
        this.authService.update({
          ...this.authService.authUser()!,
          displayName: request.displayName!,
          email: request.email!,
          givenName: request.givenName!,
          familyName: request.familyName!,
        });
        this._status.set('success');
        setTimeout(() => this._status.set('idle'), 3000);
        this.form().markAsPristine();
      },
      error: (error: ApiError) => {
        console.error('Error updating profile:', error);
        this._status.set('error');
        if (error.hasFormError()) {
          this.errorService.populateFormErrors(this.form(), error);
        }
      },
    });
  }

  onProfileImagesSelected(images: File[]): void {
    const file = images[0];
    if (!file || !file.type.startsWith('image/')) {
      return;
    }

    this.form().patchValue({ profileImage: file, existingProfileImage: null });
    this.form().markAsDirty();
  }

  getExistingProfileImages(): ImageSelectorExistingImage[] {
    const imageId = this.form().controls.existingProfileImage.value;
    return imageId ? [{ id: imageId }] : [];
  }

  onProfileImageRemoved(_: ImageSelectorRemovedImage): void {
    this.form().patchValue({ profileImage: null, existingProfileImage: null });
    this.form().markAsDirty();
  }
}
