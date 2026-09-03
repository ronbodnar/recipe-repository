import { Component, signal, inject, effect, ChangeDetectionStrategy } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ErrorService } from '@core/errors/error.service';
import { ApiError } from '@core/models/api-error.model';
import { AuthenticationService } from '@core/services/authentication.service';
import { FetchApiService } from '@core/services/fetch-api.service';
import { ImageService } from '@core/services/image.service';
import { InputTextComponent } from '@ng-modular-forms/core';
import { ButtonComponent } from '@shared/ui/button/button.component';
import {
  ImageSelectorComponent,
  ImageSelectorExistingImage,
  ImageSelectorRemovedImage,
} from '@shared/ui/image-selector/image-selector.component';
import { ProfileFactory } from './profile.factory';
import { ProfileFormModel } from './profile.types';

@Component({
  selector: 'app-settings-profile',
  imports: [ReactiveFormsModule, InputTextComponent, ButtonComponent, ImageSelectorComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './profile.component.html',
})
export class SettingsProfileComponent {
  private readonly fetchApi = inject(FetchApiService);
  private readonly imageService = inject(ImageService);
  private readonly errorService = inject(ErrorService);
  private readonly authService = inject(AuthenticationService);

  private _status = signal<'idle' | 'submitting' | 'success' | 'error'>('idle');

  readonly status = this._status.asReadonly();

  form = signal(ProfileFactory.profile(this.authService.authUser() ?? undefined));

  constructor() {
    effect(() => {
      const user = this.authService.authUser();

      if (!user) {
        console.error('No authenticated user found. Cannot populate profile form.');
        return;
      }

      this.form.set(ProfileFactory.profile(user));
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

    const request = this.form().getRawValue();

    this._status.set('submitting');

    if (request.profileImage) {
      this.uploadProfileImageAndUpdate(request);
    } else {
      this.updateProfile(request);
    }
  }

  onProfileImagesSelected(images: File[]): void {
    const file = images[0];
    if (!file || !file.type.startsWith('image/')) {
      return;
    }

    this.form().patchValue({ profileImage: file, profileImageId: null });
    this.form().markAsDirty();
  }

  getExistingProfileImages(): ImageSelectorExistingImage[] {
    const imageId = this.form().controls.profileImageId.value;
    return imageId
      ? [
          {
            id: imageId,
            src: this.imageService.getImageUrl(imageId, 'PROFILE'),
            alt: `Profile image`,
          },
        ]
      : [];
  }

  onProfileImageRemoved(_: ImageSelectorRemovedImage): void {
    this.form().patchValue({ profileImage: null, profileImageId: null });
    this.form().markAsDirty();
  }

  private uploadProfileImageAndUpdate(formData: ProfileFormModel) {
    this.imageService
      .uploadImages(formData.profileImage ? [formData.profileImage] : [], 'PROFILE')
      .subscribe({
        next: (uploadedImageIds) => {
          if (uploadedImageIds.length > 0) {
            formData.profileImageId = uploadedImageIds[0];
          }
          this.updateProfile(formData);
        },
        error: (error: ApiError) => {
          console.error('Error uploading profile image:', error);
          this._status.set('error');
        },
      });
  }

  private updateProfile(formData: ProfileFormModel) {
    this.fetchApi.putData('identity/me', formData).subscribe({
      next: () => {
        this.authService.update({
          ...this.authService.authUser()!,
          displayName: formData.displayName!,
          email: formData.email!,
          givenName: formData.givenName!,
          familyName: formData.familyName!,
          profileImageId: formData.profileImageId ?? null,
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
}
