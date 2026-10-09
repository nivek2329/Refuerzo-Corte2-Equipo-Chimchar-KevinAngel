package edu.eci.skycampus.enterprise.routing;

public record CondicionesViento(double velocidadKmH, double direccionDesdeGrados) {
    public CondicionesViento {
        if (!Double.isFinite(velocidadKmH) || velocidadKmH < 0) {
            throw new IllegalArgumentException("la velocidad del viento debe ser finita y no negativa");
        }
        if (!Double.isFinite(direccionDesdeGrados) || direccionDesdeGrados < 0 || direccionDesdeGrados >= 360) {
            throw new IllegalArgumentException("la dirección del viento debe estar entre 0 y menos de 360 grados");
        }
    }
}
