import {Pipe, PipeTransform} from '@angular/core';
import {Invoice} from "../interface/invoice";

@Pipe({
    name: 'extractArrayValue'
})
export class ExtractArrayValuePipe implements PipeTransform {

    transform(value: any, args: string): any {
        let total: number = 0;
        if (args === "invoices") {
            value.forEach((invoice: Invoice) => {
                total += invoice.total;
            })
            return total.toFixed(2);
        }
        return 0;
    }

}
