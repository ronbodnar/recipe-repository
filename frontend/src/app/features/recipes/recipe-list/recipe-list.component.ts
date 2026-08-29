import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { FluidContainerComponent } from '@shared/ui/fluid-container/fluid-container.component';
import { ButtonComponent } from '@shared/ui/button/button.component';
import { RecipeSummary } from '../recipe.types';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { FullPageLoaderComponent } from '@shared/ui/full-page-loader.component';
import { RecipeService } from '../recipe.service';
import { ImageService } from '@core/services/image.service';

@Component({
  selector: 'app-recipe-list',
  imports: [
    CommonModule,
    RouterLink,
    MatIconModule,
    FluidContainerComponent,
    ButtonComponent,
    TranslatePipe,
    FullPageLoaderComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './recipe-list.component.html',
  styleUrl: './recipe-list.component.css',
})
export class RecipeListComponent {
  private translate = inject(TranslateService);
  private recipeService = inject(RecipeService);
  readonly imageService = inject(ImageService);

  private _loadedRecipes = signal<RecipeSummary[]>([]);
  private _isLoading = signal(true);
  private _hasError = signal(false);

  readonly loadedRecipes = this._loadedRecipes.asReadonly();
  readonly isLoading = this._isLoading.asReadonly();
  readonly hasError = this._hasError.asReadonly();

  readonly appName = this.translate.instant('app.name');

  readonly hasRecipes = computed(() => this.loadedRecipes().length > 0);

  pageSize = 20;
  page = 0;

  ngOnInit(): void {
    this.recipeService.loadPaginatedRecipes(this.page, this.pageSize).subscribe({
      next: (data) => {
        console.log('Fetched recipes', data);
        this._loadedRecipes.set(data.content);
        this._hasError.set(false);
        this._isLoading.set(false);
      },
      error: (error) => {
        console.error('Failed to fetch recipes:', error);
        this._hasError.set(true);
        this._isLoading.set(false);
      },
    });
  }
}
