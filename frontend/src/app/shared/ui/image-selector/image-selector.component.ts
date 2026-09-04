import {
  ChangeDetectionStrategy,
  Component,
  input,
  OnDestroy,
  output,
  signal,
} from '@angular/core';
import { ButtonComponent } from '@shared/ui/button/button.component';

export interface ImageSelectorExistingImage {
  id: string;
  src?: string;
  alt?: string;
}

export interface ImageSelectorRemovedImage {
  type: 'existing' | 'new';
  index: number;
  file?: File;
}

interface SelectedImage {
  file: File;
  previewUrl: string;
}

@Component({
  selector: 'app-image-selector',
  imports: [ButtonComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './image-selector.component.html',
})
export class ImageSelectorComponent implements OnDestroy {
  size = input<number>(36);
  existingImages = input<ImageSelectorExistingImage[]>([]);
  multiple = input<boolean>(false);
  rounded = input<boolean>(false);
  label = input<string>('Images');

  imagesSelected = output<File[]>();
  imageRemoved = output<ImageSelectorRemovedImage>();

  private readonly _selectedImages = signal<SelectedImage[]>([]);

  readonly selectedImages = this._selectedImages.asReadonly();

  onImagesSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.selectImages(input.files);
    input.value = '';
  }

  onImagesDropped(event: DragEvent): void {
    event.preventDefault();
    this.selectImages(event.dataTransfer?.files ?? null);
  }

  private selectImages(files: FileList | null): void {
    const imageFiles = Array.from(files ?? []).filter((file) => file.type.startsWith('image/'));

    if (!imageFiles.length) {
      return;
    }

    const selectedFiles = this.multiple() ? imageFiles : imageFiles.slice(0, 1);
    const newImages = selectedFiles.map((file) => ({
      file,
      previewUrl: URL.createObjectURL(file),
    }));

    if (this.multiple()) {
      this._selectedImages.update((images) => [...images, ...newImages]);
    } else {
      this.revokeSelectedImages();
      this._selectedImages.set(newImages);
    }

    this.imagesSelected.emit(selectedFiles);
  }

  removeExistingImage(index: number): void {
    this.imageRemoved.emit({ type: 'existing', index });
  }

  removeSelectedImage(index: number): void {
    const image = this.selectedImages()[index];
    if (!image) {
      return;
    }

    URL.revokeObjectURL(image.previewUrl);
    this._selectedImages.update((images) => images.filter((_, imageIndex) => imageIndex !== index));
    this.imageRemoved.emit({ type: 'new', index, file: image.file });
  }

  ngOnDestroy(): void {
    this.revokeSelectedImages();
  }

  private revokeSelectedImages(): void {
    for (const image of this.selectedImages()) {
      URL.revokeObjectURL(image.previewUrl);
    }
  }
}
