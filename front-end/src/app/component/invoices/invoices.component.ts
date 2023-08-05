import { Component, OnInit } from '@angular/core';
import {
  BehaviorSubject,
  Observable,
  catchError,
  map,
  of,
  startWith,
} from 'rxjs';
import { DataState } from 'src/app/enum/dataState.enum';
import { ApiResponse, ResponseData } from 'src/app/interface/appstates';
import { CustomHttpResponse } from 'src/app/interface/customHttpResponse';
import { Invoice } from 'src/app/interface/invoice';
import { State } from 'src/app/interface/state';
import { CustomerService } from 'src/app/services/customer.service';

@Component({
  selector: 'app-invoices',
  templateUrl: './invoices.component.html',
  styleUrls: ['./invoices.component.css'],
})
export class InvoicesComponent implements OnInit {
  readonly DataState = DataState;
  dataSubject = new BehaviorSubject<
    CustomHttpResponse<ApiResponse<Invoice[]>> | undefined
  >(undefined);
  invoicesState$:
    | Observable<State<CustomHttpResponse<ApiResponse<Invoice[]>>>>
    | undefined = undefined;
  isLoadingSubject = new BehaviorSubject<boolean>(false);
  isLoading$ = this.isLoadingSubject.asObservable();
  currentPageSubject = new BehaviorSubject<number | undefined>(0);
  currentPage$ = this.currentPageSubject.asObservable();
  constructor(private customerService: CustomerService) {}

  ngOnInit(): void {
    this.isLoadingSubject.next(true);
    this.invoicesState$ = this.customerService.invoicePage$().pipe(
      map((response) => {
        this.dataSubject.next(response);
        this.currentPageSubject.next(response.data?.invoices?.number);
        this.isLoadingSubject.next(false);
        return { dataState: DataState.LOADED, data: response };
      }),
      startWith({ dataState: DataState.LOADING }),
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
  goToPage(pageNumber: number) {
    this.isLoadingSubject.next(true);
    this.invoicesState$ = this.customerService.invoicePage$(pageNumber).pipe(
      map((response) => {
        this.dataSubject.next(response);
        this.currentPageSubject.next(response.data?.invoices?.number);
        this.isLoadingSubject.next(false);
        console.log(this.currentPageSubject.value)
        return { dataState: DataState.LOADED, data: response };
      }),
      startWith({ dataState: DataState.LOADING }),
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
  goToNextOrPreviousPage(direction: string) {
    this.goToPage(
      direction === 'forward'
        ? this.currentPageSubject.value! + 1
        : this.currentPageSubject.value! - 1
    );
  }
}
