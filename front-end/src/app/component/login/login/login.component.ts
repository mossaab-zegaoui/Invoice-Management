import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Observable, catchError, map, of, startWith } from 'rxjs';
import { DataState } from 'src/app/enum/dataState.enum';
import { Token } from 'src/app/enum/token.enum';
import { LoginState } from 'src/app/model/appstates';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  readonly DataState = DataState;
  loginState$: Observable<LoginState> = of({
    dataState: DataState.LOADED,
  });

  constructor(private userService: UserService) {}
  login(loginForm: NgForm) {
    this.loginState$ = this.userService
      .login$(loginForm.value.email, loginForm.value.password)
      .pipe(
        map((response) => {
          loginForm.reset();
          localStorage.setItem(Token.ACCESS_TOKEN, response.data.access_token);
          localStorage.setItem(
            Token.REFRESH_TOKEN,
            response.data.refresh_token
          );
          return { dataState: DataState.LOADED, loginSuccess: true };
        }),
        startWith({ dataState: DataState.LOADING, loginSuccess: false }),
        catchError((err) =>
          of({
            dataState: DataState.ERROR,
            loginSuccess: false,
            error: err,
          })
        )
      );
  }

}
