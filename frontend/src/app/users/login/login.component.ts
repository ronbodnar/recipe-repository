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
  }

  loginForm = this.formBuilder.group({
    username: ['', Validators.required],
    password: ['', Validators.required],
  });

  onSubmit() {
    let response = this.authService.authenticate(this.loginForm.value.username!, this.loginForm.value.password!);
    console.log(response.forEach(element => console.log(element)));
  }
}
