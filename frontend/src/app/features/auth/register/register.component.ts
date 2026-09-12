import { Component, computed, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { Validators } from '@angular/forms';
import { catchError } from 'rxjs';
import { ErrorService } from '@core/errors/error.service';
import { ApiError } from '@core/models/api-error.model';
import { ButtonComponent } from '@shared/ui/button/button.component';
import { SnackbarService, SnackbarType } from '@shared/ui/snackbar.component';
import {
  FormHydrator,
  FormOrchestrator,
  FormSerializer,
  InputTextComponent,
} from '@ng-modular-forms/core';
import { TranslatePipe } from '@ngx-translate/core';
import { passwordMatchValidator } from '../password-match.validator';
import { AuthenticationService } from '@core/services/authentication.service';

@Component({
  selector: 'app-user-register',
  imports: [RouterLink, ReactiveFormsModule, ButtonComponent, TranslatePipe, InputTextComponent],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class UserRegisterComponent extends FormOrchestrator {
  private router = inject(Router);
  private formBuilder = inject(FormBuilder);
  private errorService = inject(ErrorService);
  private snackBar = inject(SnackbarService);
  private authService = inject(AuthenticationService);

  isSubmitting = computed(() => this.status() === 'submitting');
  passwordMismatch = signal(false);

  constructor(hydrator: FormHydrator, serializer: FormSerializer) {
    super(hydrator, serializer);

    const form = this.formBuilder.group(
      {
        username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(30)]],
        email: ['', [Validators.required, Validators.email, Validators.maxLength(254)]],
        givenName: ['', [Validators.minLength(2), Validators.maxLength(150)]],
        familyName: ['', [Validators.minLength(2), Validators.maxLength(150)]],
        password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(255)]],
        confirmPassword: [
          '',
          [Validators.required, Validators.minLength(8), Validators.maxLength(255)],
        ],
      },
      {
        validators: [passwordMatchValidator],
        updateOn: 'change',
      },
    );

    this.orchestrate({ form });
  }

  onSubmit() {
    const form = this.form();
    if (!form.valid) {
      form.markAllAsTouched();
      return;
    }

    this.setStatus('submitting');

    this.authService
      .register(form.value)
      .pipe(
        catchError((error: ApiError) => {
          this.setStatus('error');
          return this.errorService.populateFormErrors(this.form(), error);
        }),
      )
      .subscribe(() => {
        this.router.navigateByUrl('/app/recipes/list');
        this.snackBar.openSnackBar(SnackbarType.SUCCESS, 'auth.register.successMessage');
      });
  }
}
