import { Component, OnInit } from '@angular/core';
import {
  BehaviorSubject,
  catchError,
  map,
  Observable,
  of,
  startWith,
} from 'rxjs';
import { DataState } from 'src/app/enum/dataState.enum';
import { CustomHttpResponse } from 'src/app/interface/customHttpResponse';
import { Customer } from 'src/app/interface/customer';
import { State } from 'src/app/interface/state';
import { CustomerService } from 'src/app/services/customer.service';
import { ApiResponse, ResponseData } from 'src/app/interface/appstates';
import { CustomerActionType } from '../../interface/actions';
import { CustomerBehaviourService } from '../../services/customer-behaviour.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent implements OnInit {
  readonly DataState = DataState;
  homeState$: Observable<
    State<CustomHttpResponse<ApiResponse<Customer[]>>>
  > | null = null;
  dataSubject = new BehaviorSubject<
    CustomHttpResponse<ApiResponse<Customer[]>> | undefined
  >(undefined);
  currentPageSubject = new BehaviorSubject<number | undefined>(0);
  currentPage$ = this.currentPageSubject.asObservable();
  constructor(
    private customerService: CustomerService,
    private customerSubject: CustomerBehaviourService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.homeState$ = this.customerService.pageCustomers$().pipe(
      map((response) => {
        this.dataSubject.next(response);
        this.currentPageSubject.next(response?.data?.customers?.number);
        return { dataState: DataState.LOADED, data: this.dataSubject.value };
      }),
      startWith({ dataState: DataState.LOADING }),
      catchError((err) =>
        of({
          dataState: DataState.ERROR,
          data: this.dataSubject.value,
          error: err,
        })
      )
    );
  }

  report() {}

  selectCustomer(customer: Customer) {
    this.customerSubject.publishEvent({
      type: CustomerActionType.EDIT_CUSTOMER,
      payload: customer,
    });
    this.router.navigate(['/customers']);
  }
  goToPage(index: number | undefined) {
    this.homeState$ = this.customerService.pageCustomers$(index).pipe(
      map((response) => {
        this.dataSubject.next(response);
        this.currentPageSubject.next(index);
        return { dataState: DataState.LOADED, data: this.dataSubject.value };
      }),
      startWith({ dataState: DataState.LOADING, data: this.dataSubject.value }),
      catchError((err) =>
        of({
          dataState: DataState.ERROR,
          data: this.dataSubject.value,
          error: err,
        })
      )
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
