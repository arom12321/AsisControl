package com.asiscontrol.entity.enums;

public enum OperadorComparacion {
    MENOR_QUE,
    MENOR_IGUAL,
    MAYOR_QUE,
    MAYOR_IGUAL,
    IGUAL;

    public boolean cumple(double valor, double umbral) {
        return switch (this) {
            case MENOR_QUE -> valor < umbral;
            case MENOR_IGUAL -> valor <= umbral;
            case MAYOR_QUE -> valor > umbral;
            case MAYOR_IGUAL -> valor >= umbral;
            case IGUAL -> Double.compare(valor, umbral) == 0;
        };
    }
}
