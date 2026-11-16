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
    // Defensa en el padre: nunca aplica un movimiento inválido aunque el hijo lo emita
    const { tipo, monto } = movimiento;
    if (!Number.isFinite(monto) || monto <= 0) {
      return;
    }
    if (tipo === 'Gasto' && monto > this.saldo) {
      return;
    }

    const nuevoSaldo = tipo === 'Recarga' ? this.saldo + monto : this.saldo - monto;
    // Redondeo a céntimos para evitar errores de coma flotante (0.1 + 0.2)
    this.saldo = Math.round(nuevoSaldo * 100) / 100;
    // Orden cronológico inverso: el movimiento más reciente siempre arriba
    this.movimientos.unshift(movimiento);
  }
}
