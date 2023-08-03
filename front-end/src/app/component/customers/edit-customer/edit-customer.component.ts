import {Component, OnInit} from '@angular/core';
import {CustomHttpResponse} from "../../../interface/customHttpResponse";
import {ResponseData} from "../../../interface/appstates";
import {BehaviorSubject, catchError, map, Observable, of, startWith, switchMap} from "rxjs";
import {State} from "../../../interface/state";
import {DataState} from "../../../enum/dataState.enum";
import {CustomerService} from "../../../services/customer.service";
import {ActivatedRoute, ParamMap, Router} from "@angular/router";
import {NgForm} from "@angular/forms";
import {CustomerBehaviourService} from "../../../services/customer-behaviour.service";
import {CustomerActionType} from "../../../interface/actions";

@Component({
    selector: 'app-edit-customer',
    templateUrl: './edit-customer.component.html',
    styleUrls: ['./edit-customer.component.css']
})
export class EditCustomerComponent implements OnInit {
    readonly DataState = DataState;
    private readonly customerId: string = 'id';

    customerState$: Observable<State<CustomHttpResponse<ResponseData>>> | undefined;
    isLoadingSubject = new BehaviorSubject<boolean>(false);
    isLoading$ = this.isLoadingSubject.asObservable();
    dataSubject = new BehaviorSubject<CustomHttpResponse<ResponseData> | undefined>(undefined);

    constructor(private customerService: CustomerService, private activatedRoute: ActivatedRoute, private eventService: CustomerBehaviourService
        , private router: Router) {
    }

    ngOnInit(): void {
        this.isLoadingSubject.next(true);
        this.customerState$ = this.activatedRoute.paramMap.pipe(
            switchMap((params: ParamMap) => {
                return this.customerService.customer$(+params.get(this.customerId)!).pipe(
                    map((response) => {
                        this.dataSubject.next(response);
                        this.isLoadingSubject.next(false);
                        return {dataState: DataState.LOADED, data: this.dataSubject.value}
                    }),
                    startWith({dataState: DataState.LOADING, data: this.dataSubject.value}),
                    catchError((error) => {
                        this.isLoadingSubject.next(false);
                        return of({dataState: DataState.ERROR, data: this.dataSubject.value, error})
                    })
                )
            })
        )
    }

    updateCustomer(customerForm: NgForm) {
        this.isLoadingSubject.next(true);
        this.customerState$ = this.customerService.updateCustomer$(customerForm.value).pipe(
            map((response) => {
                this.dataSubject.next(response);
                this.isLoadingSubject.next(false);
                return {dataState: DataState.LOADED, data: this.dataSubject.value}
            }),
            startWith({dataState: DataState.LOADING, data: this.dataSubject.value}),
            catchError((error: string) => {
                this.isLoadingSubject.next(false);
                return of({dataState: DataState.ERROR, data: this.dataSubject.value, error})
            })
        )
    }

    loadCustomers() {
        this.eventService.publishEvent({type: CustomerActionType.LOAD_CUSTOMERS});
        this.router.navigate(['/customers']);
    }
}
