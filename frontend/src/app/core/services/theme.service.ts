import { effect, inject, Injectable, signal } from '@angular/core';
import { MediaService } from './media.service';
import { environment } from '../../../environments/environment';

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
      if (!environment.production) {
        console.log('Theme set to', localTheme);
      }
    } else {
      this._theme.set('auto');
      if (!environment.production) {
        console.log('Theme preference not found. Set to auto');
      }
    }
  }

  setTheme(theme: Theme) {
    this._theme.set(theme);
    localStorage.setItem('theme', theme);
  }
}
