import { DataState } from '../enum/dataState.enum';

export interface State<T> {
  dataState?: DataState;
  data?: T;
  error?: string;
  success?:boolean
}
