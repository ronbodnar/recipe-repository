import { Injectable, inject } from '@angular/core';
import { SnackbarService, SnackbarType } from '@shared/ui/snackbar.component';
import { DialogService } from '@shared/ui/dialog/dialog.service';
import { UserService } from '@features/users/user.service';

@Injectable({
  providedIn: 'root',
})
export class UserInvitationService {
  private readonly userService = inject(UserService);
  private readonly dialogService = inject(DialogService);
  private readonly snackbarService = inject(SnackbarService);

  promptInviteToRegister() {
    const dialog = this.dialogService.openInputDialog(
      'auth.inviteToRegister.title',
      'auth.inviteToRegister.instructions',
      'text',
      {
        submitButtonLabel: 'auth.inviteToRegister.submitButton',
      },
    );
    dialog.afterClosed().subscribe((response) => {
      if (response) {
        this.userService.inviteUserToRegister(response).subscribe({
          next: () => {
            this.snackbarService.openSnackBar(
              SnackbarType.SUCCESS,
              'auth.inviteToRegister.successMessage',
            );
          },
          error: () => {
            this.snackbarService.openSnackBar(
              SnackbarType.ERROR,
              'auth.inviteToRegister.errorMessage',
            );
          },
        });
      }
    });
  }
}
