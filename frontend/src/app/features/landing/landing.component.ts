import { Component, inject } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { AuthenticationService } from '@core/services/authentication.service';
import { TranslatePipe } from '@ngx-translate/core';
import { ButtonComponent } from '@shared/ui/button/button.component';

@Component({
  selector: 'app-landing',
  imports: [ButtonComponent, TranslatePipe, MatIconModule],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.css',
})
export class LandingComponent {
  private readonly authService = inject(AuthenticationService);

  readonly cards = [
    {
      icon: 'lock',
      title: 'landing.privateRecipeTitle',
      subtitle: 'landing.privateRecipeSubtitle',
    },
    {
      icon: 'search',
      title: 'landing.ingredientsTitle',
      subtitle: 'landing.ingredientsSubtitle',
    },
    {
      icon: 'tune',
      title: 'landing.scaleTitle',
      subtitle: 'landing.scaleSubtitle',
    },
  ];

  login() {
    this.authService.login();
  }

  register() {
    this.authService.register();
  }
}
