import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
import {
  BehaviorSubject,
  Observable,
  catchError,
  map,
  of,
  startWith,
  switchMap,
} from 'rxjs';
import { DataState } from 'src/app/enum/dataState.enum';
import { ResponseData } from 'src/app/interface/appstates';
import { CustomHttpResponse } from 'src/app/interface/customHttpResponse';
import { State } from 'src/app/interface/state';
import { CustomerService } from 'src/app/services/customer.service';
import { jsPDF } from 'jspdf';
@Component({
  selector: 'app-invoice',
  templateUrl: './invoice.component.html',
  styleUrls: ['./invoice.component.css'],
})
export class InvoiceComponent implements OnInit {
  @ViewChild('invoice', { static: false }) invoiceElement!: ElementRef;
  readonly DataState = DataState;
  invoiceState$: Observable<State<CustomHttpResponse<ResponseData>>> | null =
    null;
  invoiceNumberSubject = new BehaviorSubject<string>('');
  private readonly INVOICE_ID = 'id';
  constructor(
    private route: ActivatedRoute,
    private customerService: CustomerService
  ) {}
  ngOnInit(): void {
    this.invoiceState$ = this.route.paramMap.pipe(
      switchMap((params: ParamMap) => {
        return this.customerService.invoice$(params.get(this.INVOICE_ID)!).pipe(
          map((response) => {
            this.invoiceNumberSubject.next(
              response.data?.invoice?.invoiceNumber!
            );
            return { dataState: DataState.LOADED, data: response };
          }),
          startWith({ dataState: DataState.LOADING }),
          catchError((error) => {
            return of({ dataState: DataState.ERROR, error });
          })
        );
      })
    );
  }
  exportAsPDF(): void {
    const filename = `invoice-${this.invoiceNumberSubject.value}.pdf`;
    const doc = new jsPDF();
    doc.html(this.invoiceElement.nativeElement, {
      margin: 5,
      windowWidth: 1000,
      width: 200,
      callback: (invoice) => invoice.save(filename),
    });
  }

  // let DATA: any = document.getElementById('invoice');

  // html2canvas(DATA).then((canvas: any) => {
  //   let fileWidth = 208;
  //   let fileHeight = (canvas.height * fileWidth) / canvas.width;
  //   const FILEURI = canvas.toDataURL('image/jpeg');
  //   let PDF = new jsPDF('p', 'mm', 'a4');
  //   let positionX = 5; // X coordinate of the image position
  //   let positionY = 5; // Y coordinate of the image position
  //   let imageWidth = fileWidth - (2 * positionX); // Adjusting the image width to fit the margins
  //   let imageHeight = fileHeight - (2 * positionY); // Adjusting the image height to fit the margins

  //   PDF.addImage(FILEURI, 'PNG', positionX, positionY, imageWidth, imageHeight);
  //   PDF.save(filename);
  // });
}
