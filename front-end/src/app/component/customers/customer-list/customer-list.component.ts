import {Component, Input} from '@angular/core';
import {Customer} from "../../../interface/customer";

@Component({
    selector: 'app-customer-list',
    templateUrl: './customer-list.component.html',
    styleUrls: ['./customer-list.component.css']
})
export class CustomerListComponent {
    @Input() customers: Customer[] | undefined = undefined;

}
