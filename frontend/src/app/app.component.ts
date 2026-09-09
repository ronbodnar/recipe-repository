import { Component, inject, signal } from '@angular/core';
import {
  Event,
  NavigationCancel,
  NavigationEnd,
  NavigationError,
  NavigationStart,
  Router,
  RouterOutlet,
} from '@angular/router';
import { AppVersionService } from '@core/services/app-version.service';
import { SidenavComponent } from '@features/navigation/sidenav/sidenav.component';
import { FullPageLoaderComponent } from '@shared/ui/full-page-loader.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, FullPageLoaderComponent, SidenavComponent],
  template: `
    <app-side-nav>
      @if (loading()) {
        <app-full-page-loader />
      }
      @defer {
        <router-outlet />
      } @loading {
        <app-full-page-loader />
      }
    </app-side-nav>
  `,
})
export class AppComponent {
  private _loading = signal<boolean>(false);
  public readonly loading = this._loading.asReadonly();

  loaderTimeoutRef: ReturnType<typeof setTimeout> | undefined;

  private readonly router = inject(Router);
  private readonly appVersionService = inject(AppVersionService);

  ngOnInit(): void {
    console.log('Checking app version...');
    this.appVersionService.checkVersionAndReload();

    console.log('Subscribing to router events...');
    this.router.events.subscribe((event: Event) => this.interceptNavigation(event));

    console.log('Application initialized');
  }

  interceptNavigation(event: Event): void {
    if (event instanceof NavigationStart) {
      this.loaderTimeoutRef = setTimeout(() => {
        this._loading.set(true);
      }, 250);
      window.parent.postMessage(
        {
          type: 'nav_change',
          route: event.url,
          url: window.location.href,
          title: document.title,
        },
        '*',
      );
    } else if (
      event instanceof NavigationEnd ||
      event instanceof NavigationCancel ||
      event instanceof NavigationError
    ) {
      if (this.loaderTimeoutRef) {
        clearTimeout(this.loaderTimeoutRef);
        this.loaderTimeoutRef = undefined;
      }
      this._loading.set(false);
      window.scrollTo(0, 0);
    }
  }
}
