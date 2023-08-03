import { Component, OnInit } from '@angular/core';
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
import { ResponseData } from 'src/app/interface/appstates';
import { CustomHttpResponse } from 'src/app/interface/customHttpResponse';
import { State } from 'src/app/interface/state';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
})
export class ProfileComponent implements OnInit {
  readonly DataState = DataState;
  profileState$:
    | Observable<State<CustomHttpResponse<ResponseData>>>
    | undefined;
  isLoadingSubject = new BehaviorSubject<boolean>(false);
  dataSubject = new BehaviorSubject<
    CustomHttpResponse<ResponseData> | undefined
  >(undefined);
  isLoading$ = this.isLoadingSubject.asObservable();
  constructor(private userService: UserService) {}
  ngOnInit(): void {
    this.profileState$ = this.userService.profile$().pipe(
      map((response) => {
        this.dataSubject.next(response);
        return { dataState: DataState.LOADED, data: response };
      }),
      startWith({ dataState: DataState.LOADED }),
      catchError((error) => {
        return of({
          dataState: DataState.ERROR,
          data: this.dataSubject.value,
          error: error,
        });
      })
    );
  }
  updateProfile(profileForm: NgForm): void {
    this.isLoadingSubject.next(true);
    this.profileState$ = this.userService
      .updateUserDetails$(profileForm.value, profileForm.value.id)
      .pipe(
        map((response) => {
          this.dataSubject.next(response);
          this.isLoadingSubject.next(false);
          return { dataState: DataState.LOADED, data: response };
        }),
        startWith({
          dataState: DataState.LOADING,
        }),
        catchError((error) => {
          this.isLoadingSubject.next(false);
          return of({
            dataState: DataState.ERROR,
            data: this.dataSubject.value,
            error: error,
          });
        })
      );
  }

  updatePassword(passwordForm: NgForm): void {
    this.isLoadingSubject.next(true);
    if (
      passwordForm.value.newPassword !== passwordForm.value.confirmNewPassword
    ) {
      passwordForm.reset();
      alert('newPassword and verificationPassword are not equal');
      return this.isLoadingSubject.next(false);
    }

    this.profileState$ = this.userService
      .updateUserPassword$(passwordForm.value)
      .pipe(
        map((response) => {
          this.isLoadingSubject.next(false);
          this.dataSubject.next(response);
          passwordForm.reset();
          return { dataState: DataState.LOADED, data: response };
        }),
        startWith({
          dataState: DataState.LOADING,
          data: this.dataSubject.value,
        }),
        catchError((error) => {
          this.isLoadingSubject.next(false);
          return of({
            dataState: DataState.ERROR,
            data: this.dataSubject.value,
            error: error,
          });
        })
      );
  }

  updateRole(roleForm: NgForm): void {
    this.isLoadingSubject.next(true);
    this.profileState$ = this.userService
      .updateUserRole$(roleForm.value.roleName)
      .pipe(
        map((response) => {
          this.dataSubject.next(response);
          this.isLoadingSubject.next(false);
          return { dataState: DataState.LOADED, data: this.dataSubject.value };
        }),
        startWith({
          dataState: DataState.LOADING,
          data: this.dataSubject.value,
        }),
        catchError((error) => {
          this.isLoadingSubject.next(false);
          return of({
            dataState: DataState.ERROR,
            data: this.dataSubject.value,
            error: error,
          });
        })
      );
  }

  updateAccountSettings(settingsForm: NgForm): void {
    this.isLoadingSubject.next(true);
    this.profileState$ = this.userService
      .updateUserAccountSettings$(settingsForm.value)
      .pipe(
        map((response) => {
          this.dataSubject.next(response);
          this.isLoadingSubject.next(false);
          return { dataState: DataState.LOADED, data: this.dataSubject.value };
        }),
        startWith({
          dataState: DataState.LOADING,
          data: this.dataSubject.value,
        }),
        catchError((error) => {
          this.isLoadingSubject.next(false);
          return of({
            dataState: DataState.ERROR,
            data: this.dataSubject.value,
            error: error,
          });
        })
      );
  }

  toggleMfa(): void {
    this.isLoadingSubject.next(true);
    this.profileState$ = this.userService.toggleMfa().pipe(
      map((response) => {
        this.dataSubject.next(response);
        this.isLoadingSubject.next(false);
        return { dataState: DataState.LOADED, data: this.dataSubject.value };
      }),
      startWith({ dataState: DataState.LOADING, data: this.dataSubject.value }),
      catchError((error) => {
        this.isLoadingSubject.next(false);
        return of({
          dataState: DataState.ERROR,
          data: this.dataSubject.value,
          error,
        });
      })
    );
  }

  updatePicture(event: Event): void {
    const inputElement = event.target as HTMLInputElement;
    const image = inputElement.files?.[0];
    this.isLoadingSubject.next(true);
    console.log(image);
    if (image) {
      this.profileState$ = this.userService
        .uploadProfileImage$(this.getFormData(image))
        .pipe(
          map((response) => {
            this.dataSubject.next(response);
            this.isLoadingSubject.next(false);
            return { dataState: DataState.LOADED, data: response };
          }),
          startWith({
            dataState: DataState.LOADING,
            data: this.dataSubject.value,
          }),
          catchError((error) => {
            this.isLoadingSubject.next(false);
            return of({
              dataState: DataState.ERROR,
              data: this.dataSubject.value,
              error,
            });
          })
        );
    }
  }

  toggleLogs(): void {}

  private getFormData(image: File): FormData {
    const formData = new FormData();
    formData.append('image', image);
    return formData;
  }
}
