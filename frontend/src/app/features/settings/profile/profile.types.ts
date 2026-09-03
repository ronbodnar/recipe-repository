import { FormControl, FormGroup } from '@angular/forms';

export type ProfileForm = FormGroup<{
  displayName: FormControl<string>;
  email: FormControl<string>;
  givenName: FormControl<string>;
  familyName: FormControl<string>;
  profileImage: FormControl<File | null>;
  profileImageId: FormControl<string | null>;
}>;

export type ProfileFormModel = {
  displayName: string;
  email: string;
  givenName: string;
  familyName: string;
  profileImage?: File | null;
  profileImageId?: string | null;
};

export type ProfileFormRequest = {
  displayName: string;
  email: string;
  givenName: string;
  familyName: string;
  profileImageId?: string | null;
};
