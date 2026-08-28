import { CommonModule } from '@angular/common';
import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NavigationBehaviorOptions, Router } from '@angular/router';
import { TranslatePipe } from '@shared/pipes/translate.pipe';

export type ButtonColor =
  | 'primary'
  | 'secondary'
  | 'light'
  | 'danger'
  | 'transparent'
  | 'background';

export type ButtonSize = 'xs' | 'sm' | 'md' | 'lg' | 'xl';

export type ButtonIconPosition = 'left' | 'right';

export type RouterTarget = '_blank' | '_self' | '_parent' | '_top';

@Component({
  selector: 'app-button',
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    MatProgressSpinnerModule,
    TranslatePipe,
  ],
  templateUrl: './button.component.html',
  styleUrl: './button.component.css',
})
export class ButtonComponent {
  @Input() id?: string;
  @Input() label?: string;
  @Input() customLabel?: boolean;
  @Input() labelClassList: string | string[] = '';

  @Input() tooltip?: string;

  @Input() type: 'button' | 'submit' = 'button';
  @Input() color: ButtonColor = 'primary';
  @Input() size: ButtonSize = 'md';

  @Input() icon?: string;
  @Input() iconClassList: string | string[] = '';
  @Input() iconPosition: ButtonIconPosition = 'left';

  @Input() classList: string | string[] = '';
  @Input() wrapperClassList: string | string[] = '';

  @Input() cursor: 'pointer' | 'not-allowed' | 'default' = 'pointer';
  @Input() disabled: boolean = false;
  @Input() loading: boolean = false;

  @Input() routerLink?: string;
  @Input() routerState?: Record<string, unknown>;
  @Input() routerTarget: RouterTarget = '_self';

  /** Allows binding from within table templates */
  @Input() onClick?: (event: MouseEvent) => void;
  @Input() disableWhen?: (event: MouseEvent) => boolean;

  @Output() click = new EventEmitter<MouseEvent>();

  private readonly router = inject(Router);

  get colorClass(): string {
    switch (this.color) {
      case 'primary':
        return 'bg-primary text-white border border-primary hover:bg-primary/80 active:bg-primary/70';

      case 'secondary':
        return 'bg-background-alt text-white border border-background-alt hover:bg-background-alt/80 active:bg-background-alt/70';

      case 'background':
        return 'bg-background text-text border border-background-alt hover:bg-background-alt active:bg-background-alt';

      case 'light':
        return 'bg-gray-100 text-gray-800 border border-gray-200 hover:bg-gray-200 active:bg-gray-300';

      case 'danger':
        return 'bg-danger text-white border border-danger hover:bg-danger/80 active:bg-danger/70';

      case 'transparent':
        return 'bg-transparent text-background border border-transparent hover:bg-transparent/80 active:bg-transparent/70';
    }
  }

  get wrapperSizeClass(): string {
    if (this.icon && !this.label) {
      switch (this.size) {
        case 'xs':
          return 'p-1';

        case 'sm':
          return 'p-1.5';

        case 'md':
          return 'p-2';

        case 'lg':
          return 'p-2.5';

        case 'xl':
          return 'p-3';
      }
    }

    switch (this.size) {
      case 'xs':
        return 'text-xs px-2 py-1';

      case 'sm':
        return 'text-sm px-3 py-1.5';

      case 'md':
        return 'text-base px-4 py-2';

      case 'lg':
        return 'text-lg px-5 py-2.5';

      case 'xl':
        return 'text-xl px-6 py-3';
    }
  }

  get iconSizeClass(): string {
    switch (this.size) {
      case 'xs':
        return '!text-sm/4 !w-4 !h-4';

      case 'sm':
        return '!text-base/5 !w-5 !h-5';

      case 'md':
        return '!text-xl/6 !w-6 !h-6';

      case 'lg':
        return '!text-2xl/7 !w-7 !h-7';

      case 'xl':
        return '!text-3xl/8 !w-8 !h-8';
    }
  }

  handleClick(event: MouseEvent): void {
    event.stopPropagation();
    if (this.disabled || this.loading || (this.disableWhen && this.disableWhen(event))) {
      return;
    }
    if (this.routerLink) {
      this.router.navigate([this.routerLink], {
        state: this.routerState,
        //queryParams: this.routerState,
        target: this.routerTarget,
      } as NavigationBehaviorOptions);
      return;
    }
    if (this.onClick) {
      this.onClick(event);
    }
    this.click.emit(event);
  }

  normalizedClassList(classList: string | string[]): string {
    if (Array.isArray(classList)) {
      return classList.join(' ');
    }
    return classList;
  }
}
