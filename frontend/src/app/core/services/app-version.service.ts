import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, of } from 'rxjs';
import { SnackbarService, SnackbarData, SnackbarType } from '@shared/ui/snackbar.component';

@Injectable({
  providedIn: 'root',
})
export class AppVersionService {
  private readonly http = inject(HttpClient);
  private readonly snackBar = inject(SnackbarService);

  private readonly versionUrl = './assets/version.json';
  public readonly currentVersion = '0.1.0';

  checkVersionAndReload() {
    this.http
      .get<{ version: string }>(this.versionUrl)
      .pipe(catchError(() => of({ version: this.currentVersion })))
      .subscribe((res) => {
        if (res.version !== this.currentVersion) {
          const snackBarData: SnackbarData = {
            type: SnackbarType.INFO,
            message: 'ui.snackbar.newVersion',
            actions: [
              {
                icon: 'refresh',
                action: () => {
                  window.location.reload();
                },
              },
            ],
          };
          this.snackBar.openSnackBar(SnackbarType.INFO, snackBarData);
        }
      });
  }
}
