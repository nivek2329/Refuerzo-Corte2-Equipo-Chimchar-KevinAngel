package edu.eci.skycampus.enterprise.routing;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Reglas de negocio de SC-15 (planificar ruta multi-etapa inter-sede):
 * RN-1: ningún drone vuela más de 5 km con carga sin pasar por una estación de carga;
 * RN-2: el paquete no espera más de 30 minutos en una estación entre dos etapas.
 */
public final class ReglasRutaMultiEtapa {
    public static final double MAX_KM_CON_CARGA_SIN_RECARGAR = 5.0;
    public static final Duration MAX_ESPERA_EN_ESTACION = Duration.ofMinutes(30);

    /** Un incumplimiento concreto: qué etapa (o espera) y qué regla rompe. */
    public record Incumplimiento(String referencia, String regla) { }

    private ReglasRutaMultiEtapa() { }

    /**
     * @param etapas tramos en orden
     * @param esperasEnEstacion espera del paquete entre la etapa i y la i+1 (una menos que las etapas)
     */
    public static List<Incumplimiento> incumplimientos(List<EtapaRuta> etapas, List<Duration> esperasEnEstacion) {
        Objects.requireNonNull(etapas, "etapas no puede ser null");
        Objects.requireNonNull(esperasEnEstacion, "esperasEnEstacion no puede ser null");
        if (etapas.isEmpty() || esperasEnEstacion.size() != etapas.size() - 1) {
            throw new IllegalArgumentException("debe haber una espera entre cada par de etapas consecutivas");
        }
        List<Incumplimiento> encontrados = new ArrayList<>();
        etapas.stream()
                .filter(etapa -> etapa.cargaKg() > 0 && etapa.distanciaKm() > MAX_KM_CON_CARGA_SIN_RECARGAR)
                .forEach(etapa -> encontrados.add(new Incumplimiento(etapa.id(),
                        "más de 5 km con carga sin recargar")));
        for (int i = 0; i < esperasEnEstacion.size(); i++) {
            Duration espera = Objects.requireNonNull(esperasEnEstacion.get(i), "espera no puede ser null");
            if (espera.isNegative() || espera.compareTo(MAX_ESPERA_EN_ESTACION) > 0) {
                encontrados.add(new Incumplimiento(etapas.get(i).destino().codigo(),
                        "el paquete no puede esperar más de 30 min en la estación"));
            }
        }
        return List.copyOf(encontrados);
    }

    public static void validar(List<EtapaRuta> etapas, List<Duration> esperasEnEstacion) {
        List<Incumplimiento> encontrados = incumplimientos(etapas, esperasEnEstacion);
        if (!encontrados.isEmpty()) {
            Incumplimiento primero = encontrados.get(0);
            throw new IllegalArgumentException(primero.referencia() + ": " + primero.regla());
        }
    }
}
