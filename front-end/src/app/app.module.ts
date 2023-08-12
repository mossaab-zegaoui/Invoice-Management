import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {LoginComponent} from './component/login/login.component';
import {RegisterComponent} from './component/register/register.component';
import {HomeComponent} from './component/home/home.component';
import {TokenInterceptor} from './interceptor/token.interceptor';
import {CommonModule} from "@angular/common";
import { NavbarComponent } from './component/navbar/navbar.component';
import { StatsComponent } from './component/stats/stats.component';
import { NewCustomerComponent } from './component/customers/new-customer/new-customer.component';
import { NewInvoiceComponent } from './component/invoices/new-invoice/new-invoice.component';
import { InvoicesComponent } from './component/invoices/invoices.component';
import { ProfileComponent } from './component/profile/profile.component';
import { CustomersComponent } from './component/customers/customers.component';
import { EditCustomerComponent } from './component/customers/edit-customer/edit-customer.component';
import { CustomerListComponent } from './component/customers/customer-list/customer-list.component';
import { CustomerItemComponent } from './component/customers/customer-list/customer-item/customer-item.component';
import { ExtractArrayValuePipe } from './pipes/extract-array-value.pipe';
import { InvoiceComponent } from './component/invoice/invoice.component';
import { ResetPasswordComponent } from './component/reset-password/reset-password.component';
import { VerifyComponent } from './component/verify/verify.component';

@NgModule({
  declarations: [
    AppComponent,
    RegisterComponent,
    LoginComponent,
    HomeComponent,
    NavbarComponent,
    StatsComponent,
    NewCustomerComponent,
    NewInvoiceComponent,
    InvoicesComponent,
    ProfileComponent,
    CustomersComponent,
    EditCustomerComponent,
    CustomerListComponent,
    CustomerItemComponent,
    ExtractArrayValuePipe,
    InvoiceComponent,
    ResetPasswordComponent,
    VerifyComponent,
  ],
  imports: [CommonModule, BrowserModule, AppRoutingModule, HttpClientModule, FormsModule, ReactiveFormsModule],
  providers: [
    {provide: HTTP_INTERCEPTORS, useClass: TokenInterceptor, multi: true},
  ],
  bootstrap: [AppComponent],
})
export class AppModule {
}
