import {Component, Input} from '@angular/core';
import {User} from '../../interface/user';
import {Token} from 'src/app/enum/token.enum';
import {Router} from '@angular/router';
import {CustomerBehaviourService} from "../../services/customer-behaviour.service";
import {CustomerActionType} from "../../interface/actions";

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
})
export class NavbarComponent {
  @Input() user: User | undefined = undefined;

  constructor(private router: Router, private customerSubject: CustomerBehaviourService) {
  }

  logOut() {
    localStorage.removeItem(Token.ACCESS_TOKEN);
    localStorage.removeItem(Token.REFRESH_TOKEN);
    this.router.navigate(['/login']);
  }

  loadCustomers() {
    this.customerSubject.publishEvent({type: CustomerActionType.LOAD_CUSTOMERS})
    this.router.navigate(['/customers']);
  }
}
