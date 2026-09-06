import { provideZoneChangeDetection } from '@angular/core';
import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';
import { logError } from '@shared/utils/logging';

bootstrapApplication(AppComponent, {
  ...appConfig,
  providers: [provideZoneChangeDetection(), ...appConfig.providers],
})
  .catch((err) => logError(err))
  .then(() => {
    const loader = document.getElementById('app-loader');
    if (loader) {
      loader.remove();
    }
  });
