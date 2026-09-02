import { inject, Injectable } from '@angular/core';
import { environment } from '@env/environment';
import { FetchApiService } from './fetch-api.service';
import { resizeImage } from '@shared/utils/resize-image';
import { from, switchMap } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ImageService {
  private readonly fetchApi = inject(FetchApiService);
  private readonly urlPrefix = `${environment.cdnUrl}/recipe-images`;

  getImageUrl(imageId: string): string {
    return `${this.urlPrefix}/${imageId}/original.webp`;
  }

  uploadImages(images: File[], purpose: 'RECIPE' | 'PROFILE' = 'RECIPE') {
    const resizedImages = Promise.all((images ?? []).map((image) => resizeImage(image)));

    return from(resizedImages).pipe(
      switchMap((images) => {
        console.log('Resized images:', images);

        const formData = new FormData();
        formData.append('purpose', purpose);

        images.forEach((image) => formData.append('files', image));

        return this.fetchApi.postData<string[]>(`images`, formData);
      }),
    );
  }
}
