import { DataState } from '../enum/dataState.enum';
import { Role } from './role';
import { Customer } from './customer';
import { Invoice } from './invoice';
import { Stats } from './stats';
import { User } from './user';

export interface RegisterAndResetState {
  dataState: DataState;
  registerSuccess?: boolean;
  message?: string;
  error?: string;
}
export interface LoginState {
  dataState: DataState;
  loginSuccess?: boolean;
  message?: string;
  error?: string;
  usingMfa?: boolean;
  phone?: string;
}
export interface VerifyState {
  dataState: DataState;
  verifySuccess: boolean;
  type?: VerificationLink;
  message?: string;
  error?: string;
}
export interface Profile {
  user: User;
  roles?: Role[];
  access_token: string;
  refresh_token: string;
}

export interface ResponseData {
  customers?: Customer[];
  customer?: Customer;
  user?: User;
  stats?: Stats;
  invoices?: Invoice[];
  invoice?: Invoice;
  roles?: Role[];
}

export interface ApiResponse<T> {
  page: Page<T>;
  user?: User;
  stats?: Stats;
}

export interface Page<T> {
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  content: T;
}

export type VerificationLink = 'register' | 'reset-password';
