import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest,} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {BehaviorSubject, catchError, Observable, switchMap, throwError,} from 'rxjs';
import {Token} from '../enum/token.enum';
import {Profile} from '../interface/appstates';
import {CustomHttpResponse} from '../interface/customHttpResponse';
import {UserService} from '../services/user.service';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {
    private isTokenRefreshing: boolean = false;
    private refreshTokenSubject =
        new BehaviorSubject<CustomHttpResponse<Profile> | null>(null);

    constructor(private userService: UserService) {
    }

    intercept(
        request: HttpRequest<any>,
        next: HttpHandler
    ): Observable<HttpEvent<any>> {
        if (
            request.url.includes('login') ||
            request.url.includes('register') ||
            request.url.includes('refresh')
        ) {
            return next.handle(request);
        }
        return next
            .handle(
                this.addAuthorizationTokenHeader(
                    request,
                    localStorage.getItem(Token.ACCESS_TOKEN)!
                )
            )
            .pipe(
                catchError((error: HttpErrorResponse) => {
                    if (
                        error.status === 401 &&
                        error.error?.reason.includes('expired')
                    ) {
                        console.log(error);
                        return this.handleRefreshToken(request, next);
                    } else {
                        return throwError(() => error);
                    }
                })
            );
    }

    handleRefreshToken(
        request: HttpRequest<unknown>,
        next: HttpHandler
    ): Observable<HttpEvent<unknown>> {
        if (!this.isTokenRefreshing) {
            this.isTokenRefreshing = true;
            this.refreshTokenSubject.next(null);
            return this.userService.refreshToken$().pipe(
                switchMap((response) => {
                    console.log("REFRESHING TOKEN ...");
                    this.refreshTokenSubject.next(response);

                    this.isTokenRefreshing = false;
                    return next.handle(
                        this.addAuthorizationTokenHeader(
                            request,
                            response.data?.access_token!
                        )
                    );
                })
            );
        } else {
            return this.refreshTokenSubject.pipe(
                switchMap((response) => {
                    return next.handle(
                        this.addAuthorizationTokenHeader(
                            request,
                            response?.data?.access_token!
                        )
                    );
                })
            );
        }
    }

    addAuthorizationTokenHeader(
        request: HttpRequest<any>,
        token: string
    ): HttpRequest<unknown> {
        return request.clone({setHeaders: {Authorization: `Bearer ${token}`}});
    }
}
