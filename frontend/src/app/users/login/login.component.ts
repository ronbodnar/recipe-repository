import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
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

  constructor(
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
    this.authService.authenticate(this.loginForm.value.username!, this.loginForm.value.password!);
  }

  handleError(error: any) {
    let errorDiv = document.getElementById('error')
    if (errorDiv) {
      errorDiv.innerHTML = this.getErrorMessage(error)
      errorDiv.removeAttribute('hidden')
    }
  }

  getErrorMessage(error: any) : string {
    let errorMessages: any = {
      "Bad credentials": "Invalid username or password."
    }
    return errorMessages[error]
  }
}
