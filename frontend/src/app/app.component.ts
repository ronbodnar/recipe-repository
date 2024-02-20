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

import { FooterComponent } from './navigation/footer/footer.component';
import { TopNavbarComponent } from './navigation/top-navbar/top-navbar.component';
import { SideNavbarComponent } from './navigation/side-navbar/side-navbar.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    RouterOutlet,
    FooterComponent,
    TopNavbarComponent,
    SideNavbarComponent,
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {
  showOverlay: boolean = false;

  constructor(private router: Router) {
    this.showOverlay = false;

    router.events.subscribe((event: any) => {
      this.interceptNavigation(event);
    });
  }

  // intercepting navigation routing events to toggle a loading overlay
  interceptNavigation(event: RouterEvent): void {
    if (event instanceof NavigationStart) {
      this.showOverlay = true;
    } else if (
      event instanceof NavigationEnd ||
      event instanceof NavigationCancel ||
      event instanceof NavigationError
    ) {
      this.showOverlay = false;
    }
  }
}
