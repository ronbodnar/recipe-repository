import { ChangeDetectionStrategy, Component, computed, inject, input, output } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router';
import { ImageService } from '@core/services/image.service';
import { RecipeListSource } from '@features/recipes/recipe-list/recipe-list.component';
import { RecipeSummary } from '@features/recipes/recipe.types';
import { TranslatePipe } from '@ngx-translate/core';
import { ImageCarouselComponent } from '@shared/ui/image-carousel/image-carousel.component';
import { ButtonComponent } from '@shared/ui/button/button.component';

@Component({
  selector: 'app-recipe-card-list',
  imports: [ImageCarouselComponent, MatIconModule, RouterLink, TranslatePipe, ButtonComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './recipe-card-list.component.html',
})
export class RecipeCardListComponent {
  private readonly imageService = inject(ImageService);

  readonly source = input.required<RecipeListSource>();
  readonly recipes = input.required<RecipeSummary[]>();
  readonly totalRecipes = input<number>(0);
  readonly totalPages = input<number>(0);
  readonly currentPage = input<number>(0);
  readonly pageChange = output<number>();

  readonly pages = computed<(number | null)[]>(() => {
    const totalPages = this.totalPages();
    const currentPage = this.currentPage();

    if (totalPages <= 7) {
      return Array.from({ length: totalPages }, (_, page) => page);
    }

    const visiblePages = new Set([0, totalPages - 1]);
    for (
      let page = Math.max(1, currentPage - 1);
      page <= Math.min(totalPages - 2, currentPage + 1);
      page++
    ) {
      visiblePages.add(page);
    }

    const pages: (number | null)[] = [];
    let previousPage: number | null = null;
    for (const page of [...visiblePages].sort((firstPage, secondPage) => firstPage - secondPage)) {
      if (previousPage !== null && page > previousPage + 1) {
        pages.push(null);
      }
      pages.push(page);
      previousPage = page;
    }

    return pages;
  });

  changePage(page: number): void {
    if (page >= 0 && page < this.totalPages() && page !== this.currentPage()) {
      this.pageChange.emit(page);
    }
  }

  readonly recipeImageUrls = (imageIds: string[]) =>
    imageIds.map((imageId) => this.imageService.getImageUrl(imageId));
}
