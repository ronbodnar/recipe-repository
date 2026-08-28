import { inject, Injectable } from '@angular/core';
import { MatDialog, MatDialogConfig, MatDialogRef } from '@angular/material/dialog';
import { DialogComponent } from './dialog.component';
import { ComponentType } from '@angular/cdk/portal';

@Injectable({
  providedIn: 'root',
})
export class DialogService {
  private readonly dialog = inject(MatDialog);

  closeAll(): void {
    this.dialog.closeAll();
  }

  open<T>(component: ComponentType<T>, config?: MatDialogConfig): MatDialogRef<T> {
    return this.dialog.open(component, config);
  }

  openConfirmationDialog(title: string, message: string, responseFn: (response: boolean) => void) {
    this.dialog.closeAll();
    return this.dialog.open(DialogComponent, {
      disableClose: true,
      data: {
        title: title,
        message: message,
        actions: [
          {
            label: 'forms.cancel',
            action: () => {
              responseFn(false);
              this.dialog.closeAll();
            },
            color: 'secondary',
          },
          {
            label: 'forms.confirm',
            action: () => {
              responseFn(true);
              this.dialog.closeAll();
            },
            color: 'danger',
          },
        ],
      },
    });
  }
}
