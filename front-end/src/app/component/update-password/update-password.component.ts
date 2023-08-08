import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute, ParamMap } from '@angular/router';
import {
  BehaviorSubject,
  Observable,
  catchError,
  map,
  of,
  startWith,
  switchMap,
} from 'rxjs';
import { DataState } from 'src/app/enum/dataState.enum';
import { updatePasswordState } from 'src/app/interface/appstates';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-update-password',
  templateUrl: './update-password.component.html',
  styleUrls: ['./update-password.component.css'],
})
export class UpdatePasswordComponent implements OnInit {
  readonly DataState = DataState;
  updatePasswordState$: Observable<updatePasswordState> = of({
    dataState: DataState.LOADED,
    resetPasswordSuccess: false,
  });
  isLoadingSubject = new BehaviorSubject<boolean>(false);
  isLoading$ = this.isLoadingSubject.asObservable();
  TOKEN = 'token';
  tokenSujbect = new BehaviorSubject<string>('');
  constructor(
    private userService: UserService,
    private activatedRoute: ActivatedRoute
  ) {}
  ngOnInit(): void {
    this.updatePasswordState$ = this.activatedRoute.queryParamMap.pipe(
      switchMap((params: ParamMap) => {
        this.tokenSujbect.next(params.get(this.TOKEN)!);
        return this.userService
          .validateResetPassword$(this.tokenSujbect.value)
          .pipe(
            map(() => {
              return {
                dataState: DataState.LOADED,
                resetPasswordSuccess: true,
              };
            }),
            startWith({
              dataState: DataState.LOADED,
              resetPasswordSuccess: false,
            }),
            catchError((error) => {
              return of({
                dataState: DataState.ERROR,
                resetPasswordSuccess: false,
                error,
              });
            })
          );
      })
    );
  }
  onUpdatePassword(updatePasswordForm: NgForm) {
    this.isLoadingSubject.next(true);
    this.updatePasswordState$ = this.userService
      .updatePasswod$({
        ...updatePasswordForm.value,
        token: this.tokenSujbect.value,
      })
      .pipe(
        map((response) => {
          this.isLoadingSubject.next(false);
          return {
            dataState: DataState.LOADED,
            resetPasswordSuccess: false,
            updatePasswordSuccess: true,
            message: response.message,
          };
        }),
        startWith({ dataState: DataState.LOADING, resetPasswordSuccess: true }),
        catchError((error) => {
          return of({
            dataState: DataState.ERROR,
            resetPasswordSuccess: true,
            error,
          });
        })
      );
  }
  createAccountForm() {}
}
