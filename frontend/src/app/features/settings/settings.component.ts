import { Component } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink, RouterOutlet } from '@angular/router';
import { FluidContainerComponent } from '@shared/ui/fluid-container/fluid-container.component';

@Component({
  selector: 'app-settings',
  imports: [RouterOutlet, RouterLink, MatIconModule, FluidContainerComponent],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.css',
})
export class SettingsComponent {
  settingsNav = [
    { label: 'General', route: 'general', icon: 'tune' },
    { label: 'Profile', route: 'profile', icon: 'person' },
    { label: 'Security', route: 'security', icon: 'security' },
  ];
}
