import { Component, OnInit } from '@angular/core';
import { Observable, catchError, map, of, startWith } from 'rxjs';
import { DataState } from 'src/app/enum/dataState.enum';
import { CustomHttpResponse } from 'src/app/interface/customHttpResponse';
import { Customer } from 'src/app/interface/customer';
import { State } from 'src/app/interface/state';
import { CustomerService } from 'src/app/services/customer.service';
import { ResponseData } from 'src/app/interface/appstates';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent implements OnInit {
  readonly DataState = DataState;
  homeState$!: Observable<State<CustomHttpResponse<ResponseData>>>;
  constructor(private customerService: CustomerService) {}

  ngOnInit(): void {
    this.homeState$ = this.customerService.customers$().pipe(
      map((response) => {
        return { dataState: DataState.LOADED, data: response };
      }),
      startWith({ dataState: DataState.LOADING }),
      catchError((err) =>
        of({
          dataState: DataState.ERROR,
          error: err,
        })
      )
    );
  }

  report() {}

  selectCustomer(customer: Customer) {}

  goToNextOrPreviousPage(forward: string) {}
}