import { MediaMatcher } from '@angular/cdk/layout';
import { inject, Injectable, OnDestroy, signal } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class MediaService implements OnDestroy {
  private media = inject(MediaMatcher);

  private readonly mobileQuery = this.media.matchMedia('(max-width: 600px)');
  private readonly smallScreenQuery = this.media.matchMedia('(max-width: 1300px)');
  private readonly darkModeQuery = this.media.matchMedia('(prefers-color-scheme: dark)');

  public readonly isMobile = signal(this.mobileQuery.matches);
  public readonly isSmallScreen = signal(this.smallScreenQuery.matches);
  public readonly isDarkMode = signal(this.darkModeQuery.matches);

  private readonly mobileListener = () => this.isMobile.set(this.mobileQuery.matches);
  private readonly smallScreenListener = () =>
    this.isSmallScreen.set(this.smallScreenQuery.matches);
  private readonly darkModeListener = () => {
    this.isDarkMode.set(this.darkModeQuery.matches);
  };

  constructor() {
    this.mobileQuery.addEventListener('change', this.mobileListener);
    this.smallScreenQuery.addEventListener('change', this.smallScreenListener);
    this.darkModeQuery.addEventListener('change', this.darkModeListener);
  }

  ngOnDestroy(): void {
    this.mobileQuery.removeEventListener('change', this.mobileListener);
    this.smallScreenQuery.removeEventListener('change', this.smallScreenListener);
    this.darkModeQuery.removeEventListener('change', this.darkModeListener);
  }
}
