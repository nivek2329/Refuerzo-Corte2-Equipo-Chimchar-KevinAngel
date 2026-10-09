package edu.eci.skycampus.enterprise.routing;

import edu.eci.skycampus.enterprise.domain.Sede;
import java.util.Objects;

public record EtapaRuta(
        String id,
        Sede origen,
        Sede destino,
        double distanciaKm,
        double rumboGrados,
        double nivelRiesgo,
        double cargaKg,
        TipoDroneEtapa tipoDroneRequerido) {

    public EtapaRuta {
        Objects.requireNonNull(id, "id de etapa no puede ser null");
        Objects.requireNonNull(origen, "origen no puede ser null");
        Objects.requireNonNull(destino, "destino no puede ser null");
        Objects.requireNonNull(tipoDroneRequerido, "tipo de drone requerido no puede ser null");
        validarDatos(id, origen, destino, distanciaKm, rumboGrados, nivelRiesgo, cargaKg);
    }

    private static void validarDatos(String id, Sede origen, Sede destino, double distanciaKm,
                                     double rumboGrados, double nivelRiesgo, double cargaKg) {
        if (id.isBlank() || origen.equals(destino) || !Double.isFinite(distanciaKm)
                || !Double.isFinite(rumboGrados) || !Double.isFinite(nivelRiesgo)
                || !Double.isFinite(cargaKg) || distanciaKm <= 0 || rumboGrados < 0 || rumboGrados >= 360
                || nivelRiesgo < 0 || nivelRiesgo > 1 || cargaKg < 0) {
            throw new IllegalArgumentException("datos de etapa de ruta inválidos");
        }
    }
}
