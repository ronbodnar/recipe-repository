import { Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  RouterLink,
  RouterOutlet,
  RouterLinkActive,
  ActivatedRoute,
  Router,
  NavigationEnd,
} from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { MatIconModule } from '@angular/material/icon';
import { FluidContainerComponent } from '@shared/ui/fluid-container/fluid-container.component';

@Component({
  selector: 'app-settings',
  imports: [
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    MatIconModule,
    FluidContainerComponent,
    TranslatePipe,
  ],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.css',
})
export class SettingsComponent {
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  readonly currentRoute = signal(this.route.snapshot.firstChild?.routeConfig?.path ?? '');

  settingsNav = [
    { label: 'settings.general.label', route: 'general', icon: 'tune' },
    { label: 'settings.profile.label', route: 'profile', icon: 'person' },
  ];

  constructor() {
    this.router.events.pipe(takeUntilDestroyed()).subscribe((e) => {
      if (e instanceof NavigationEnd) {
        this.currentRoute.set(this.route.snapshot.firstChild?.routeConfig?.path ?? '');
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/app/recipes/list']);
  }
}
