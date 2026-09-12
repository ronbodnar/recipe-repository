import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ProfileFormModel, ProfileForm } from './profile.types';

const nameValidators = [
  Validators.minLength(2),
  Validators.maxLength(150),
  Validators.pattern(/^[a-zA-Z\s'-]+$/),
];

export class ProfileFactory {
  static profile(v?: ProfileFormModel): ProfileForm {
    console.log('Profile set from ', v);
    return new FormGroup({
      profileImage: new FormControl<File | null>(null),
      profileImageId: new FormControl<string | null>(v?.profileImageId ?? null),
      username: new FormControl(v?.username ?? '', {
        nonNullable: true,
        validators: [
          Validators.required,
          Validators.minLength(3),
          Validators.maxLength(30),
          Validators.pattern(/^[a-zA-Z0-9._-]+$/),
        ],
      }),
      email: new FormControl(v?.email ?? '', {
        nonNullable: true,
        validators: [
          Validators.required,
          Validators.email,
          Validators.pattern(/^[^\s@]+@[^\s@]+\.[^\s@]+$/),
        ],
      }),
      givenName: new FormControl(v?.givenName ?? null, {
        validators: nameValidators,
      }),
      familyName: new FormControl(v?.familyName ?? null, {
        validators: nameValidators,
      }),
    });
  }
}
