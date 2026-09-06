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
import { CommonModule, DOCUMENT } from '@angular/common';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NavItem } from '../nav-item.interface';
import { AppVersionService } from '@core/services/app-version.service';
import { AuthenticationService } from '@core/services/authentication.service';
import { MediaService } from '@core/services/media.service';
import { TranslatePipe } from '@ngx-translate/core';
import { getNavItems } from '../nav-menu.config';
import { StorageService } from '@core/services/storage.service';
import { ToolbarComponent } from '../toolbar/toolbar.component';

@Component({
  selector: 'app-side-nav',
  imports: [
    CommonModule,
    MatSidenavModule,
    MatIconModule,
    MatListModule,
    MatTooltipModule,
    TranslatePipe,
    ToolbarComponent,
  ],
  encapsulation: ViewEncapsulation.None,
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './sidenav.component.html',
  styleUrl: './sidenav.component.css',
})
export class SidenavComponent implements OnDestroy {
  @ViewChild('snav') nav!: MatSidenav;

  private readonly router = inject(Router);
  private readonly authService = inject(AuthenticationService);
  private readonly storageService = inject(StorageService);
  private readonly mediaService = inject(MediaService);
  private readonly version = inject(AppVersionService);
  private readonly document = inject(DOCUMENT);

  readonly currentVersion = this.version.currentVersion;
  readonly isMobile = this.mediaService.isMobile;
  readonly isAuthenticated = this.authService.isAuthenticated;

  private readonly _isSidenavOpen = signal(
    !this.isMobile() && this.storageService.getPreference('sidenav.isOpen') === true,
  );

  readonly isSidenavOpen = this._isSidenavOpen.asReadonly();

  readonly NAV_ITEMS = getNavItems();

  private expandedSections = signal<string[]>(
    this.storageService.getPreference('sidenav.expandedSections') || ['recipes', 'groups'],
  );

  toggleSection(section: string) {
    this.expandedSections.update((current) => {
      return current.includes(section)
        ? current.filter((s) => s !== section)
        : current.map((s) => s).concat(section);
    });
    this.storageService.setPreference('sidenav.expandedSections', this.expandedSections());
  }

  onNavigationClick(navItem: NavItem, childIdxClicked?: number) {
    const { children, route, onClick, sectionName } = navItem;
    const hasChildren = children?.length;

    if (hasChildren && childIdxClicked === undefined) {
      this.toggleSection(sectionName);
      return;
    }

    if (this.isMobile()) {
      this.nav.close();
    }

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

  onSidenavOpenedChange(isOpened: boolean) {
    this.storageService.setPreference('sidenav.isOpen', isOpened);
    this._isSidenavOpen.set(isOpened);

    if (this.isMobile()) {
      this.document.body.classList.toggle('mobile-sidenav-open', isOpened);
    }
  }

  ngOnDestroy() {
    this.document.body.classList.remove('mobile-sidenav-open');
  }
}
