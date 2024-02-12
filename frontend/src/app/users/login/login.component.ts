import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { User } from '../user';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { Validators } from '@angular/forms';
import { AuthenticationService } from '../../authentication.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterLink, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  user!: User;

  showPassword: boolean = false;

  constructor(
    private router: Router,
    private formBuilder: FormBuilder,
    private authService: AuthenticationService,
  ) {
    this.user = new User();
    this.authService.setLoginComponent(this)
  }

  loginForm = this.formBuilder.group({
    username: ['', Validators.required],
    password: ['', Validators.required],
  });

  onSubmit() {
    // Clear any errors 
    let errorDiv = document.querySelector('#error')
    if (errorDiv) {
      errorDiv.innerHTML = '';
      errorDiv.setAttribute('hidden', '')
    }

    // Disable the Log In button and display the loading spinner
    let loginButton = document.querySelector('#loginButton')
    let loadingSpinner = document.querySelector('#loadingSpinner')
    loginButton?.setAttribute('disabled', '')
    loadingSpinner?.removeAttribute('hidden')

    this.authService.authenticate(this.loginForm.value.username!, this.loginForm.value.password!)
  }

  onComplete(): void {
      // Navigate to the home page if the login is successful
      this.router.navigate(['/'])
  }

  togglePassword(event: any): void {
    // Toggle the showPassword state
    this.showPassword = !this.showPassword

    let eye = event.target
    let passwordField = eye.parentElement.querySelector('#password')
    
    // Toggle the bi-eye and bi-eye-slash depending on showPassword state
    eye.classList.remove(this.showPassword ? 'bi-eye' : 'bi-eye-slash')
    eye.classList.add(this.showPassword ? 'bi-eye-slash' : 'bi-eye')

    // Toggle the password input type depending on showPassword state
    passwordField.type = this.showPassword ? 'text' : 'password'
  }

  handleError(error: any) {
    let passwordField = document.querySelector('#password');
    let errorDiv = document.getElementById('error')
    if (errorDiv) {
      errorDiv.innerHTML = this.authService.formatErrorMessage(error)
      errorDiv.removeAttribute('hidden')
    }

    if (error === 'Bad credentials') {
      passwordField?.classList.add('is-invalid')
    }

    let loginButton = document.querySelector('#loginButton')
    let loadingSpinner = document.querySelector('#loadingSpinner')

    // Enable the Log In button and display the loading spinner
    loginButton?.removeAttribute('disabled')
    loadingSpinner?.setAttribute('hidden', '')
  }

}