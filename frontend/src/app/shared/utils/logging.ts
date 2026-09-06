import { environment } from '@env';

export function logDebug(message: string, ...optionalParams: unknown[]): void {
  if (environment.production) {
    return;
  }
  console.log(message, ...optionalParams);
}

export function logError(message: string, ...optionalParams: unknown[]): void {
  console.error(message, ...optionalParams);
}
