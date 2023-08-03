import {Component, OnDestroy, OnInit} from '@angular/core';
import {DataState} from "../../enum/dataState.enum";
import {BehaviorSubject, catchError, map, Observable, of, startWith, Subscription} from "rxjs";
import {CustomHttpResponse} from "../../interface/customHttpResponse";
import {ResponseData} from "../../interface/appstates";
import {State} from "../../interface/state";
import {CustomerService} from "../../services/customer.service";
import {NgForm} from "@angular/forms";
import {Customer} from "../../interface/customer";
import {CustomerAction, CustomerActionType} from "../../interface/actions";
import {CustomerBehaviourService} from "../../services/customer-behaviour.service";
import {Router} from "@angular/router";

@Component({
    selector: 'app-customers',
    templateUrl: './customers.component.html',
    styleUrls: ['./customers.component.css']
})
export class CustomersComponent implements OnInit, OnDestroy {
    readonly DataState = DataState;
    dataSubject = new BehaviorSubject<CustomHttpResponse<ResponseData> | undefined>(undefined);
    customers$: Observable<State<CustomHttpResponse<ResponseData>>> | null = null;
    unsubscribe$:Subscription | null = null;
    constructor(private customerService: CustomerService, private customerSubject: CustomerBehaviourService, private router:Router) {
    }

    ngOnInit() {
        this.unsubscribe$ = this.customerSubject.customerSubject$
            .subscribe({
                next: ($event: CustomerAction) => {
                    this.onAction($event);
                },
                error: (err) => {
                    console.log(err.message);
                }
            })

    }

    loadCustomers() {
        this.customers$ = this.customerService.customers$().pipe(
            map((response) => {
                this.dataSubject.next(response);
                return {dataState: DataState.LOADED, data: this.dataSubject.value}
            }),
            startWith({dataState: DataState.LOADING, data: this.dataSubject.value}),
            catchError((error: string) => {
                return of({dataState: DataState.ERROR, data: this.dataSubject.value, error})
            })
        )
    }

    onAction(event: CustomerAction) {
        console.log(event)
        switch (event.type) {
            case CustomerActionType.LOAD_CUSTOMERS: {
                this.loadCustomers();
                break;
            }
            case CustomerActionType.EDIT_CUSTOMER:{
                this.editCustomer(event.payload);
                break;
            }
        }
    }

    searchCustomers(searchForm: NgForm) {


    }


    private editCustomer(customer: Customer) {
        this.router.navigate([`customers/edit/${customer?.id}`]);
    }

    ngOnDestroy(): void {
        this.unsubscribe$?.unsubscribe();
    }


}
