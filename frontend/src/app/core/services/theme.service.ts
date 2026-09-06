import { effect, inject, Injectable, signal } from '@angular/core';
import { MediaService } from './media.service';
import { logDebug } from '@shared/utils/logging';

export type Theme = 'dark' | 'light' | 'auto';

@Injectable({
  providedIn: 'root',
})
export class ThemeService {
  private readonly mediaService = inject(MediaService);
  private readonly systemDarkMode = this.mediaService.isDarkMode;

  private readonly _theme = signal<Theme>('auto');
  public readonly theme = this._theme.asReadonly();

  constructor() {
    effect(() => {
      const theme = this.theme();
      const isSystemDark = this.systemDarkMode();

      const html = document.documentElement;
      if (theme === 'dark' || (theme === 'auto' && isSystemDark)) {
        html.classList.add('dark');
      } else {
        html.classList.remove('dark');
      }
    });
  }

  loadTheme() {
    const localTheme = localStorage.getItem('theme') as Theme;
    if (localTheme) {
      this._theme.set(localTheme);
      logDebug('Theme preference loaded from localStorage:', localTheme);
    } else {
      this._theme.set('auto');
      logDebug('Theme preference not found in localStorage. Defaulting to auto.');
    }
  }

  setTheme(theme: Theme) {
    this._theme.set(theme);
    localStorage.setItem('theme', theme);
  }
}
