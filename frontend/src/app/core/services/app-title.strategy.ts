import { inject, Injectable } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { RouterStateSnapshot, TitleStrategy } from '@angular/router';
import { TranslateService } from './translate.service';

@Injectable({ providedIn: 'root' })
export class AppTitleStrategy extends TitleStrategy {
  private readonly translateService = inject(TranslateService);

  constructor(private readonly title: Title) {
    super();
  }

  updateTitle(snapshot: RouterStateSnapshot): void {
    const pageTitle = this.buildTitle(snapshot);
    if (pageTitle) {
      // If using a library or external files, load asynchronously here instead of using instantly because under certain
      // conditions when receiving http errors, translations are not yet available and will display the translation key.
      const appName = this.translateService.get('app.name');
      const title = this.translateService.get(pageTitle);

      this.title.setTitle(`${title} | ${appName}`);
    }
  }
}
