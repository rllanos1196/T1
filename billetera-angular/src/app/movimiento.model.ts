/**
 * Modelo de la Billetera Digital.
 * tipo: 'Recarga' o 'Gasto' (union type)
 * monto: monto del movimiento (> 0)
 */
export interface Movimiento {
  tipo: 'Recarga' | 'Gasto';
  monto: number;
}
