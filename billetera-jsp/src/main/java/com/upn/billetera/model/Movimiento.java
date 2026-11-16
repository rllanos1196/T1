package com.upn.billetera.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * POJO que representa un movimiento de la billetera.
 * tipo: 'Recarga' o 'Gasto'
 * monto: monto del movimiento (> 0). Se usa BigDecimal para evitar
 * errores de redondeo propios de double con montos de dinero.
 */
public class Movimiento implements Serializable {

    private static final long serialVersionUID = 1L;

    private String tipo;
    private BigDecimal monto;

    public Movimiento() {
    }

    public Movimiento(String tipo, BigDecimal monto) {
        this.tipo = tipo;
        this.monto = monto;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }
}
