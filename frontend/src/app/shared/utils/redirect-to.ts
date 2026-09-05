import { Router } from '@angular/router';

export function redirectTo(router: Router, uri: string, extras?: Record<string, unknown>) {
  router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
    router.navigate([uri], extras);
  });
}
