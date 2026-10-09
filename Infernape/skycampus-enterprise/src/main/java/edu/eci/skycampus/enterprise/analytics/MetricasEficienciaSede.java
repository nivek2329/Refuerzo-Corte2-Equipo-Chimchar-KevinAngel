package edu.eci.skycampus.enterprise.analytics;

import edu.eci.skycampus.enterprise.domain.Sede;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;

public record MetricasEficienciaSede(
        Sede sede,
        long totalMisiones,
        long misionesEntregadas,
        double tasaExito,
        OptionalDouble tiempoPromedioEntregaMinutos,
        Optional<String> droneMasUtilizado,
        double porcentajeUrgentes) {

    public MetricasEficienciaSede {
        Objects.requireNonNull(sede, "sede no puede ser null");
        Objects.requireNonNull(tiempoPromedioEntregaMinutos, "tiempoPromedio no puede ser null");
        Objects.requireNonNull(droneMasUtilizado, "droneMasUtilizado no puede ser null");
        validarConteos(totalMisiones, misionesEntregadas);
    }

    private static void validarConteos(long totalMisiones, long misionesEntregadas) {
        if (totalMisiones < 1 || misionesEntregadas < 0 || misionesEntregadas > totalMisiones) {
            throw new IllegalArgumentException("conteos de misiones fuera de rango");
        }
    }
}
