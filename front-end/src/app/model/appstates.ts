import { DataState } from '../enum/dataState.enum';
import { User } from './User';

export interface RegisterState {
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
}

export interface Profile {
  user: User;
  access_token: string;
  refresh_token: string;
}
