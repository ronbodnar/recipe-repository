import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';

import { AuthenticationService } from '../../authentication.service';

@Component({
  selector: 'app-top-navbar',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './top-navbar.component.html',
  styleUrl: './top-navbar.component.css'
})
export class TopNavbarComponent {

  constructor(private authenticationService: AuthenticationService) {}

  authenticated() {
    return this.authenticationService.isAuthenticated()
  }

  getAuthenticatedUser() {
    return this.authenticationService.getAuthenticatedUser()
  }

  deauthenticate() {
    this.authenticationService.deauthenticate()
  }
}
