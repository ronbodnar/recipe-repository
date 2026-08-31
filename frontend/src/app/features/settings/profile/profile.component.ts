import { Component, signal, inject } from '@angular/core';
import { FormGroup, FormControl, ReactiveFormsModule } from '@angular/forms';
import { AuthenticationService } from '@core/services/authentication.service';
import { FetchApiService } from '@core/services/fetch-api.service';

@Component({
  selector: 'app-settings-profile',
  imports: [ReactiveFormsModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css',
})
export class SettingsProfileComponent {
  private readonly fetchApi = inject(FetchApiService);
  private readonly authService = inject(AuthenticationService);

  form = signal(
    new FormGroup({
      username: new FormControl(this.authService.authUser()?.username ?? ''),
      email: new FormControl(this.authService.authUser()?.email ?? ''),
      givenName: new FormControl(this.authService.authUser()?.givenName ?? ''),
      familyName: new FormControl(this.authService.authUser()?.familyName ?? ''),
    }),
  );

  submit() {
    const formValue = this.form().value;

    const request = {
      username: formValue.username,
      email: formValue.email,
      firstName: formValue.givenName,
      lastName: formValue.familyName,
    };

    this.fetchApi.putData('identity/me', request).subscribe({
      next: (response) => {
        console.log('Profile updated successfully:', response);
      },
      error: (error) => {
        console.error('Error updating profile:', error);
      },
    });
  }
}
