import { Component, inject, Injectable } from '@angular/core';
import {
  MAT_SNACK_BAR_DATA,
  MatSnackBar,
  MatSnackBarAction,
  MatSnackBarActions,
  MatSnackBarHorizontalPosition,
  MatSnackBarLabel,
  MatSnackBarRef,
  MatSnackBarVerticalPosition,
} from '@angular/material/snack-bar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';

import { ButtonColor, ButtonComponent } from './button/button.component';

export enum SnackbarType {
  SUCCESS = 'success',
  ERROR = 'error',
  INFO = 'info',
}

export interface SnackbarAction {
  icon?: string;
  label?: string;
  action?: () => void;
  color?: ButtonColor;
  closeOnClick?: boolean;
}

export interface SnackbarData {
  type: SnackbarType;
  message?: string;
  translateData?: Record<string, string | number>;
  duration?: number;
  actions?: SnackbarAction[];
  classList?: string;
  horizontalPosition?: MatSnackBarHorizontalPosition;
  verticalPosition?: MatSnackBarVerticalPosition;
}

@Injectable({
  providedIn: 'root',
})
export class SnackbarService {
  private readonly snackBar = inject(MatSnackBar);

  openSnackBar(
    type: SnackbarType,
    data: string | SnackbarData,
    translateData: Record<string, string | number> = {},
  ): MatSnackBarRef<SnackbarComponent> {
    const snackbarData: SnackbarData =
      typeof data === 'string'
        ? {
            type,
            message: data,
            translateData,
            actions: [{ icon: 'close' }],
          }
        : {
            ...data,
            type: data.type ?? type,
          };

    return this.snackBar.openFromComponent(SnackbarComponent, {
      data: snackbarData,
      duration: snackbarData.duration ?? 50000,
      horizontalPosition: snackbarData.horizontalPosition ?? 'right',
      verticalPosition: snackbarData.verticalPosition ?? 'bottom',
      panelClass: ['mt-[var(--mat-toolbar-standard-height)]!'],
    });
  }
}

@Component({
  selector: 'app-snackbar',
  template: `
    <div
      class="flex w-full p-2 pl-6 {{ data.classList }}"
      [class.bg-success]="data.type === SnackbarType.SUCCESS"
      [class.bg-label]="data.type === SnackbarType.INFO"
      [class.bg-danger]="data.type === SnackbarType.ERROR"
    >
      <span
        matSnackBarLabel
        class="px-0! text-sm!"
        [class.text-text!]="data.type === SnackbarType.INFO"
        [class.text-white!]="data.type !== SnackbarType.INFO"
      >
        {{ data.message | translate: data.translateData }}
      </span>

      @if (data.actions?.length) {
        <div matSnackBarActions class="flex gap-1 pl-4 pr-2 ml-auto">
          @for (action of data.actions; track $index) {
            <app-button
              matButton
              matSnackBarAction
              [icon]="action.icon"
              [label]="action.label"
              size="sm"
              [color]="action.color ?? 'transparent'"
              [iconClassList]="data.type === SnackbarType.INFO ? 'text-text!' : 'text-white!'"
              (click)="handleAction(action)"
            />
          }
        </div>
      }
    </div>
  `,
  imports: [
    MatButtonModule,
    MatSnackBarLabel,
    MatSnackBarActions,
    MatSnackBarAction,
    MatIconModule,
    ButtonComponent,
    TranslatePipe,
  ],
})
export class SnackbarComponent {
  protected readonly SnackbarType = SnackbarType;

  readonly data = inject<SnackbarData>(MAT_SNACK_BAR_DATA);
  private readonly snackBarRef = inject(MatSnackBarRef);

  get icon(): string {
    switch (this.data.type) {
      case SnackbarType.SUCCESS:
        return 'check_circle';
      case SnackbarType.ERROR:
        return 'dangerous';
      case SnackbarType.INFO:
      default:
        return 'info';
    }
  }

  protected handleAction(action: SnackbarAction): void {
    if (action.closeOnClick ?? true) {
      this.snackBarRef.dismiss();
    }

    action.action?.();
  }
}
