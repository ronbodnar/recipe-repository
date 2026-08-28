import { HttpErrorResponse } from '@angular/common/http';
import { environment } from '../../../environments/environment.development';
import { ErrorCode, TransportErrorCode } from '../interfaces/error-code.interface';

export type ApiErrorCode = ErrorCode | TransportErrorCode;

export interface FieldError {
  errorCode: ApiErrorCode;
  field: string | undefined;
  message?: string;
}

export class ApiError {
  timestamp: Date;
  status: number;
  instance: string;
  code: ApiErrorCode;
  fieldErrors: FieldError[];

  constructor(data: Partial<ApiError>) {
    if (!environment.production) {
      console.log('Deconstructing ApiError from data:', data);
    }

    this.timestamp = data.timestamp ? new Date(data.timestamp) : new Date();
    this.status = data.status ?? 0;
    this.instance = data.instance ?? '';
    this.fieldErrors = data.fieldErrors ?? [];
    this.code = data.code ?? TransportErrorCode.SERVER_UNAVAILABLE;
  }

  static fromResponse(error: HttpErrorResponse): ApiError {
    try {
      return new ApiError(error ?? {});
    } catch (_error) {
      return new ApiError({});
    }
  }

  hasFormError(): boolean {
    return this.fieldErrors?.length > 0;
  }
}
