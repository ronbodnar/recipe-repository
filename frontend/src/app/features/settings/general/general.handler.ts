import { inject } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { Theme, ThemeService } from '@core/services/theme.service';
import { FormHandlerBase } from '@ng-modular-forms/core';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';

type Controls = {
  theme: FormControl<string>;
  language: FormControl<string>;
};

export class GeneralSettingsHandler extends FormHandlerBase<Controls> {
  private readonly themeService = inject(ThemeService);
  private readonly translateService = inject(TranslateService);

  override getReactiveLogic(form: FormGroup): Subscription {
    const sub = new Subscription();

    sub.add(
      form.get('theme')?.valueChanges.subscribe((theme) => {
        this.onThemeChange(theme);
      }),
    );

    sub.add(
      form.get('language')?.valueChanges.subscribe((language) => {
        this.onLanguageChange(language);
      }),
    );

    return sub;
  }

  onThemeChange(theme: string | number | null) {
    if (theme === null) return;
    this.themeService.setTheme(theme as Theme);
  }

  onLanguageChange(language: string) {
    if (!language) return;
    this.translateService.use(language);
  }
}
