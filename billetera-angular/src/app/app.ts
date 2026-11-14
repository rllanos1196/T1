import { Component } from '@angular/core';
import { BilleteraComponent } from './billetera/billetera';

/**
 * AppComponent: únicamente como contenedor de <app-billetera>.
 * No contiene lógica ni estado (el estado vive en BilleteraComponent).
 */
@Component({
  selector: 'app-root',
  imports: [BilleteraComponent],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class AppComponent {}
