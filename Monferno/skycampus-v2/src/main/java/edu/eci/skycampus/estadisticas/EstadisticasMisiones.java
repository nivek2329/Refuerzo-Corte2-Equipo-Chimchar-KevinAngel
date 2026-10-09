package edu.eci.skycampus.estadisticas;

import edu.eci.skycampus.modelo.EstadoMision;
import edu.eci.skycampus.modelo.Mision;
import edu.eci.skycampus.modelo.Prioridad;
import edu.eci.skycampus.modelo.TipoDrone;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/** Estadísticas del dashboard del operador sobre las misiones del día. Todo con Streams. */
public final class EstadisticasMisiones {
    public static final Duration ESPERA_MAXIMA_URGENTE = Duration.ofMinutes(10);
    private static final double CIEN_POR_CIENTO = 100.0;

    private EstadisticasMisiones() {
    }

    /** 1) Tipo de drone → número de misiones completadas (ENTREGADA). */
    public static Map<TipoDrone, Long> completadasPorTipo(List<Mision> misiones) {
        Objects.requireNonNull(misiones, "misiones no puede ser null");
        return misiones.stream()
                .filter(mision -> mision.estado() == EstadoMision.ENTREGADA)
                .collect(Collectors.groupingBy(mision -> mision.drone().tipo(), Collectors.counting()));
    }

    /** 2) Drone con más misiones completadas; en empate gana el ID menor para que el resultado sea estable. */
    public static Optional<String> droneConMasCompletadas(List<Mision> misiones) {
        Objects.requireNonNull(misiones, "misiones no puede ser null");
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
        Objects.requireNonNull(misiones, "misiones no puede ser null");
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
        Objects.requireNonNull(misiones, "misiones no puede ser null");
        Objects.requireNonNull(ahora, "ahora no puede ser null");
        return misiones.stream()
                .filter(mision -> mision.prioridad() == Prioridad.URGENTE)
                .filter(mision -> mision.estado() == EstadoMision.PENDIENTE)
                .anyMatch(mision -> esperaMasDeLoPermitido(mision, ahora));
    }

    private static boolean esperaMasDeLoPermitido(Mision mision, LocalDateTime ahora) {
        return Duration.between(mision.creadaEn(), ahora).compareTo(ESPERA_MAXIMA_URGENTE) > 0;
    }
}
