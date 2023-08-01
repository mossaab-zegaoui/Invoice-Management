export interface CustomHttpResponse<T> {
  timeStamp: Date;
  status: string;
  message: string;
  reason?: string;
  developerMessage?: string;
  data?: T;
}
