import { Component, inject, input } from '@angular/core';
import { MediaService } from '@core/services/media.service';
import { TranslatePipe } from '@ngx-translate/core';
import { ButtonComponent } from '../button/button.component';

@Component({
  selector: 'app-fluid-container',
  imports: [TranslatePipe, ButtonComponent],
  templateUrl: './fluid-container.component.html',
  styleUrl: './fluid-container.component.css',
})
export class FluidContainerComponent {
  pageTitle = input<string>('');
  size = input<'sm' | 'md' | 'lg' | 'xl' | 'full'>('md');
  backRoute = input<string | null>(null);

  private mediaService = inject(MediaService);

  readonly isMobile = this.mediaService.isMobile;

  protected get widthClass(): string {
    const classes = {
      sm: 'max-w-xl',
      md: 'max-w-3xl',
      lg: 'max-w-5xl',
      xl: 'max-w-7xl',
      full: 'max-w-full',
    };
    return classes[this.size()];
  }
}
