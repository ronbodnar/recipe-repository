import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { delay, Observable } from 'rxjs';
import { PaginatedResponse } from '@core/interfaces/paginated-response.interface';
import { environment } from '@env';

type ApiData = object;

export interface FetchOptions {
  parameters?: ApiData;
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH';
  sendParametersAsJSON?: boolean;
  verbose?: boolean;
  requestBody?: ApiData;
}

@Injectable({
  providedIn: 'root',
})
export class FetchApiService {
  private http = inject(HttpClient);

  postData<T>(endpoint: string, data: ApiData): Observable<T> {
    return this.fetch<T>(endpoint, { method: 'POST', requestBody: data });
  }

  putData<T>(endpoint: string, data: ApiData): Observable<T> {
    return this.fetch<T>(endpoint, { method: 'PUT', requestBody: data });
  }

  patchData<T>(endpoint: string, data: ApiData): Observable<T> {
    return this.fetch<T>(endpoint, { method: 'PATCH', requestBody: data });
  }

  deleteData<T>(endpoint: string, parameters?: ApiData): Observable<T> {
    return this.fetch<T>(endpoint, { method: 'DELETE', parameters: parameters });
  }

  getData<T>(endpoint: string, parameters?: ApiData): Observable<T> {
    return this.fetch<T>(endpoint, { method: 'GET', parameters: parameters });
  }

  /**
   * Performs an HTTP request to fetch paginated data from the backend API.
   * @param endpoint The endpoint, excluding the domain, to which the request will be sent.
   * @param options An object containing the options for the request, including parameters, method, and request body.
   * @returns An Observable of the paginated response containing the requested data.
   */
  fetchPaginatedData<ContentType>(
    endpoint: string,
    options: FetchOptions = {},
  ): Observable<PaginatedResponse<ContentType>> {
    return this.fetch<PaginatedResponse<ContentType>>(endpoint, {
      parameters: options.parameters,
      method: options.method,
      sendParametersAsJSON: options.sendParametersAsJSON,
      verbose: options.verbose,
      requestBody: options.requestBody,
    });
  }

  /**
   * Performs an HTTP request to fetch http data via HttpClient.
   * @param endpoint The endpoint, excluding the domain, to which the request will be sent.
   * @param parameters An object containing the parameters to be converted into the query string.
   * @param method The HTTP method to be used. Default: 'GET'.
   * @param requestBody The body to be sent with aplicable request types. Cannot be used with GET requests.
   * @returns An Observable of the response body.
   */
  fetch<T>(endpoint: string, options: FetchOptions = {}): Observable<T> {
    if (!endpoint) {
      throw new Error('Endpoint is required.');
    }

    const { parameters, method = 'GET', requestBody } = options;
    const url = `${environment.apiUrl}/${endpoint}`;

    return this.http
      .request<T>(method, url, {
        body: requestBody,
        params: this.getHttpParams(parameters),
      })
      .pipe(delay(environment.production ? 0 : 500));
  }

  /**
   * Builds the HttpParams object to be used in the HTTP request.
   * @param parameters The parameters to be converted into the query string.
   * @returns The constructed HttpParams object.
   */
  private getHttpParams(parameters?: ApiData, sendParametersAsJSON: boolean = false): HttpParams {
    let params = new HttpParams();
    if (!parameters) return params;

    Object.entries(parameters).forEach(([key, value]) => {
      if (value !== null && value !== undefined) {
        const stringValue = Array.isArray(value) ? value.join(',') : String(value);
        params = params.set(key, sendParametersAsJSON ? JSON.stringify(stringValue) : stringValue);
      }
    });

    return params;
  }
}
