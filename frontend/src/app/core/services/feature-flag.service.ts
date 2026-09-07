import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class FeatureFlagService {
  featureFlags: Record<string, boolean> = {
    groups: false,
    discover: true,
  };

  isFeatureEnabled(featureName: string): boolean {
    return this.featureFlags[featureName] || false;
  }
}
