import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { CustomHttpResponse } from '../interface/customHttpResponse';
import { Customer } from '../interface/customer';
import { ResponseData } from '../interface/appstates';
import { Invoice } from '../interface/invoice';

@Injectable({
  providedIn: 'root',
})
export class CustomerService {
  private readonly apiUrl = 'http://localhost:8080/api/v1/customers';

  constructor(private http: HttpClient) {}

  customers$(): Observable<CustomHttpResponse<ResponseData>> {
    return this.http
      .get<CustomHttpResponse<ResponseData>>(this.apiUrl)
      .pipe(tap(console.log), catchError(this.handleError));
  }

  newCustomer$(
    customer: Customer
  ): Observable<CustomHttpResponse<ResponseData>> {
    return this.http
      .post<CustomHttpResponse<ResponseData>>(this.apiUrl, customer)
      .pipe(tap(console.log), catchError(this.handleError));
  }
  newInvoice$(): Observable<CustomHttpResponse<ResponseData>> {
    return this.http
      .get<CustomHttpResponse<ResponseData>>(`${this.apiUrl}/invoices/new`)
      .pipe(tap(console.log), catchError(this.handleError));
  }
  invoices$(): Observable<CustomHttpResponse<ResponseData>> {
    return this.http
      .get<CustomHttpResponse<ResponseData>>(`${this.apiUrl}/invoices`)
      .pipe(tap(console.log), catchError(this.handleError));
  }
  addInvoiceToCustomer$(
    id: string,
    invoice: Invoice
  ): Observable<CustomHttpResponse<ResponseData>> {
    console.warn(id);
    return this.http
      .post<CustomHttpResponse<ResponseData>>(
        `${this.apiUrl}/invoices/addtoCustomer/${id}`,
        invoice
      )
      .pipe(tap(console.log), catchError(this.handleError));
  }
  invoice$(id: string): Observable<CustomHttpResponse<ResponseData>> {
    return this.http
      .get<CustomHttpResponse<ResponseData>>(`${this.apiUrl}/invoices/${id}`)
      .pipe(tap(console.log), catchError(this.handleError));
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    console.log(error);
    let errorMessage: string;
    if (error.error instanceof ErrorEvent) {
      errorMessage = `A client error occurred - ${error.error.message}`;
    } else {
      if (error.error.reason) {
        errorMessage = error.error.reason;
        console.log(errorMessage);
      } else {
        errorMessage = `An error occurred - Error status ${error.status}`;
      }
    }
    return throwError(() => errorMessage);
  }
}
