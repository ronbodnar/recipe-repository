import { ChangeDetectionStrategy, Component, inject, input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router';
import { ImageService } from '@core/services/image.service';
import { RecipeListSource } from '@features/recipes/recipe-list/recipe-list.component';
import { RecipeSummary } from '@features/recipes/recipe.types';
import { TranslatePipe } from '@ngx-translate/core';
import { ImageCarouselComponent } from '@shared/ui/image-carousel/image-carousel.component';

@Component({
  selector: 'app-recipe-card-list',
  imports: [ImageCarouselComponent, MatIconModule, RouterLink, TranslatePipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './recipe-card-list.component.html',
})
export class RecipeCardListComponent {
  private readonly imageService = inject(ImageService);

  readonly source = input.required<RecipeListSource>();
  readonly recipes = input.required<RecipeSummary[]>();

  readonly recipeImageUrls = (imageIds: string[]) =>
    imageIds.map((imageId) => this.imageService.getImageUrl(imageId));
}
