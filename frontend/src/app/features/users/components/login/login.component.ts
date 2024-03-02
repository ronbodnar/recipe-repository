import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { User } from '../../../../core/models/user';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { Validators } from '@angular/forms';
import { AuthenticationService } from '../../../../core/services/authentication.service';
import { EMPTY, Observable } from 'rxjs';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  user!: User;

  successfulRegistration: boolean = false;

  showPassword: boolean = false;

  loginForm = this.formBuilder.group({
    username: ['', Validators.required],
    password: ['', Validators.required],
  });

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private authService: AuthenticationService,
  ) {
    this.user = new User();
    this.authService.setLoginComponent(this)

    this.route.queryParams.subscribe(params => {
      this.successfulRegistration = params['registered']
    });
  }

  onSubmit() {
    // Clear any errors 
    let errorDiv = document.querySelector('#error')
    if (errorDiv) {
      errorDiv.innerHTML = '';
    }

    // Disable the Log In button and display the loading spinner
    let loginButton = document.querySelector('#loginButton')
    let loadingSpinner = document.querySelector('#loadingSpinner')
    loginButton?.setAttribute('disabled', '')
    loadingSpinner?.removeAttribute('hidden')

    this.authService.authenticate(this.loginForm.value.username!, this.loginForm.value.password!).subscribe(() => this.router.navigateByUrl('/'))
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

  handleError(error: any): Observable<any> {
    let passwordField = document.querySelector('#password');
    let errorDiv = document.getElementById('error')
    if (errorDiv) {
      errorDiv.innerHTML = this.formatErrorMessage(error)
    }

    if (error === 'Bad credentials') {
      passwordField?.classList.add('is-invalid')
    }

    let loginButton = document.querySelector('#loginButton')
    let loadingSpinner = document.querySelector('#loadingSpinner')

    // Enable the Log In button and display the loading spinner
    loginButton?.removeAttribute('disabled')
    loadingSpinner?.setAttribute('hidden', '')

    return EMPTY
  }

  formatErrorMessage(error: any): string {
    console.log(error);
    let errorMessages: any = {
      'Bad credentials': 'Invalid username or password.',
    }
    // Connection error
    if (error instanceof ProgressEvent) {
      return 'Unable to reach login server.'
    }
    return errorMessages.hasOwnProperty(error) ? errorMessages[error] : error
  }

}