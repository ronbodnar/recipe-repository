import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'toString',
  standalone: true,
})
export class ToStringPipe implements PipeTransform {
  transform(text: string | number | undefined): string {
    return String(text);
  }
}
