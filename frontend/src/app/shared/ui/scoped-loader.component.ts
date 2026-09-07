import { Component, Input } from '@angular/core';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-scoped-loader',
  standalone: true,
  imports: [MatProgressBarModule, MatProgressSpinnerModule, TranslatePipe],
  template: `
    <div class="scoped-loader">
      <div class="loader-content">
        <p class="loading-message">
          {{ message | translate }}
        </p>
        <mat-progress-bar mode="indeterminate" color="primary" />
      </div>
    </div>
  `,
  styles: [
    `
      .scoped-loader {
        position: relative;
        top: 0;
        left: 0;
        z-index: 9999;
        width: 100%;
        height: 100%;
        display: flex;
        align-items: center;
        flex-direction: column;
      }

      .loader-content {
        width: 300px;
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 1rem;
      }

      .loading-message {
        font-size: 1rem;
        text-align: center;
      }
    `,
  ],
})
export class ScopedLoaderComponent {
  @Input() message = 'loading.long';
}
