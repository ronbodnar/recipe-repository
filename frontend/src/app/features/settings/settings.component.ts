import { Component } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink, RouterOutlet } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { FluidContainerComponent } from '@shared/ui/fluid-container/fluid-container.component';

@Component({
  selector: 'app-settings',
  imports: [RouterOutlet, RouterLink, MatIconModule, FluidContainerComponent, TranslatePipe],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.css',
})
export class SettingsComponent {
  settingsNav = [
    { label: 'settings.general.label', route: 'general', icon: 'tune' },
    { label: 'settings.profile.label', route: 'profile', icon: 'person' },
    { label: 'settings.security.label', route: 'security', icon: 'security' },
  ];
}
