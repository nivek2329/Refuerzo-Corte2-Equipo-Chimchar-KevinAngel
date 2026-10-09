package edu.eci.skycampus.asignacion;

import edu.eci.skycampus.externo.ApiMeteorologica;
import edu.eci.skycampus.modelo.Drone;
import edu.eci.skycampus.modelo.EstadoDrone;
import edu.eci.skycampus.modelo.Paquete;
import edu.eci.skycampus.modelo.Prioridad;
import edu.eci.skycampus.modelo.SolicitudReparto;
import edu.eci.skycampus.modelo.TipoDrone;
import edu.eci.skycampus.notificacion.GestorFlota;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Asigna automáticamente un drone a una solicitud (SkyCampus v2):
 * 1) rechaza paquetes que ningún tipo de drone puede llevar (sin gastar una consulta al clima);
 * 2) no despega si la API meteorológica dice que no es apto;
 * 3) usa la estrategia configurada para la prioridad del paquete (relación inyectada: sin condicionales);
 * 4) pone el drone EN_VUELO a través de GestorFlota, que notifica a los observadores.
 *
 * <p>Contrato: no modifica la lista recibida. {@link Drone} es inmutable, así que quien llama debe reemplazar en
 * su flota el drone devuelto; si vuelve a pasar la lista original, el mismo drone podría elegirse otra vez.
 */
public class AsignadorMision {
    private static final int CAPACIDAD_TIPO_MAS_GRANDE_GRAMOS = TipoDrone.capacidadMaximaGramos();

    private final ApiMeteorologica clima;
    private final GestorFlota gestorFlota;
    private final Map<Prioridad, EstrategiaAsignacion> estrategiasPorPrioridad = new EnumMap<>(Prioridad.class);

    public AsignadorMision(ApiMeteorologica clima, GestorFlota gestorFlota,
                           Map<Prioridad, EstrategiaAsignacion> estrategiasPorPrioridad) {
        this.clima = Objects.requireNonNull(clima, "clima no puede ser null");
        this.gestorFlota = Objects.requireNonNull(gestorFlota, "gestorFlota no puede ser null");
        this.estrategiasPorPrioridad.putAll(
                Objects.requireNonNull(estrategiasPorPrioridad, "estrategiasPorPrioridad no puede ser null"));
    }

    public Optional<Drone> asignar(List<Drone> flota, SolicitudReparto solicitud) {
        Objects.requireNonNull(flota, "flota no puede ser null");
        Objects.requireNonNull(solicitud, "solicitud no puede ser null");
        Paquete paquete = solicitud.paquete();
        validarPeso(paquete);
        if (!clima.esApto()) {
            return Optional.empty();
        }
        return estrategiasPorPrioridad.get(paquete.prioridad()).seleccionar(flota, paquete)
                .map(drone -> gestorFlota.cambiarEstado(drone, EstadoDrone.EN_VUELO));
    }

    private static void validarPeso(Paquete paquete) {
        if (paquete.pesoGramos() > CAPACIDAD_TIPO_MAS_GRANDE_GRAMOS) {
            throw new IllegalArgumentException("el paquete pesa " + paquete.pesoGramos()
                    + " g y supera la capacidad máxima de la flota (" + CAPACIDAD_TIPO_MAS_GRANDE_GRAMOS + " g)");
        }
    }
}
