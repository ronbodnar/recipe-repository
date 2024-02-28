import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import { User } from '../../../../core/models/user';
import { FormBuilder, FormControl, ReactiveFormsModule } from '@angular/forms';
import { Validators } from '@angular/forms';
import { AuthenticationService } from '../../../../core/services/authentication.service';
import { EMPTY, Observable } from 'rxjs';

@Component({
  selector: 'app-create-account',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent {
  user!: User;

  showPassword: boolean = false;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private authService: AuthenticationService
  ) {
    this.user = new User();
    this.authService.setRegisterComponent(this);
  }

  firstName = new FormControl('', [Validators.required]);
  lastName = new FormControl('', [Validators.required]);
  username = new FormControl('', [Validators.required]);
  email = new FormControl('', [Validators.required]);
  password = new FormControl('', [Validators.required]);
  confirmPassword = new FormControl('', [Validators.required]);

  registrationForm = this.formBuilder.group({
    firstName: this.firstName,
    lastName: this.lastName,
    username: this.user,
    email: this.email,
    password: this.password,
    confirmPassword: this.confirmPassword,
  });

  onSubmit() {
    // Clear form validation
    Array.from(document.getElementsByClassName('form-control')).forEach((div) =>
      div.classList.remove('is-invalid')
    );

    // Disable the Register button and display the loading spinner
    let registerButton = document.querySelector('#registerButton');
    let loadingSpinner = document.querySelector('#loadingSpinner');
    registerButton?.setAttribute('disabled', '');
    loadingSpinner?.removeAttribute('hidden');

    if (!this.registrationForm.valid) {
      this.validate();
      return;
    }

    this.authService.register(this.registrationForm.value!).subscribe(() => {
      this.router.navigate(['/users/login'], { skipLocationChange: true, queryParams: { registered: true } })
    });

    registerButton?.removeAttribute('disabled');
    loadingSpinner?.setAttribute('hidden', '');
  }

  validate() {
    let forms = document.querySelectorAll('.needs-validation');

    Array.prototype.slice.call(forms).forEach((form) => {
      form.checkValidity();

      form.classList.add('was-validated');
    }, false);
  }

  handleErrors(errors: any): Observable<any> {
    errors.forEach((error: any) => {
      let fieldElement = document.getElementById(error.field);
      fieldElement?.classList.add('is-invalid');

      // Populate the invalid field validation error
      let feedbackDiv = fieldElement?.closest('div');
      if (feedbackDiv && feedbackDiv.lastElementChild)
        feedbackDiv.lastElementChild.innerHTML = error.defaultMessage;
    });

    let registerButton = document.querySelector('#registerButton');
    let loadingSpinner = document.querySelector('#loadingSpinner');

    // Enable the Register button and display the loading spinner
    registerButton?.removeAttribute('disabled');
    loadingSpinner?.setAttribute('hidden', '');

    return EMPTY;
  }

  // really dont need this same function twice
  togglePassword(event: any): void {
    // Toggle the showPassword state
    this.showPassword = !this.showPassword;

    let eye = event.target;
    let passwordField = eye.parentElement.querySelector('[id*="password" i]');

    // Toggle the bi-eye and bi-eye-slash depending on showPassword state
    eye.classList.remove(this.showPassword ? 'bi-eye' : 'bi-eye-slash');
    eye.classList.add(this.showPassword ? 'bi-eye-slash' : 'bi-eye');

    // Toggle the password input type depending on showPassword state
    passwordField.type = this.showPassword ? 'text' : 'password';
  }
}
