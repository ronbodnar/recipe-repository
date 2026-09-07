import { ChangeDetectionStrategy, Component, Input, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { redirectTo } from '@shared/utils/redirect-to';

@Component({
  selector: 'app-table-link',
  imports: [MatIconModule, MatTooltipModule, TranslatePipe, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './table-link.component.html',
  styleUrl: './table-link.component.css',
})
export class TableLinkComponent {
  private readonly router = inject(Router);

  @Input() label: string = 'N/A';
  @Input() routerLink?: string;
  @Input() routerState?: Record<string, unknown>;
  @Input() target: '_blank' | '_self' | '_parent' | '_top' = '_self';
  @Input() clamp: number | null = null;
  @Input() applyOpacity: boolean = false;
  @Input() reloadOnClick: boolean = false;

  @Input() onClick?: (event: MouseEvent, data: unknown) => void;
  @Input() transformLabel?: (label?: string) => string;

  EM_DASH = '—';

  get transformedLabel(): string {
    return this.transformLabel
      ? this.transformLabel(this.label) || this.EM_DASH
      : this.label || this.EM_DASH;
  }

  handleClick(event: MouseEvent) {
    /*     if (this.keyboard.isKeyDown('Control') || this.keyboard.isKeyDown('Shift')) {
      return;
    } */
    if (this.onClick) {
      this.onClick(event, this.label);
    }
    if (this.reloadOnClick) {
      redirectTo(this.router, this.routerLink || '', this.routerState);
    } else {
      this.router.navigate([this.routerLink], this.routerState);
    }
  }
}
