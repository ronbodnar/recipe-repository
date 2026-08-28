import { Injectable, signal } from '@angular/core';

export interface TranslationDictionary {
  [key: string]: string | TranslationDictionary;
}

@Injectable({
  providedIn: 'root',
})
export class TranslateService {
  private translations = signal<TranslationDictionary>({});

  async loadLanguage(lang: string) {
    const translations = await import(`../../../assets/i18n/${lang}.json`);
    this.translations.set(translations.default);
  }

  get(key: string, params?: Record<string, unknown>): string {
    if (!key) return '';

    let translation = this.resolveTranslation(key);

    if (params && translation) {
      Object.entries(params).forEach(([param, value]) => {
        const placeholder = new RegExp(`{{${param}}}`, 'g');
        translation = translation.replace(placeholder, String(value ?? param));
      });
    }

    return translation;
  }

  private resolveTranslation(key: string): string {
    const keys = key.split('.');
    let current: TranslationDictionary | string = this.translations();

    for (const k of keys) {
      if (current && typeof current === 'object' && k in current) {
        current = current[k];
      } else {
        return key;
      }
    }

    return typeof current === 'string' ? current : key;
  }
}
