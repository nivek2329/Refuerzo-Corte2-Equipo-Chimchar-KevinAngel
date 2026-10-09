package edu.eci.skycampus.enterprise.routing;

public enum TipoDroneEtapa {
    LIGERO(30.0, 2.0),
    CARGA(24.0, 15.0);

    private final double velocidadNominalKmH;
    private final double capacidadCargaKg;

    TipoDroneEtapa(double velocidadNominalKmH, double capacidadCargaKg) {
        this.velocidadNominalKmH = velocidadNominalKmH;
        this.capacidadCargaKg = capacidadCargaKg;
    }

    public double velocidadNominalKmH() { return velocidadNominalKmH; }

    public double capacidadCargaKg() { return capacidadCargaKg; }
}
