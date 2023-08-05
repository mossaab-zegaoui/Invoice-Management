import { Pipe, PipeTransform } from '@angular/core';
import { Invoice } from '../interface/invoice';

@Pipe({
  name: 'extractArrayValue',
})
export class ExtractArrayValuePipe implements PipeTransform {
  transform(value: any, args: string): any {
    if (args === 'invoices') {
      return this.calculateTotal(value);
    }
    if (args === 'number') {
      return this.generateNumberArray(value);
    }
    return 0;
  }

  private calculateTotal(invoices: Invoice[]): string {
    let total: number = 0;
    invoices.forEach((invoice: Invoice) => {
      total += invoice.total;
    });
    return total.toFixed(2);
  }

  private generateNumberArray(count: number): number[] {
    let numberArray: number[] = [];
    for (let i = 0; i < count; i++) {
      numberArray.push(i);
    }
    return numberArray;
  }
}
