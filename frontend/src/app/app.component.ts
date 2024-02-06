import { Component, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { TopNavbarComponent } from './navigation/top-navbar/top-navbar.component';
import { SideNavbarComponent } from './navigation/side-navbar/side-navbar.component';
import { HttpClient } from '@angular/common/http';
import { AuthenticationService } from './authentication.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    RouterOutlet,
    TopNavbarComponent,
    SideNavbarComponent,
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {
  title: string;

  constructor(
    private http: HttpClient,
    private router: Router,
    private authenticationService: AuthenticationService
  ) {
    this.title = 'Recipe Repository';
    this.authenticationService.checkIfAuthenticated()
  }
}
