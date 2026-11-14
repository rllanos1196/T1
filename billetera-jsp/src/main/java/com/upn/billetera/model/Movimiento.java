package com.upn.billetera.model;

/**
 * POJO que representa un movimiento de la billetera.
 * tipo: 'Recarga' o 'Gasto'
 * monto: monto del movimiento (> 0)
 */
public class Movimiento {

    private String tipo;
    private double monto;

    public Movimiento() {
    }

    public Movimiento(String tipo, double monto) {
        this.tipo = tipo;
        this.monto = monto;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }
}
