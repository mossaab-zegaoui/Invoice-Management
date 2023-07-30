import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, tap, throwError } from 'rxjs';
import { CustomHttpResponse } from '../model/CustomHttpResponse';
import { User } from '../model/User';
import { Profile } from '../model/appstates';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  readonly apiUrl = 'http://localhost:8080/api/v1/users';
  constructor(private http: HttpClient) {}

  register$(user: User): Observable<CustomHttpResponse<User>> {
    return this.http
      .post<CustomHttpResponse<User>>(`${this.apiUrl}/register`, user)
      .pipe(tap(console.log), catchError(this.handleError));
  }
  login$(
    email: string,
    password: string
  ): Observable<CustomHttpResponse<Profile>> {
    return this.http
      .post<CustomHttpResponse<Profile>>(`${this.apiUrl}/login`, {
        email,
        password,
      })
      .pipe(tap(console.log), catchError(this.handleError));
  }

  private handleError(error: any): Observable<never> {
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
