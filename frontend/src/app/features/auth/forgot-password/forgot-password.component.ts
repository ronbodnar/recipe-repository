import { Component, computed, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { InputTextComponent } from '@ng-modular-forms/core';
import { TranslatePipe } from '@ngx-translate/core';
import { ButtonComponent } from '@shared/ui/button/button.component';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-forgot-password',
  imports: [InputTextComponent, ButtonComponent, ReactiveFormsModule, TranslatePipe, RouterLink],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.css',
})
export class ForgotPasswordComponent {
  private readonly authService = inject(AuthService);

  status = signal<'idle' | 'submitting' | 'success'>('idle');

  isSubmitting = computed(() => this.status() === 'submitting');

  form = new FormGroup({
    usernameOrEmail: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.maxLength(254)],
    }),
  });

  onSubmit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const { usernameOrEmail } = this.form.getRawValue();

    this.status.set('submitting');

    this.authService.requestPasswordReset(usernameOrEmail).subscribe({
      next: () => this.status.set('success'),
      error: () => this.status.set('idle'),
    });
  }
}
