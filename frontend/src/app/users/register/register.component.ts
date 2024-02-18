import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { User } from '../user';
import { FormBuilder, FormControl, ReactiveFormsModule } from '@angular/forms';
import { Validators } from '@angular/forms';
import { AuthenticationService } from '../../authentication.service';

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
    private formBuilder: FormBuilder,
    private authService: AuthenticationService
  ) {
    this.user = new User();
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
    if (!this.registrationForm.valid) {
      this.validate();
      return;
    }
    this.authService.register(this.registrationForm.value!);
  }

  validate() {
    let forms = document.querySelectorAll('.needs-validation');

    Array.prototype.slice.call(forms).forEach((form) => {
      form.checkValidity();

      form.classList.add('was-validated');
    }, false);
  }

  // really dont need this same function twice
  togglePassword(event: any): void {
    this.showPassword = !this.showPassword

    let eye = event.target
    let passwordField = eye.parentElement.querySelector('[id*="password"]')
    
    if (this.showPassword) {
      eye.classList.remove('bi-eye')
      eye.classList.add('bi-eye-slash')
      passwordField.type = 'text'
    } else {
      eye.classList.add('bi-eye')
      eye.classList.remove('bi-eye-slash')
      passwordField.type = 'password'
    }
  }
}
