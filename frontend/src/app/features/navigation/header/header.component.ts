import { Component, inject, Input } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatMenuModule } from '@angular/material/menu';
import { RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { TranslatePipe } from '@shared/pipes/translate.pipe';
import { MediaService } from '@core/services/media.service';
import { ButtonComponent } from '@shared/ui/button/button.component';
import { MatSidenav } from '@angular/material/sidenav';

@Component({
  selector: 'app-header',
  imports: [
    MatTooltipModule,
    MatIconModule,
    MatMenuModule,
    MatToolbarModule,
    ButtonComponent,
    RouterLink,
    MatDividerModule,
    MatSlideToggleModule,
    TranslatePipe,
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css',
})
export class HeaderComponent {
  @Input() snav!: MatSidenav;

  private readonly mediaService = inject(MediaService);

  readonly isMobile = this.mediaService.isMobile;
}
