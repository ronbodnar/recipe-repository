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
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { ButtonColor, ButtonComponent } from './button/button.component';
import { TranslatePipe } from '@shared/pipes/translate.pipe';

export enum SnackbarType {
  SUCCESS = 'success',
  ERROR = 'error',
  INFO = 'info',
}

export interface SnackbarData {
  type: SnackbarType;
  duration?: number;
  message?: string;
  translateData?: Record<string, string | number>;
  actions?: {
    icon?: string;
    label?: string;
    action?: () => void;
    color?: ButtonColor;
    closeOnClick?: boolean;
  }[];
  classList?: string;
  horizontalPosition?: MatSnackBarHorizontalPosition;
  verticalPosition?: MatSnackBarVerticalPosition;
}

@Injectable({
  providedIn: 'root',
})
export class SnackbarService {
  private _snackBar = inject(MatSnackBar);

  openSnackBar(
    type: SnackbarType,
    data: string | SnackbarData,
    translateData: Record<string, string | number> = {},
  ): MatSnackBarRef<SnackbarComponent> {
    if (typeof data === 'string') {
      data = {
        type: type,
        message: data,
        translateData: translateData,
        actions: [
          {
            icon: 'close',
            action: () => {
              this._snackBar.dismiss();
            },
          },
        ],
      };
    }

    if (!data.type) {
      data.type = SnackbarType.INFO;
    }

    return this._snackBar.openFromComponent(SnackbarComponent, {
      data: data,
      duration: data.duration || 5000,
      horizontalPosition: data.horizontalPosition || 'center',
      verticalPosition: data.verticalPosition || 'bottom',
      panelClass: ['mt-[var(--mat-toolbar-standard-height)]!'],
    });
  }
}

@Component({
  selector: 'snack-bar',
  template: `
    <div class="flex {{ data.classList }}">
      <div class="self-center px-2">
        <mat-icon
          [ngClass]="{
            'text-success!': data.type === 'success',
            'text-label!': data.type === 'info',
            'text-danger!': data.type === 'error',
          }"
        >
          {{
            data.type === 'success' ? 'check_circle' : data.type === 'info' ? 'info' : 'dangerous'
          }}
        </mat-icon>
      </div>

      <span matSnackBarLabel class="text-sm! px-0!">
        {{ data.message | translate: data.translateData }}
      </span>

      <div matSnackBarActions class="pl-4 pr-2 ml-auto">
        @for (action of data.actions; track action) {
          <app-button
            matButton
            matSnackBarAction
            [icon]="action.icon"
            [label]="action.label"
            [size]="'sm'"
            [color]="action.color || 'transparent'"
            (click)="handleAction(action.action, action.closeOnClick !== false)"
          />
        }
      </div>
    </div>
  `,
  imports: [
    MatButtonModule,
    MatSnackBarLabel,
    MatSnackBarActions,
    MatSnackBarAction,
    MatIconModule,
    ButtonComponent,
    CommonModule,
    TranslatePipe,
  ],
})
export class SnackbarComponent {
  data = inject<SnackbarData>(MAT_SNACK_BAR_DATA);

  handleAction(action?: () => void, close: boolean = true) {
    if (close) {
      this.snackBarRef.dismiss();
    }
    if (typeof action === 'function') {
      action();
    }
  }

  snackBarRef = inject(MatSnackBarRef);
}
