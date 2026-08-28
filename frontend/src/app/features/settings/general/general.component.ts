import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { Theme, ThemeService } from '@core/services/theme.service';
import { FormOrchestrator, SelectOption, InputSelectComponent } from '@ng-modular-forms/core';
import { TranslatePipe } from '@shared/pipes/translate.pipe';

@Component({
  selector: 'app-settings-general',
  imports: [ReactiveFormsModule, InputSelectComponent, TranslatePipe],
  templateUrl: './general.component.html',
  styleUrl: './general.component.css',
})
export class SettingsGeneralComponent extends FormOrchestrator implements OnInit {
  private formBuilder = inject(FormBuilder);
  private themeService = inject(ThemeService);

  themeOptions = signal<SelectOption[]>([
    { value: 'light', label: 'settings.general.themeOptions.light' },
    { value: 'dark', label: 'settings.general.themeOptions.dark' },
    { value: 'auto', label: 'settings.general.themeOptions.system' },
  ]);

  ngOnInit(): void {
    console.log('Theme:', this.themeService.theme());
    this.orchestrate({
      form: this.formBuilder.group({
        theme: [this.themeService.theme()],
      }),
      handlerRegistry: [],
      mapperRegistry: {},
    });
    const ctrl = this.form().get('theme');

    console.log('INIT FORM VALUE:', ctrl?.value);

    ctrl?.valueChanges.subscribe((v) => {
      console.log('THEME CHANGED:', v);
    });

    this.form()
      .get('theme')
      ?.valueChanges.subscribe((theme) => {
        this.onThemeChange(theme);
      });
  }

  onThemeChange(theme: string | number | null) {
    if (theme === null) return;
    this.themeService.setTheme(theme as Theme);
  }
}
