export enum CustomerActionType {
  ADD_CUSTOMER,
  EDIT_CUSTOMER,
  LOAD_CUSTOMERS,
  DELETE_CUSTOMER,
  UPDATE_CUSTOMER
}

export interface CustomerAction {
  type?: CustomerActionType,
  payload?: any
}
