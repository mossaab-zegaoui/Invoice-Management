import {Injectable} from '@angular/core';
import {BehaviorSubject} from "rxjs";
import {CustomerAction, CustomerActionType} from "../interface/actions";

@Injectable({
    providedIn: 'root'
})
export class CustomerBehaviourService {
    customerSubject = new BehaviorSubject<CustomerAction>({type: CustomerActionType.LOAD_CUSTOMERS});
    customerSubject$ = this.customerSubject.asObservable();

    constructor() {
    }

    publishEvent(event: CustomerAction) {
        this.customerSubject.next(event);
    }
}
