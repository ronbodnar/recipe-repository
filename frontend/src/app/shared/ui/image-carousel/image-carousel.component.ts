import { ChangeDetectionStrategy, Component, computed, input, signal } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { ButtonComponent } from '@shared/ui/button/button.component';

@Component({
  selector: 'app-image-carousel',
  imports: [MatIconModule, ButtonComponent, TranslatePipe],
  templateUrl: './image-carousel.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ImageCarouselComponent {
  readonly images = input<string[]>([]);
  readonly alt = input('');

  private readonly selectedIndex = signal(0);

  readonly currentIndex = computed(() =>
    Math.min(this.selectedIndex(), Math.max(this.images().length - 1, 0)),
  );
  readonly currentImage = computed(() => this.images()[this.currentIndex()] ?? null);
  readonly hasMultipleImages = computed(() => this.images().length > 1);

  previous(): void {
    if (!this.hasMultipleImages()) {
      return;
    }

    this.selectedIndex.update((index) => (index === 0 ? this.images().length - 1 : index - 1));
  }

  next(): void {
    if (!this.hasMultipleImages()) {
      return;
    }

    this.selectedIndex.update((index) => (index + 1) % this.images().length);
  }
}
