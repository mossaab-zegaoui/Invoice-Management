import {Component, Input} from '@angular/core';
import {Customer} from "../../../../interface/customer";
import {CustomerBehaviourService} from "../../../../services/customer-behaviour.service";
import {CustomerActionType} from "../../../../interface/actions";

@Component({
    selector: 'app-customer-item',
    templateUrl: './customer-item.component.html',
    styleUrls: ['./customer-item.component.css']
})
export class CustomerItemComponent {
    @Input() customer: Customer | undefined = undefined;

    constructor(private customerSubject: CustomerBehaviourService) {
    }

    selectCustomer(customer: Customer | undefined) {
        this.customerSubject.publishEvent({type: CustomerActionType.EDIT_CUSTOMER, payload: customer});
    }
}
