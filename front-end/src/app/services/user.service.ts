import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, tap, throwError } from 'rxjs';
import { User } from '../interface/user';
import { Profile, ResponseData } from '../interface/appstates';
import { Token } from '../enum/token.enum';
import { JwtHelperService } from '@auth0/angular-jwt';
import { CustomHttpResponse } from '../interface/customHttpResponse';
import { ResolveData } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private readonly apiUrl = 'http://localhost:8080/api/v1/users';
  private jwtHelper = new JwtHelperService();
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

  logOut() {
    localStorage.removeItem(Token.ACCESS_TOKEN);
    localStorage.removeItem(Token.REFRESH_TOKEN);
  }
  isAuthenticated(): boolean {
    return this.jwtHelper.decodeToken<string>(
      localStorage.getItem(Token.ACCESS_TOKEN)!
    ) &&
      !this.jwtHelper.isTokenExpired(localStorage.getItem(Token.ACCESS_TOKEN))
      ? true
      : false;
  }
  profile$(): Observable<CustomHttpResponse<ResponseData>> {
    return this.http
      .get<CustomHttpResponse<ResponseData>>(`${this.apiUrl}/profile`)
      .pipe(tap(console.log), catchError(this.handleError));
  }
  updateUserDetails$(
    user: User,
    id: number
  ): Observable<CustomHttpResponse<ResponseData>> {
    return this.http
      .put<CustomHttpResponse<ResponseData>>(`${this.apiUrl}/${id}`, user)
      .pipe(tap(console.log, catchError(this.handleError)));
  }

  updateUserPassword$(form: {
    currentPassword: string;
    newPassword: string;
    confirmNewPassword: string;
  }): Observable<CustomHttpResponse<ResponseData>> {
    return this.http
      .put<CustomHttpResponse<ResponseData>>(
        `${this.apiUrl}/updatePassword`,
        form
      )
      .pipe(tap(console.log), catchError(this.handleError));
  }
  updateUserRole$(
    roleName: string
  ): Observable<CustomHttpResponse<ResponseData>> {
    return this.http
      .put<CustomHttpResponse<ResponseData>>(
        `${this.apiUrl}/updateUserRole`,
        roleName
      )
      .pipe(tap(console.log), catchError(this.handleError));
  }
  updateUserAccountSettings$(accountSettingsForm: {
    isNotEnabled: boolean;
    isNotLocked: boolean;
  }): Observable<CustomHttpResponse<ResponseData>> {
    return this.http
      .put<CustomHttpResponse<ResponseData>>(
        `${this.apiUrl}/updateUserAccountSettings`,
        accountSettingsForm
      )
      .pipe(tap(console.log), catchError(this.handleError));
  }

  toggleMfa(): Observable<CustomHttpResponse<ResponseData>> {
    return this.http
      .put<CustomHttpResponse<ResponseData>>(`${this.apiUrl}/toggleMfa`, null)
      .pipe(tap(console.log), catchError(this.handleError));
  }
  uploadProfileImage$(
    formData: FormData
  ): Observable<CustomHttpResponse<ResponseData>> {
    return this.http
      .put<CustomHttpResponse<ResponseData>>(
        `${this.apiUrl}/uploadImage`,
        formData
      )
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
