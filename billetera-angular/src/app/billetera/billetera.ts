import { Component } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { Movimiento } from '../movimiento.model';
import { NuevoMovimientoComponent } from '../nuevo-movimiento/nuevo-movimiento';

/**
 * BilleteraComponent (PADRE): dueño del estado.
 * - saldo inicial 250, arreglo de movimientos vacío
 * - pasa el saldo al hijo con [saldo]
 * - escucha (movimientoValido) y registra al inicio del arreglo
 */
@Component({
  selector: 'app-billetera',
  imports: [DecimalPipe, NuevoMovimientoComponent],
  templateUrl: './billetera.html',
  styleUrl: './billetera.css',
})
export class BilleteraComponent {
  saldo = 250;
  movimientos: Movimiento[] = [];

  registrarMovimiento(movimiento: Movimiento): void {
    if (movimiento.tipo === 'Recarga') {
      this.saldo += movimiento.monto;
    } else {
      this.saldo -= movimiento.monto;
    }
    // Orden cronológico inverso: el movimiento más reciente siempre arriba
    this.movimientos.unshift(movimiento);
  }
}
