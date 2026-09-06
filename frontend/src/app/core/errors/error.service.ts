import { Injectable, inject } from '@angular/core';
import { AbstractControl, FormArray, FormGroup } from '@angular/forms';
import { catchError, EMPTY, Observable } from 'rxjs';
import { FrontendError } from './error.handler';
import { ApiError } from '@core/models/api-error.model';
import { ErrorCode, TransportErrorCode } from '@core/interfaces/error-code.interface';
import { FetchApiService } from '@core/services/fetch-api.service';
import { SnackbarService, SnackbarType } from '@shared/ui/snackbar.component';
import { asCamelCase } from '@shared/utils/as-camel-case';
import { logDebug } from '@shared/utils/logging';

@Injectable({ providedIn: 'root' })
export class ErrorService {
  private snackBar = inject(SnackbarService);
  private fetchApiService = inject(FetchApiService);

  displayError(error: ApiError): void {
    switch (error.code) {
      case ErrorCode.INTERNAL_ERROR:
      case ErrorCode.NOT_AUTHORIZED:
      case ErrorCode.MESSAGE_NOT_READABLE:
      case ErrorCode.MESSAGE_NOT_WRITABLE:
      case TransportErrorCode.NETWORK_TIMEOUT:
      case TransportErrorCode.SERVER_UNAVAILABLE:
        this.snackBar.openSnackBar(SnackbarType.ERROR, 'errors.' + asCamelCase(error.code));
        break;
    }
  }

  isValidationError(error: ApiError): boolean {
    return error.code === ErrorCode.VALIDATION_FAILED;
  }

  public populateFormErrors(form: FormGroup, error: ApiError): Observable<never> {
    error.fieldErrors = error.fieldErrors || [];

    logDebug('Populating form errors', error);

    if (error.fieldErrors.length === 0) {
      form.markAllAsTouched();
      form.setErrors({
        ...(form.errors ?? {}),
        custom: 'errors.' + asCamelCase(error.code ?? TransportErrorCode.SERVER_UNAVAILABLE),
      });
    } else {
      error.fieldErrors.forEach((error) => {
        const { field, errorCode: _errorCode } = error;

        const errorCode = _errorCode.replace('FIELD_', '');

        if (field == null) {
          form.markAllAsTouched();
          form.setErrors({
            ...(form.errors ?? {}),
            custom: 'errors.fields.' + asCamelCase(errorCode),
          });
          return;
        }

        const control = this.findControl(form, field);

        control?.markAsTouched();
        control?.setErrors({
          ...(control?.errors ?? {}),
          custom: 'errors.fields.' + asCamelCase(errorCode),
        });
      });
    }

    return EMPTY;
  }

  public logFrontendError(payload: FrontendError): void {
    this.fetchApiService
      .fetch('frontend-logs', { method: 'POST', requestBody: payload })
      .pipe(catchError(() => EMPTY))
      .subscribe();
  }

  private findControl(form: AbstractControl, field: string): AbstractControl | null {
    const direct = form.get(field);

    if (direct) {
      return direct;
    }

    if (form instanceof FormGroup) {
      for (const [_name, control] of Object.entries(form.controls)) {
        const found = this.findControl(control, field);

        if (found) {
          return found;
        }
      }
    }

    if (form instanceof FormArray) {
      for (const control of form.controls) {
        const found = this.findControl(control, field);

        if (found) {
          return found;
        }
      }
    }

    return null;
  }
}
