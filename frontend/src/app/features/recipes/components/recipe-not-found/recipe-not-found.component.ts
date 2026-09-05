import { Component } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-recipe-not-found',
  imports: [MatIconModule, TranslatePipe],
  templateUrl: './recipe-not-found.component.html',
})
export class RecipeNotFoundComponent {}
