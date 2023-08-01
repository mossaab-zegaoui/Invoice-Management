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
import { ResponseData } from 'src/app/interface/appstates';
import { CustomHttpResponse } from 'src/app/interface/customHttpResponse';
import { State } from 'src/app/interface/state';
import { CustomerService } from 'src/app/services/customer.service';

@Component({
  selector: 'app-invoices',
  templateUrl: './invoices.component.html',
  styleUrls: ['./invoices.component.css'],
})
export class InvoicesComponent implements OnInit {
  readonly DataState = DataState;
  dataSubject$ = new BehaviorSubject<CustomHttpResponse<ResponseData> | null>(
    null
  );
  invoicesState$: Observable<State<CustomHttpResponse<ResponseData>>> | null =
    null;
  isLoadingSubject = new BehaviorSubject<boolean>(false);
  isLoading$ = this.isLoadingSubject.asObservable();

  constructor(private customerService: CustomerService) {}

  ngOnInit(): void {
    this.isLoadingSubject.next(true);
    this.invoicesState$ = this.customerService.invoices$().pipe(
      map((response) => {
        this.dataSubject$.next(response);
        this.isLoadingSubject.next(false);
        return { dataState: DataState.LOADED, data: response };
      }),
      startWith({ dataState: DataState.LOADING }),
      catchError((error) => {
        this.isLoadingSubject.next(false);
        return of({
          dataState: DataState.ERROR,
          error: error,
        });
      })
    );
  }
  goToNextOrPreviousPage(page: string) {}
}
