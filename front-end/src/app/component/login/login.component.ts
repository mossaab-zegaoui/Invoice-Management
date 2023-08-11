import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import {
  BehaviorSubject,
  Observable,
  catchError,
  map,
  of,
  startWith,
} from 'rxjs';
import { DataState } from 'src/app/enum/dataState.enum';
import { Token } from 'src/app/enum/token.enum';
import { LoginState } from 'src/app/interface/appstates';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit {
  readonly DataState = DataState;
  loginState$: Observable<LoginState> = of({
    dataState: DataState.LOADED,
  });
  phoneSubject = new BehaviorSubject<string>('');

  constructor(private userService: UserService, private router: Router) {}
  ngOnInit(): void {
    this.userService.isAuthenticated()
      ? this.router.navigate(['/'])
      : this.router.navigate(['/login']);
  }
  login(loginForm: NgForm) {
    this.loginState$ = this.userService
      .login$(loginForm.value.email, loginForm.value.password)
      .pipe(
        map((response) => {
          if (response.data?.user.usingMfa) {
            this.phoneSubject.next(response.data?.user.phone!);
            return {
              dataState: DataState.LOADED,
              loginSuccess: false,
              usingMfa: true,
              phone: this.phoneSubject.value,
            };
          } else {
            loginForm.reset();
            localStorage.setItem(
              Token.ACCESS_TOKEN,
              response.data!.access_token
            );
            localStorage.setItem(
              Token.REFRESH_TOKEN,
              response.data!.refresh_token
            );
            this.router.navigate(['/']);
            return { dataState: DataState.LOADED, loginSuccess: true };
          }
        }),
        startWith({ dataState: DataState.LOADING, loginSuccess: false }),
        catchError((error) =>
          of({
            dataState: DataState.ERROR,
            loginSuccess: false,
            error,
          })
        )
      );
  }
  verifyCode(verifyCodeForm: NgForm) {
    this.loginState$ = this.userService
      .verifyCode$(this.phoneSubject.value, verifyCodeForm.value.code)
      .pipe(
        map((response) => {
          localStorage.setItem(Token.ACCESS_TOKEN, response.data!.access_token);
          localStorage.setItem(
            Token.REFRESH_TOKEN,
            response.data!.refresh_token
          );
          this.router.navigate(['/']);
          return {
            dataState: DataState.LOADED,
            data: response,
            loginSuccess: true,
          };
        }),
        startWith({
          dataState: DataState.LOADING,
          loginSuccess: false,
          usingMfa: true,
          phone: this.phoneSubject.value,
        }),
        catchError((error) => {
          return of({
            dataState: DataState.ERROR,
            loginSuccess: false,
            usingMfa: true,
            phone: this.phoneSubject.value,
            error,
          });
        })
      );
  }

  loginPage() {
    this.loginState$ = of({ dataState: DataState.LOADED, loginSuccess: false });
  }
}
