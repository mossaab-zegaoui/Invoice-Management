import { HttpStatusCode } from '@angular/common/http';

export interface CustomHttpResponse<T> {
  timeStamp: Date;
  status: string;
  reason?: string;
  message?: string;
  developerMessage?: string;
  data: T;
}
