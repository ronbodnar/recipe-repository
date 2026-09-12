import { FormControl, FormGroup } from '@angular/forms';

export type ProfileForm = FormGroup<{
  username: FormControl<string>;
  email: FormControl<string>;
  givenName: FormControl<string | null>;
  familyName: FormControl<string | null>;
  profileImage: FormControl<File | null>;
  profileImageId: FormControl<string | null>;
}>;

export type ProfileFormModel = {
  username: string;
  email: string;
  givenName: string | null;
  familyName: string | null;
  profileImage?: File | null;
  profileImageId?: string | null;
};

export type ProfileFormRequest = {
  username: string;
  email: string;
  givenName: string | null;
  familyName: string | null;
  profileImageId?: string | null;
};
