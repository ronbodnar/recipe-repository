import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@shared/pipes/translate.pipe';

@Component({
  selector: 'app-full-page-loader',
  standalone: true,
  imports: [MatProgressBarModule, MatProgressSpinnerModule, TranslatePipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="full-page-loader">
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
      .full-page-loader {
        position: absolute;
        top: 0;
        left: 0;
        z-index: 9999;
        width: 100%;
        height: 100%;
        display: flex;
        align-items: center;
        flex-direction: column;
        background-color: var(--background-color);
      }

      .loader-content {
        width: 300px;
        height: 100%;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        gap: 1rem;
      }

      .loading-message {
        font-size: 1rem;
        text-align: center;
      }
    `,
  ],
})
export class FullPageLoaderComponent {
  @Input() message = 'app.loadingMessage';
}
