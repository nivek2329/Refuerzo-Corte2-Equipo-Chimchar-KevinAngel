package edu.eci.skycampus.asignacion;

import edu.eci.skycampus.modelo.Prioridad;

import java.util.Map;

/**
 * Política de asignación de SkyCampus v2: qué estrategia usa cada prioridad.
 * URGENTE → el drone más rápido (EXPRESS primero); NORMAL y BAJO → el de mayor batería.
 * Es la configuración por defecto de {@link AsignadorMision}; se puede inyectar otra sin modificarlo (OCP).
 */
public final class PoliticaAsignacion {
    private PoliticaAsignacion() {
    }

    public static Map<Prioridad, EstrategiaAsignacion> porDefecto() {
        return Map.of(
                Prioridad.URGENTE, new AsignacionMasRapido(),
                Prioridad.NORMAL, new AsignacionMayorBateria(),
                Prioridad.BAJO, new AsignacionMayorBateria());
    }
}
