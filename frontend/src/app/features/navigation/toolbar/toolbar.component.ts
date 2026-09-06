import { Component, inject, Input } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatMenuModule } from '@angular/material/menu';
import { RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { TranslatePipe } from '@ngx-translate/core';
import { MediaService } from '@core/services/media.service';
import { ButtonComponent } from '@shared/ui/button/button.component';
import { MatSidenav } from '@angular/material/sidenav';

@Component({
  selector: 'app-toolbar',
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
  templateUrl: './toolbar.component.html',
  styleUrl: './toolbar.component.css',
})
export class ToolbarComponent {
  @Input() snav!: MatSidenav;

  private readonly mediaService = inject(MediaService);

  readonly isMobile = this.mediaService.isMobile;
}
