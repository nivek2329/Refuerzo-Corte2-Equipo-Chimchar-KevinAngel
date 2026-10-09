package edu.eci.skycampus.modelo;

/** Tipos de drone de la flota v2 con el rango de peso (en gramos) que cada uno puede llevar. */
public enum TipoDrone {
    MINI(1, 500),
    CARGO(100, 2000),
    EXPRESS(1, 800);

    private final int pesoMinimoGramos;
    private final int capacidadGramos;

    TipoDrone(int pesoMinimoGramos, int capacidadGramos) {
        this.pesoMinimoGramos = pesoMinimoGramos;
        this.capacidadGramos = capacidadGramos;
    }

    public int capacidadGramos() {
        return capacidadGramos;
    }

    /** Un CARGO no se usa para paquetes de menos de 100 g (regla de negocio SC-07). */
    public boolean esCompatibleCon(int pesoGramos) {
        return pesoGramos >= pesoMinimoGramos && pesoGramos <= capacidadGramos;
    }
}
