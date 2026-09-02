import { Component, EventEmitter, Input, OnDestroy, Output, signal } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';

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
  imports: [MatIconModule, MatTooltipModule],
  templateUrl: './image-selector.component.html',
})
export class ImageSelectorComponent implements OnDestroy {
  @Input() size = 36;
  @Input() existingImages: readonly ImageSelectorExistingImage[] = [];
  @Input() multiple = false;
  @Input() label = 'Images';

  @Output() imagesSelected = new EventEmitter<File[]>();
  @Output() imageRemoved = new EventEmitter<ImageSelectorRemovedImage>();

  private readonly _selectedImages = signal<SelectedImage[]>([]);

  readonly selectedImages = this._selectedImages.asReadonly();

  onImagesSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const files = Array.from(input.files ?? []).filter((file) => file.type.startsWith('image/'));
    input.value = '';

    if (!files.length) {
      return;
    }

    const selectedFiles = this.multiple ? files : files.slice(0, 1);
    const newImages = selectedFiles.map((file) => ({
      file,
      previewUrl: URL.createObjectURL(file),
    }));

    if (this.multiple) {
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
