package edu.eci.skycampus.estadisticas;

import edu.eci.skycampus.modelo.EstadoMision;
import edu.eci.skycampus.modelo.Mision;
import edu.eci.skycampus.modelo.Prioridad;
import edu.eci.skycampus.modelo.TipoDrone;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Estadísticas del dashboard del operador, calculadas solo con Streams.
 * Supuesto: la lista que reciben los métodos ya es la de las misiones del día (la filtra quien la consulta);
 * estos métodos no filtran por fecha. Las horas son de la ECI (America/Bogota): la espera se mide con zona horaria.
 */
public final class EstadisticasMisiones {
    public static final Duration ESPERA_MAXIMA_URGENTE = Duration.ofMinutes(10);
    private static final double CIEN_POR_CIENTO = 100.0;
    private static final ZoneId ZONA_ECI = ZoneId.of("America/Bogota");
    private static final String MISIONES_NULL = "misiones no puede ser null";

    private EstadisticasMisiones() {
    }

    /** 1) Tipo de drone → número de misiones completadas (ENTREGADA) en la lista del día recibida. */
    public static Map<TipoDrone, Long> completadasPorTipo(List<Mision> misiones) {
        Objects.requireNonNull(misiones, MISIONES_NULL);
        return misiones.stream()
                .filter(mision -> mision.estado() == EstadoMision.ENTREGADA)
                .collect(Collectors.groupingBy(mision -> mision.drone().tipo(), Collectors.counting()));
    }

    /** 2) Drone con más misiones completadas; en empate gana el ID menor para que el resultado sea estable. */
    public static Optional<String> droneConMasCompletadas(List<Mision> misiones) {
        Objects.requireNonNull(misiones, MISIONES_NULL);
        return misiones.stream()
                .filter(mision -> mision.estado() == EstadoMision.ENTREGADA)
                .collect(Collectors.groupingBy(mision -> mision.drone().id(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.<String, Long>comparingByValue()
                        .thenComparing(Map.Entry.comparingByKey(Comparator.reverseOrder())))
                .map(Map.Entry::getKey);
    }

    /** 3) Porcentaje de misiones fallidas sobre el total; 0.0 si no hay misiones. */
    public static double porcentajeFallidas(List<Mision> misiones) {
        Objects.requireNonNull(misiones, MISIONES_NULL);
        if (misiones.isEmpty()) {
            return 0.0;
        }
        Map<Boolean, Long> fallidasONo = misiones.stream()
                .collect(Collectors.partitioningBy(mision -> mision.estado() == EstadoMision.FALLIDA,
                        Collectors.counting()));
        return fallidasONo.get(true) * CIEN_POR_CIENTO / misiones.size();
    }

    /** 4) ¿Hay alguna misión URGENTE en PENDIENTE desde hace más de 10 minutos? */
    public static boolean hasUrgentePendienteDemorada(List<Mision> misiones, LocalDateTime ahora) {
        Objects.requireNonNull(misiones, MISIONES_NULL);
        Objects.requireNonNull(ahora, "ahora no puede ser null");
        return misiones.stream()
                .filter(mision -> mision.prioridad() == Prioridad.URGENTE)
                .filter(mision -> mision.estado() == EstadoMision.PENDIENTE)
                .anyMatch(mision -> esperaMasDeLoPermitido(mision, ahora));
    }

    private static boolean esperaMasDeLoPermitido(Mision mision, LocalDateTime ahora) {
        Duration espera = Duration.between(mision.creadaEn().atZone(ZONA_ECI), ahora.atZone(ZONA_ECI));
        return espera.compareTo(ESPERA_MAXIMA_URGENTE) > 0;
    }
}
