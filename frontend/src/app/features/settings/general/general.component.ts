import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ThemeService } from '@core/services/theme.service';
import { FormOrchestrator, SelectOption, InputSelectComponent } from '@ng-modular-forms/core';
import { TranslatePipe } from '@ngx-translate/core';
import { GeneralSettingsHandler } from './general.handler';
import { TranslateService } from '@ngx-translate/core';

const VALID_LANGUAGES: Record<string, string> = {
  en_US: 'settings.general.languageOptions.en_US',
  es_MX: 'settings.general.languageOptions.es_MX',
};

@Component({
  selector: 'app-settings-general',
  imports: [ReactiveFormsModule, InputSelectComponent, TranslatePipe],
  providers: [GeneralSettingsHandler],
  templateUrl: './general.component.html',
  styleUrl: './general.component.css',
})
export class SettingsGeneralComponent extends FormOrchestrator implements OnInit {
  private formBuilder = inject(FormBuilder);
  private themeService = inject(ThemeService);
  private translate = inject(TranslateService);

  themeOptions = signal<SelectOption[]>([
    { value: 'light', label: 'settings.general.themeOptions.light' },
    { value: 'dark', label: 'settings.general.themeOptions.dark' },
    { value: 'auto', label: 'settings.general.themeOptions.system' },
  ]);

  languageOptions = signal<SelectOption[]>(
    Object.keys(VALID_LANGUAGES).map((lang) => ({
      value: lang,
      label: VALID_LANGUAGES[lang],
    })),
  );

  private readonly handler = inject(GeneralSettingsHandler);

  ngOnInit(): void {
    this.orchestrate({
      form: this.formBuilder.group({
        theme: [this.themeService.theme()],
        language: [this.translate.currentLang()],
      }),
      handlerRegistry: [this.handler],
    });
  }
}
