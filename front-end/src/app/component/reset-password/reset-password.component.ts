import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import {
  BehaviorSubject,
  Observable,
  catchError,
  map,
  of,
  startWith,
} from 'rxjs';
import { DataState } from 'src/app/enum/dataState.enum';
import { RegisterState } from 'src/app/interface/appstates';
import { CustomHttpResponse } from 'src/app/interface/customHttpResponse';
import { State } from 'src/app/interface/state';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css'],
})
export class ResetPasswordComponent {
  readonly DataState = DataState;
  resetPasswordState$: Observable<RegisterState> = of({
    dataState: DataState.LOADED,
  });
  isLoadingSubject = new BehaviorSubject<boolean>(false);
  isLoading$ = this.isLoadingSubject.asObservable();
  constructor(private userService: UserService) {}

  resetPassword(resetPasswordForm: NgForm) {
    this.isLoadingSubject.next(true);
    this.resetPasswordState$ = this.userService
      .resetPassword$(resetPasswordForm.value.email)
      .pipe(
        map((response) => {
          this.isLoadingSubject.next(false);
          return { dataState: DataState.LOADED, registerSuccess:true, message: response?.message };
        }),
        startWith({ dataState: DataState.LOADING }),
        catchError((error) => {
          return of({ dataState: DataState.ERROR, error, registerSuccess: false });
        })
      );
  }
}
