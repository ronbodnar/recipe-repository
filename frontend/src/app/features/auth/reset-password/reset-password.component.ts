import { Component, signal, computed, inject } from '@angular/core';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { InputTextComponent } from '@ng-modular-forms/core';
import { TranslatePipe } from '@ngx-translate/core';
import { ButtonComponent } from '@shared/ui/button/button.component';
import { SnackbarService, SnackbarType } from '@shared/ui/snackbar.component';
import { passwordMatchValidator } from '../password-match.validator';
import { AuthService } from '../auth.service';
import { ApiError } from '@core/models/api-error.model';
import { ErrorCode } from '@core/interfaces/error-code.interface';

@Component({
  selector: 'app-reset-password',
  imports: [InputTextComponent, ButtonComponent, ReactiveFormsModule, TranslatePipe, RouterLink],
  templateUrl: './reset-password.component.html',
  styleUrl: './reset-password.component.css',
})
export class ResetPasswordComponent {
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly authService = inject(AuthService);
  private readonly snackbar = inject(SnackbarService);

  private token: string;

  status = signal<'idle' | 'submitting' | 'success'>('idle');

  isSubmitting = computed(() => this.status() === 'submitting');

  form = new FormGroup(
    {
      password: new FormControl('', {
        nonNullable: true,
        validators: [Validators.required, Validators.minLength(8), Validators.maxLength(255)],
      }),
      confirmPassword: new FormControl('', {
        nonNullable: true,
        validators: [Validators.required, Validators.minLength(8), Validators.maxLength(255)],
      }),
    },
    { validators: passwordMatchValidator, updateOn: 'change' },
  );

  constructor() {
    this.token = this.route.snapshot.queryParamMap.get('token') ?? '';

    if (!this.token) {
      this.router.navigate(['/app/auth/forgot-password']);
      this.snackbar.openSnackBar(SnackbarType.ERROR, 'auth.resetPassword.missingToken');
    }
  }

  onSubmit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.status.set('submitting');

    const password = this.form.getRawValue().password;

    this.authService.resetPassword(this.token, password).subscribe({
      next: () => {
        this.status.set('success');
        this.snackbar.openSnackBar(SnackbarType.SUCCESS, 'auth.resetPassword.success');
        this.router.navigate(['/app/auth/login']);
      },
      error: (error: ApiError) => {
        if (error.code === ErrorCode.INVALID_PASSWORD_RESET_TOKEN) {
          this.snackbar.openSnackBar(SnackbarType.ERROR, 'auth.resetPassword.invalidToken');
          this.router.navigate(['/app/auth/forgot-password']);
        }
        this.status.set('idle');
      },
    });
  }
}
