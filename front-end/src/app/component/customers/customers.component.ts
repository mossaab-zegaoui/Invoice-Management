import { Component, OnDestroy, OnInit } from '@angular/core';
import { DataState } from '../../enum/dataState.enum';
import {
  BehaviorSubject,
  catchError,
  map,
  Observable,
  of,
  startWith,
  Subscription,
} from 'rxjs';
import { CustomHttpResponse } from '../../interface/customHttpResponse';
import { ApiResponse, ResponseData } from '../../interface/appstates';
import { State } from '../../interface/state';
import { CustomerService } from '../../services/customer.service';
import { NgForm } from '@angular/forms';
import { Customer } from '../../interface/customer';
import { CustomerAction, CustomerActionType } from '../../interface/actions';
import { CustomerBehaviourService } from '../../services/customer-behaviour.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-customers',
  templateUrl: './customers.component.html',
  styleUrls: ['./customers.component.css'],
})
export class CustomersComponent implements OnInit, OnDestroy {
  readonly DataState = DataState;
  dataSubject = new BehaviorSubject<
    CustomHttpResponse<ApiResponse<Customer[]>> | undefined
  >(undefined);
  customers$:
    | Observable<State<CustomHttpResponse<ApiResponse<Customer[]>>>>
    | undefined = undefined;
  unsubscribe$: Subscription | null = null;
  currentPageSubject = new BehaviorSubject<number | undefined>(0);
  currentPage$ = this.currentPageSubject.asObservable();
  searchValueSubject = new BehaviorSubject<string>('');
  constructor(
    private customerService: CustomerService,
    private customerSubject: CustomerBehaviourService,
    private router: Router
  ) {}

  ngOnInit() {
    this.unsubscribe$ = this.customerSubject.customerSubject$.subscribe({
      next: ($event: CustomerAction) => {
        this.onAction($event);
      },
      error: (err) => {
        console.log(err.message);
      },
    });
  }

  loadCustomers() {
    this.customers$ = this.customerService
      .searchCustomers$(this.searchValueSubject.value)
      .pipe(
        map((response) => {
          this.dataSubject.next(response);
          this.currentPageSubject.next(response?.data?.page?.number);
          return { dataState: DataState.LOADED, data: this.dataSubject.value };
        }),
        startWith({
          dataState: DataState.LOADING,
          data: this.dataSubject.value,
        }),
        catchError((error: string) => {
          return of({
            dataState: DataState.ERROR,
            data: this.dataSubject.value,
            error,
          });
        })
      );
  }

  onAction(event: CustomerAction) {
    switch (event.type) {
      case CustomerActionType.LOAD_CUSTOMERS: {
        this.loadCustomers();
        break;
      }
      case CustomerActionType.EDIT_CUSTOMER: {
        this.editCustomer(event.payload);
        break;
      }
    }
  }

  searchCustomers(searchForm: NgForm) {
    this.currentPageSubject.next(0);
    console.log(searchForm.value);
    this.customers$ = this.customerService
      .searchCustomers$(searchForm.value.name)
      .pipe(
        map((response) => {
          this.searchValueSubject.next(searchForm.value.name);
          this.dataSubject.next(response);
          this.currentPageSubject.next(response?.data?.page?.number);
          return { dataState: DataState.LOADED, data: this.dataSubject.value };
        }),
        startWith({
          dataState: DataState.LOADING,
          data: this.dataSubject.value,
        }),
        catchError((error: string) => {
          return of({
            dataState: DataState.ERROR,
            data: this.dataSubject.value,
            error,
          });
        })
      );
  }
  goToPage(index: number | undefined) {
    this.customers$ = this.customerService
      .searchCustomers$(this.searchValueSubject.value, index)
      .pipe(
        map((response) => {
          this.dataSubject.next(response);
          this.currentPageSubject.next(index);
          return { dataState: DataState.LOADED, data: this.dataSubject.value };
        }),
        startWith({
          dataState: DataState.LOADING,
          data: this.dataSubject.value,
        }),
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
  clearSearchForm(searchForm: NgForm) {
    searchForm.reset();
    searchForm.value.name = '';
  }
  private editCustomer(customer: Customer) {
    this.router.navigate([`customers/edit/${customer?.id}`]);
  }

  ngOnDestroy(): void {
    this.unsubscribe$?.unsubscribe();
  }
}
