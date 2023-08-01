export interface Invoice {
  id?: string;
  invoiceNumber: string;
  services: string;
  date: Date;
  status: string;
  total: number;
}
