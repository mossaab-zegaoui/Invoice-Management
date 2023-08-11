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
import { CustomerService } from 'src/app/services/customer.service';

@Component({
  selector: 'app-new-invoice',
  templateUrl: './new-invoice.component.html',
  styleUrls: ['./new-invoice.component.css'],
})
export class NewInvoiceComponent implements OnInit {
  readonly DataState = DataState;
  newInvoiceState$:
    | Observable<State<CustomHttpResponse<ResponseData>>>
    | undefined;
  isLoadingSubject = new BehaviorSubject<boolean>(false);
  isLoading$ = this.isLoadingSubject.asObservable();
  dataSubject = new BehaviorSubject<
    CustomHttpResponse<ResponseData> | undefined
  >(undefined);
  constructor(private customerService: CustomerService) {}
  ngOnInit(): void {
    this.newInvoiceState$ = this.customerService.newInvoice$().pipe(
      map((response) => {
        this.dataSubject.next(response);
        return { dataState: DataState.LOADED, data: response };
      }),
      startWith({ dataState: DataState.LOADED }),
      catchError((error) =>
        of({
          dataState: DataState.ERROR,
          error,
        })
      )
    );
  }
  newInvoice(newInvoiceForm: NgForm) {
    this.isLoadingSubject.next(true);
    this.newInvoiceState$ = this.customerService
      .addInvoiceToCustomer$(
        newInvoiceForm.value.customerId,
        newInvoiceForm.value
      )
      .pipe(
        map((response) => {
          this.dataSubject.next(response);
          newInvoiceForm.reset({ status: 'PENDING' });
          this.isLoadingSubject.next(false);
          return {
            dataState: DataState.LOADED,
            data: this.dataSubject.value,
            success: true,
          };
        }),
        startWith({
          dataState: DataState.LOADED,
          data: this.dataSubject.value,
        }),
        catchError((error) => {
          this.isLoadingSubject.next(false);
          return of({
            dataState: DataState.ERROR,
            error: error,
          });
        })
      );
  }
}
