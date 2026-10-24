import { Component, inject, Input, signal } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatMenuModule } from '@angular/material/menu';
import { Router, RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { TranslatePipe } from '@ngx-translate/core';
import { MediaService } from '@core/services/media.service';
import { ButtonComponent } from '@shared/ui/button/button.component';
import { MatSidenav } from '@angular/material/sidenav';
import { StorageService } from '@core/services/storage.service';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatInputTextComponent } from '@ng-modular-forms/material';

@Component({
  selector: 'app-toolbar',
  imports: [
    MatTooltipModule,
    MatIconModule,
    MatMenuModule,
    MatToolbarModule,
    ButtonComponent,
    RouterLink,
    MatDividerModule,
    MatSlideToggleModule,
    TranslatePipe,
    ReactiveFormsModule,
    MatInputTextComponent,
  ],
  templateUrl: './toolbar.component.html',
  styleUrl: './toolbar.component.css',
})
export class ToolbarComponent {
  @Input() snav!: MatSidenav;

  private readonly router = inject(Router);
  private readonly mediaService = inject(MediaService);
  private readonly storageService = inject(StorageService);

  readonly isMobile = this.mediaService.isMobile;

  private _searchActive = signal(false);

  readonly searchActive = this._searchActive.asReadonly();

  readonly searchControl = new FormControl('', { nonNullable: true });

  toggleSidenav() {
    this.snav.toggle();
    const isOpened = this.snav.opened;
    this.storageService.setPreference('sidenav.isOpen', isOpened);
  }

  toggleSearch() {
    this._searchActive.update((value) => !value);
    if (!this.router.url.includes('searchValue')) {
      this.searchControl.setValue('');
    }
  }

  onSearch() {
    const searchValue = this.searchControl.value.trim();
    if (!searchValue) return;

    this.router.navigate(['/app/recipes/search'], { queryParams: { searchValue } });
    this.toggleSearch();
  }
}
