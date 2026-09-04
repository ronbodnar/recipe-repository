import {
  ChangeDetectionStrategy,
  Component,
  inject,
  OnDestroy,
  signal,
  ViewChild,
  ViewEncapsulation,
} from '@angular/core';

import { Router } from '@angular/router';
import { MatSidenav, MatSidenavModule } from '@angular/material/sidenav';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { HeaderComponent } from '../header/header.component';
import { CommonModule, DOCUMENT } from '@angular/common';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NavItem } from '../nav-item.interface';
import { AppVersionService } from '@core/services/app-version.service';
import { AuthenticationService } from '@core/services/authentication.service';
import { MediaService } from '@core/services/media.service';
import { TranslatePipe } from '@ngx-translate/core';
import { getNavItems } from '../nav-menu.config';

@Component({
  selector: 'app-side-navbar',
  imports: [
    CommonModule,
    MatSidenavModule,
    MatIconModule,
    MatListModule,
    MatTooltipModule,
    HeaderComponent,
    TranslatePipe,
  ],
  encapsulation: ViewEncapsulation.None,
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css',
})
export class SidebarComponent implements OnDestroy {
  @ViewChild('snav') nav!: MatSidenav;

  private router = inject(Router);
  private authService = inject(AuthenticationService);
  private mediaService = inject(MediaService);
  private version = inject(AppVersionService);
  private document = inject(DOCUMENT);

  readonly currentVersion = this.version.currentVersion;

  readonly isMobile = this.mediaService.isMobile;

  private _authLoading = signal<boolean>(false);

  public authLoading = this._authLoading.asReadonly();

  readonly NAV_ITEMS = getNavItems();

  private expandedSections = signal<string[]>(['recipes', 'groups']);

  toggleSection(section: string) {
    this.expandedSections.update((current) =>
      current.includes(section)
        ? current.filter((s) => s !== section)
        : current.map((s) => s).concat(section),
    );
  }

  onNavigationClick(navItem: NavItem, childIdxClicked?: number) {
    const { children, route, onClick, sectionName } = navItem;
    const hasChildren = children?.length;

    if (hasChildren && childIdxClicked === undefined) {
      this.toggleSection(sectionName);
      return;
    }

    this.nav.close();

    if (!hasChildren && childIdxClicked === undefined) {
      if (route) {
        if (route.startsWith('.')) {
          window.open(document.location.origin + route.slice(1), '_blank');
        } else {
          this.router.navigateByUrl(route);
        }
        return;
      }

      if (onClick) {
        onClick();
      }
      return;
    }

    if (!children || childIdxClicked === undefined) {
      return;
    }

    const child = children[childIdxClicked];
    if (!child) {
      console.warn(
        'No child found within children for navigation click: ',
        navItem,
        childIdxClicked,
      );
      return;
    }
    if (child.route) {
      if (child.route.startsWith('.')) {
        window.open(document.location.origin + child.route.slice(1), '_blank');
      } else {
        this.router.navigateByUrl(child.route);
      }
    }
  }

  isOpen(section: string): boolean {
    return this.expandedSections().includes(section);
  }

  hasRole(requiredRole: string | undefined): boolean {
    if (!requiredRole) {
      return true;
    }
    return this.authService.authUser()?.roles.includes(requiredRole) || false;
  }

  deauthenticate() {
    this._authLoading.set(true);
    this.authService.logout().then(() => {
      this._authLoading.set(false);
      this.router.navigateByUrl('/');
      this.nav.close();
    });
  }

  isAuthenticated() {
    return this.authService.isAuthenticated();
  }

  onSidenavOpenedChange(isOpened: boolean) {
    if (!this.isMobile()) {
      return;
    }

    this.document.body.classList.toggle('mobile-sidenav-open', isOpened);
  }

  ngOnDestroy() {
    this.document.body.classList.remove('mobile-sidenav-open');
  }
}
