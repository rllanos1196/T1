import { Component, input, output } from '@angular/core';
import { Movimiento } from '../movimiento.model';

/**
 * NuevoMovimientoComponent (HIJO): solo presenta el formulario y notifica.
 * - recibe el saldo con input.required<number>()
 * - emite movimientos válidos con output<Movimiento>()
 * - valida en tiempo real y deshabilita el botón con [disabled]
 * - NO modifica saldo ni historial (eso lo hace el padre)
 */
@Component({
  selector: 'app-nuevo-movimiento',
  imports: [],
  templateUrl: './nuevo-movimiento.html',
  styleUrl: './nuevo-movimiento.css',
})
export class NuevoMovimientoComponent {
  saldo = input.required<number>();
  movimientoValido = output<Movimiento>();

  tipo: 'Recarga' | 'Gasto' = 'Recarga';
  montoTexto = '';

  /** Solo dígitos, signo opcional y hasta 2 decimales (rechaza Infinity, 1e3, 0x10...). */
  private static readonly FORMATO_MONTO = /^-?\d+([.,]\d{1,2})?$/;

  private parseMonto(): number | null {
    const texto = this.montoTexto.trim();
    if (!NuevoMovimientoComponent.FORMATO_MONTO.test(texto)) {
      return null;
    }
    const monto = Number(texto.replace(',', '.'));
    return Number.isFinite(monto) ? monto : null;
  }

  /** Monto numérico y mayor a cero */
  get montoValido(): boolean {
    const monto = this.parseMonto();
    return monto !== null && monto > 0;
  }

  /** true cuando es un gasto que supera el saldo disponible */
  get saldoInsuficiente(): boolean {
    const monto = this.parseMonto();
    return this.tipo === 'Gasto' && monto !== null && monto > 0 && monto > this.saldo();
  }

  /** Formulario completo: monto válido y sin saldo insuficiente */
  get formularioValido(): boolean {
    return this.montoValido && !this.saldoInsuficiente;
  }

  onTipoChange(event: Event): void {
    this.tipo = (event.target as HTMLSelectElement).value as 'Recarga' | 'Gasto';
  }

  onMontoInput(event: Event): void {
    this.montoTexto = (event.target as HTMLInputElement).value;
  }

  registrar(event: Event): void {
    event.preventDefault();

    const monto = this.parseMonto();
    // El hijo nunca emite una solicitud inválida
    if (monto === null || monto <= 0) {
      return;
    }
    if (this.tipo === 'Gasto' && monto > this.saldo()) {
      return;
    }

    this.movimientoValido.emit({ tipo: this.tipo, monto });
    this.montoTexto = ''; // limpia el campo tras emitir un registro válido
  }
}
