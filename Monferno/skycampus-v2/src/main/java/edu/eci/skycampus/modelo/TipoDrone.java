package edu.eci.skycampus.modelo;

/** Tipos de drone de la flota v2: rango de peso (en gramos) y velocidad relativa (mayor = más rápido). */
public enum TipoDrone {
    MINI(1, 500, 2),
    CARGO(100, 2000, 1),
    EXPRESS(1, 800, 3);

    private final int pesoMinimoGramos;
    private final int capacidadGramos;
    private final int velocidadRelativa;

    TipoDrone(int pesoMinimoGramos, int capacidadGramos, int velocidadRelativa) {
        this.pesoMinimoGramos = pesoMinimoGramos;
        this.capacidadGramos = capacidadGramos;
        this.velocidadRelativa = velocidadRelativa;
    }

    public int capacidadGramos() {
        return capacidadGramos;
    }

    public int velocidadRelativa() {
        return velocidadRelativa;
    }

    /** Un CARGO no se usa para paquetes de menos de 100 g (regla de negocio SC-07). */
    public boolean esCompatibleCon(int pesoGramos) {
        return pesoGramos >= pesoMinimoGramos && pesoGramos <= capacidadGramos;
    }
}
