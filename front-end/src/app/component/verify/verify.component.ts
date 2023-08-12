import {Component, OnInit} from '@angular/core';
import {NgForm} from '@angular/forms';
import {ActivatedRoute, ParamMap, Router} from '@angular/router';
import {BehaviorSubject, catchError, map, Observable, of, startWith, switchMap,} from 'rxjs';
import {DataState} from 'src/app/enum/dataState.enum';
import {VerificationLink, VerifyState,} from 'src/app/interface/appstates';
import {UserService} from 'src/app/services/user.service';

@Component({
  selector: 'app-verify',
  templateUrl: './verify.component.html',
  styleUrls: ['./verify.component.css'],
})
export class VerifyComponent implements OnInit {
  readonly DataState = DataState;
  updatePasswordState$: Observable<VerifyState> = of({
    dataState: DataState.LOADED,
    verifySuccess: false,
  });
  isLoadingSubject = new BehaviorSubject<boolean>(false);
  isLoading$ = this.isLoadingSubject.asObservable();
  TOKEN = 'token';
  EMAIL = 'email';
  tokenSubject = new BehaviorSubject<string>('');
  emailSubject = new BehaviorSubject<string>('');

  constructor(
    private userService: UserService,
    private activatedRoute: ActivatedRoute,
    private router: Router
  ) {
  }

  ngOnInit(): void {
    this.updatePasswordState$ = this.activatedRoute.queryParamMap.pipe(
      switchMap((params: ParamMap) => {
        this.tokenSubject.next(params.get(this.TOKEN)!);
        this.emailSubject.next(params.get(this.EMAIL)!);
        const type: VerificationLink = this.getUrlType(window.location.href);
        return this.userService
          .validateResetPassword$(
            this.tokenSubject.value,
            this.emailSubject.value,
            type
          )
          .pipe(
            map((response) => {
              return {
                dataState: DataState.LOADED,
                message:response.message,
                type,
                verifySuccess: true,
              };
            }),
            startWith({
              dataState: DataState.LOADED,
              type,
              verifySuccess: false,
            }),
            catchError((error) => {
              return of({
                dataState: DataState.ERROR,
                verifySuccess: false,
                type,
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
        token: this.tokenSubject.value,
      })
      .pipe(
        map((response) => {
          this.isLoadingSubject.next(false);
          return {
            type: 'register' as VerificationLink,
            dataState: DataState.LOADED,
            verifySuccess: true,
            message: response.message,
          };
        }),
        startWith({
          dataState: DataState.LOADING,
          verifySuccess: false,
          type: 'reset-password' as VerificationLink,
        }),
        catchError((error) => {
          return of({
            type: 'reset-password' as VerificationLink,
            dataState: DataState.ERROR,
            verifySuccess: true,
            error,
          });
        })
      );
  }

  createAccountForm() {
    this.router.navigate(['/register']);
  }

  private getUrlType(url: string): VerificationLink {
    return url.includes('register') ? 'register' : 'reset-password';
  }
}
