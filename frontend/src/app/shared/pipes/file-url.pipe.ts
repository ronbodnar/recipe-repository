import { Pipe } from '@angular/core';

@Pipe({
  name: 'fileUrl',
  standalone: true,
})
export class FileUrlPipe {
  private urls = new WeakMap<File, string>();

  transform(file: File | null): string | null {
    if (!file) return null;

    let url = this.urls.get(file);

    if (!url) {
      url = URL.createObjectURL(file);
      this.urls.set(file, url);
    }

    return url;
  }
}
