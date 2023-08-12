import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './component/login/login.component';
import { RegisterComponent } from './component/register/register.component';
import { HomeComponent } from './component/home/home.component';
import { AuthenticationGuard } from './guard/auth.guard';
import { NewCustomerComponent } from './component/customers/new-customer/new-customer.component';
import { NewInvoiceComponent } from './component/invoices/new-invoice/new-invoice.component';
import { InvoicesComponent } from './component/invoices/invoices.component';
import { ProfileComponent } from './component/profile/profile.component';
import { CustomersComponent } from './component/customers/customers.component';
import { EditCustomerComponent } from './component/customers/edit-customer/edit-customer.component';
import { InvoiceComponent } from './component/invoice/invoice.component';
import { ResetPasswordComponent } from './component/reset-password/reset-password.component';
import { VerifyComponent } from './component/verify/verify.component';

const routes: Routes = [
  { path: 'register', component: RegisterComponent },
  { path: 'login', component: LoginComponent },
  {
    path: 'reset-password/validate',
    component: VerifyComponent,
  },
  { path: 'register/validate', component: VerifyComponent },
  { path: 'reset-password', component: ResetPasswordComponent },
  {
    path: 'home',
    component: HomeComponent,
    canActivate: [AuthenticationGuard],
  },
  {
    path: 'customers/new',
    component: NewCustomerComponent,
    canActivate: [AuthenticationGuard],
  },
  {
    path: 'invoices/new',
    component: NewInvoiceComponent,
    canActivate: [AuthenticationGuard],
  },
  {
    path: 'invoices',
    component: InvoicesComponent,
    canActivate: [AuthenticationGuard],
  },
  {
    path: 'invoices/:id/:invoiceNumber',
    component: InvoiceComponent,
    canActivate: [AuthenticationGuard],
  },
  {
    path: 'profile',
    component: ProfileComponent,
    canActivate: [AuthenticationGuard],
  },
  {
    path: 'customers',
    component: CustomersComponent,
    canActivate: [AuthenticationGuard],
  },
  {
    path: 'customers/edit/:id',
    component: EditCustomerComponent,
    canActivate: [AuthenticationGuard],
  },
  { path: '', redirectTo: '/home', pathMatch: 'full' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
