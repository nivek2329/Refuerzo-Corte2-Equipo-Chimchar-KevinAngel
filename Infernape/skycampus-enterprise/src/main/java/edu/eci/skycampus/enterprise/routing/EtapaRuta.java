package edu.eci.skycampus.enterprise.routing;

import edu.eci.skycampus.enterprise.domain.Sede;
import java.util.Arrays;
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
        boolean valida = !id.isBlank() && !origen.equals(destino)
                && finitos(distanciaKm, rumboGrados, nivelRiesgo, cargaKg)
                && distanciaKm > 0 && cargaKg >= 0
                && enRango(rumboGrados, 0, 360) && nivelRiesgo >= 0 && nivelRiesgo <= 1;
        if (!valida) {
            throw new IllegalArgumentException("datos de etapa de ruta inválidos");
        }
    }

    private static boolean finitos(double... valores) {
        return Arrays.stream(valores).allMatch(Double::isFinite);
    }

    /** {@code minimo <= valor < maximo}. */
    private static boolean enRango(double valor, double minimo, double maximo) {
        return valor >= minimo && valor < maximo;
    }
}
