import { Injectable } from '@angular/core';
import { environment } from '@env/environment';

@Injectable({
  providedIn: 'root',
})
export class ImageService {
  private readonly urlPrefix = `${environment.cdnUrl}/recipe-images`;

  getImageUrl(imageId: string): string {
    return `${this.urlPrefix}/${imageId}/original.webp`;
  }
}
