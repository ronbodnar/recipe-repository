import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ProfileFormModel, ProfileForm } from './profile.types';

const nameValidators = [
  Validators.required,
  Validators.minLength(2),
  Validators.pattern(/^[a-zA-Z\s'-]+$/),
];

export class ProfileFactory {
  static profile(v?: ProfileFormModel): ProfileForm {
    return new FormGroup({
      profileImage: new FormControl<File | null>(null),
      profileImageId: new FormControl<string | null>(v?.profileImageId ?? null),
      displayName: new FormControl(v?.displayName ?? '', {
        nonNullable: true,
        validators: nameValidators,
      }),
      email: new FormControl(v?.email ?? '', {
        nonNullable: true,
        validators: [
          Validators.required,
          Validators.email,
          Validators.pattern(/^[^\s@]+@[^\s@]+\.[^\s@]+$/),
        ],
      }),
      givenName: new FormControl(v?.givenName ?? '', {
        nonNullable: true,
        validators: nameValidators,
      }),
      familyName: new FormControl(v?.familyName ?? '', {
        nonNullable: true,
        validators: nameValidators,
      }),
    });
  }
}
