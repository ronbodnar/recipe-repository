import {
  ChangeDetectionStrategy,
  Component,
  Input,
  ViewEncapsulation,
  inject,
} from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { ComponentType } from '@angular/cdk/portal';
import { ButtonColor, ButtonComponent, ButtonSize } from '../button/button.component';
import { ComponentWrapperComponent } from './components/component-wrapper.component';
import { TranslatePipe } from '@ngx-translate/core';

export interface DialogAction {
  label: string;
  action?: () => void;
  disabledFn?: () => boolean;
  loadingFn?: () => boolean;
  closeOnClick?: boolean;
  size?: ButtonSize;
  color?: ButtonColor;
}

export interface DialogData {
  title: string;
  message?: string;
  actions?: DialogAction[];

  component?: ComponentType<unknown>;
  componentInputs?: Record<string, unknown>;
  componentOutputs?: Record<string, unknown>;
}

@Component({
  selector: 'app-dialog',
  styleUrl: './dialog.component.css',
  templateUrl: './dialog.component.html',
  imports: [
    MatDialogModule,
    MatButtonModule,
    ButtonComponent,
    ComponentWrapperComponent,
    TranslatePipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None,
})
export class DialogComponent {
  readonly data = inject<DialogData>(MAT_DIALOG_DATA);
  private readonly dialogRef = inject<MatDialogRef<DialogComponent>>(MatDialogRef);

  @Input() id: string = 'genericDialog';
  @Input() title: string = 'Dialog Title';
  @Input() hasCloseButton: boolean = true;
  @Input() httpErrorMessage: string = 'An error occurred.';
  @Input() unknownErrorMessage: string = 'An unexpected error occurred.';

  handleActionClick(action: DialogAction) {
    if (action.action) {
      action.action();
    }
    if (action.closeOnClick !== false) {
      this.close();
    }
  }

  close() {
    this.dialogRef.close();
  }
}
