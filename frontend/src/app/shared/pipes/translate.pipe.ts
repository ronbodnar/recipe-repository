import { inject, Pipe, PipeTransform } from '@angular/core';
import { TranslateService } from '@core/services/translate.service';

@Pipe({
  name: 'translate',
})
export class TranslatePipe implements PipeTransform {
  private translate = inject(TranslateService);

  transform(text: string | number | undefined, params?: Record<string, string | number>): string {
    return this.translate.get(String(text), params) ?? String(text);
  }
}
