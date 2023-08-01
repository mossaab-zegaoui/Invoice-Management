import {Component, Input} from '@angular/core';
import {Stats} from "../../interface/stats";

@Component({
  selector: 'app-stats',
  templateUrl: './stats.component.html',
  styleUrls: ['./stats.component.css']
})
export class StatsComponent {
  @Input() stats: Stats | undefined = undefined
}
