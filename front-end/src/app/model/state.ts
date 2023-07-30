import { DataState } from '../enum/dataState.enum';

export interface State<T> {
  state?: DataState;
  data?: T;
  error?: string;
}
