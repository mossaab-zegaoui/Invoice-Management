import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './component/login/login.component';
import { RegisterComponent } from './component/register/register.component';
import { HomeComponent } from './component/home/home.component';
import { AuthenticationGuard } from './guard/auth.guard';
import { CustomerComponent } from './component/customer/customer.component';
import { NewInvoiceComponent } from './component/invoices/new-invoice/new-invoice.component';
import { InvoicesComponent } from './component/invoices/invoices.component';
import { ProfileComponent } from './component/profile/profile.component';

const routes: Routes = [
  { path: 'register', component: RegisterComponent },
  { path: 'login', component: LoginComponent },
  {
    path: 'home',
    component: HomeComponent,
    canActivate: [AuthenticationGuard],
  },
  {
    path: 'customers/new',
    component: CustomerComponent,
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
    path: 'profile',
    component: ProfileComponent,
    canActivate: [AuthenticationGuard],
  },
  { path: '', redirectTo: '/home', pathMatch: 'full' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
