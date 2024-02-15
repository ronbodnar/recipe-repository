import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  NavigationCancel,
  NavigationEnd,
  NavigationError,
  NavigationStart,
  Router,
  RouterEvent,
  RouterLink,
  RouterOutlet,
} from '@angular/router';

import { TopNavbarComponent } from './navigation/top-navbar/top-navbar.component';
import { SideNavbarComponent } from './navigation/side-navbar/side-navbar.component';
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

  showOverlay: boolean = false;

  constructor(
    private router: Router,
    private authenticationService: AuthenticationService
  ) {
    this.title = 'Recipe Repository';
    /*this.authenticationService.checkAuthentication();

    this.showOverlay = false

    router.events.subscribe((event: any) => {
      this.interceptNavigation(event);
      console.log(event);
    });*/
  }

  // intercepting navigation routing events to toggle a loading overlay
  interceptNavigation(event: RouterEvent): void {
    if (event instanceof NavigationStart) {
      this.showOverlay = true;
    } else if (event instanceof NavigationEnd || event instanceof NavigationCancel || event instanceof NavigationError) {
      this.showOverlay = false;
    }
  }

}