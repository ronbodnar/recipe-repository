import { inject, Injectable } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { RouterStateSnapshot, TitleStrategy } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

@Injectable({ providedIn: 'root' })
export class AppTitleStrategy extends TitleStrategy {
  private readonly translate = inject(TranslateService);

  constructor(private readonly title: Title) {
    super();
  }

  updateTitle(snapshot: RouterStateSnapshot): void {
    const pageTitle = this.buildTitle(snapshot);
    if (pageTitle) {
      // We load asynchronously here instead of using instant because under certain conditions
      // when receiving errors, translations are not yet available and will display the translation key.
      this.translate.get([pageTitle, 'app.name']).subscribe((translated) => {
        this.title.setTitle(`${translated[pageTitle]} | ${translated['app.name']}`);
      });
    }
  }
}
