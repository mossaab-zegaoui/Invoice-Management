import {Component, OnInit} from '@angular/core';
import {catchError, map, Observable, of, startWith} from 'rxjs';
import {DataState} from 'src/app/enum/dataState.enum';
import {CustomHttpResponse} from 'src/app/interface/customHttpResponse';
import {Customer} from 'src/app/interface/customer';
import {State} from 'src/app/interface/state';
import {CustomerService} from 'src/app/services/customer.service';
import {ResponseData} from 'src/app/interface/appstates';
import {CustomerActionType} from "../../interface/actions";
import {CustomerBehaviourService} from "../../services/customer-behaviour.service";
import {Router} from "@angular/router";

@Component({
    selector: 'app-home',
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.css'],
})
export class HomeComponent implements OnInit {
    readonly DataState = DataState;
    homeState$!: Observable<State<CustomHttpResponse<ResponseData>>>;

    constructor(private customerService: CustomerService, private customerSubject: CustomerBehaviourService, private router: Router) {
    }

    ngOnInit(): void {
        this.homeState$ = this.customerService.customers$().pipe(
            map((response) => {
                return {dataState: DataState.LOADED, data: response};
            }),
            startWith({dataState: DataState.LOADING}),
            catchError((err) =>
                of({
                    dataState: DataState.ERROR,
                    error: err,
                })
            )
        );
    }

    report() {
    }

    selectCustomer(customer: Customer) {
        this.customerSubject.publishEvent({type: CustomerActionType.EDIT_CUSTOMER, payload: customer});
        this.router.navigate(['/customers']);
    }

    goToNextOrPreviousPage(forward: string) {
    }
}
