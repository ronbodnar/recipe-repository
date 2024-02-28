import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthenticationService } from '../../core/services/authentication.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {

  @Input() showOverlay!: boolean;

  constructor(private authenticationService: AuthenticationService) {}

  getAuthenticationService() {
    return this.authenticationService
  }
}
