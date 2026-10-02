package com.asiscontrol.entity.enums;

public enum NivelLogro {
    AD(4),
    A(3),
    B(2),
    C(1);

    private final int valor;

    NivelLogro(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }

    public static NivelLogro desdePromedio(double promedio) {
        if (promedio >= 3.5) {
            return AD;
        }
        if (promedio >= 2.5) {
            return A;
        }
        if (promedio >= 1.5) {
            return B;
        }
        return C;
    }
}
