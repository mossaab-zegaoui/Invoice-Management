import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Observable, catchError, map, of, startWith } from 'rxjs';
import { DataState } from 'src/app/enum/dataState.enum';
import { RegisterAndResetState } from 'src/app/interface/appstates';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
})
export class RegisterComponent {
  readonly DataState = DataState;
  registerState$: Observable<RegisterAndResetState> = of({
    dataState: DataState.LOADED,
  });
  constructor(private userService: UserService) {}
  onRegister(registerForm: NgForm) {
    this.registerState$ = this.userService.register$(registerForm.value).pipe(
      map((response) => {
        registerForm.reset();
        return {
          dataState: DataState.LOADED,
          message: response.message,
          registerSuccess: true,
        };
      }),
      startWith({ dataState: DataState.LOADING, registerSuccess: false }),
      catchError((error: string) =>
        of({
          dataState: DataState.ERROR,
          registerSuccess: false,
          error,
        })
      )
    );
  }
  
  createAccountForm() {
    this.registerState$ = of({
      dataState: DataState.LOADED,
      registerSuccess: false,
    });
  }
}
