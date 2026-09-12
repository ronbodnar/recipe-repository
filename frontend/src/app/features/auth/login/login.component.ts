import { Component, computed, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { Validators } from '@angular/forms';
import { catchError } from 'rxjs';
import { MatFormFieldModule } from '@angular/material/form-field';
import { ErrorService } from '@core/errors/error.service';
import { ApiError } from '@core/models/api-error.model';
import { AuthenticationService } from '@core/services/authentication.service';
import { ButtonComponent } from '@shared/ui/button/button.component';
import {
  FormHydrator,
  FormOrchestrator,
  FormSerializer,
  InputTextComponent,
} from '@ng-modular-forms/core';
import { TranslatePipe } from '@ngx-translate/core';
import { SidenavComponent } from '@features/navigation/sidenav/sidenav.component';

@Component({
  selector: 'app-user-login',
  imports: [
    RouterLink,
    ReactiveFormsModule,
    ButtonComponent,
    MatFormFieldModule,
    TranslatePipe,
    InputTextComponent,
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class UserLoginComponent extends FormOrchestrator {
  private router = inject(Router);
  private formBuilder = inject(FormBuilder);
  private errorService = inject(ErrorService);
  private authService = inject(AuthenticationService);
  private sidenav = inject(SidenavComponent);

  isSubmitting = computed(() => this.status() === 'submitting');

  constructor(hydrator: FormHydrator, serializer: FormSerializer) {
    super(hydrator, serializer);

    this.orchestrate({
      form: this.formBuilder.group({
        username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(30)]],
        password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(255)]],
      }),
    });
  }

  onSubmit() {
    const form = this.form();
    if (!form.valid) {
      form.markAllAsTouched();
      return;
    }

    const { username, password } = form.value;

    this.setStatus('submitting');

    this.authService
      .login(username, password)
      .pipe(
        catchError((error: ApiError) => {
          this.setStatus('error');
          return this.errorService.populateFormErrors(this.form(), error);
        }),
      )
      .subscribe(() =>
        this.router.navigateByUrl('/').then(() => {
          this.sidenav.open();
        }),
      );
  }
}
