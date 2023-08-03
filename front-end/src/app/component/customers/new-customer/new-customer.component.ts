import { Component, OnInit } from '@angular/core';
import { DataState } from '../../../enum/dataState.enum';
import {
  BehaviorSubject,
  catchError,
  map,
  Observable,
  of,
  startWith,
} from 'rxjs';
import { Customer } from '../../../interface/customer';
import { CustomHttpResponse } from '../../../interface/customHttpResponse';
import { ResponseData } from '../../../interface/appstates';
import { CustomerService } from '../../../services/customer.service';
import { NgForm } from '@angular/forms';
import { State } from '../../../interface/state';

@Component({
  selector: 'app-new-edit-customer',
  templateUrl: './new-customer.component.html',
  styleUrls: ['./new-customer.component.css'],
})
export class NewCustomerComponent implements OnInit {
  readonly DataState = DataState;
  newCustomerState$: Observable<State<CustomHttpResponse<ResponseData>>> | undefined ;
  private dataSubject = new BehaviorSubject<
    CustomHttpResponse<ResponseData> | undefined
  >(undefined);
  private isLoadingSubject = new BehaviorSubject<boolean>(false);
  isLoading$ = this.isLoadingSubject.asObservable();

  constructor(private customerService: CustomerService) {}
  ngOnInit(): void {
    this.newCustomerState$ = this.customerService.customers$().pipe(
      map((response) => {
        this.dataSubject.next(response);
        return { dataState: DataState.LOADED, data: response };
      }),
      startWith({ dataState: DataState.LOADING }),
      catchError((err: string) => {
        this.isLoadingSubject.next(false);
        return of({
          dataState: DataState.ERROR,
          error: err,
        });
      })
    );
  }
  createCustomer(newCustomerForm: NgForm) {
    this.isLoadingSubject.next(true);
    this.newCustomerState$ = this.customerService
      .newCustomer$(newCustomerForm.value)
      .pipe(
        map((response) => {
          console.log(response);
          newCustomerForm.reset({ type: 'INSTITUTION', status: 'ACTIVE' });
          this.isLoadingSubject.next(false);
          return {
            dataState: DataState.LOADED,
            data: this.dataSubject.value,
            success: true,
          };
        }),
        startWith({
          dataState: DataState.LOADING,
          data: this.dataSubject.value,
        }),
        catchError((error: string) => {
          this.isLoadingSubject.next(false);
          return of({
            dataState: DataState.ERROR,
            error,
            data: this.dataSubject.value,
          });
        })
      );
  }
}
