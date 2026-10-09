package edu.eci.skycampus.soporte;

import edu.eci.skycampus.modelo.Destino;
import edu.eci.skycampus.modelo.Drone;
import edu.eci.skycampus.modelo.EstadoDrone;
import edu.eci.skycampus.modelo.EstadoMision;
import edu.eci.skycampus.modelo.Mision;
import edu.eci.skycampus.modelo.Paquete;
import edu.eci.skycampus.modelo.Prioridad;
import edu.eci.skycampus.modelo.SolicitudReparto;
import edu.eci.skycampus.modelo.TipoCarga;
import edu.eci.skycampus.modelo.TipoDrone;

import java.time.LocalDateTime;

/** Fábrica de datos de prueba para no repetir constructores largos en cada prueba. */
public final class Datos {
    public static final LocalDateTime AHORA = LocalDateTime.of(2026, 10, 8, 10, 0);

    private Datos() {
    }

    public static Drone drone(String id, TipoDrone tipo, int bateria) {
        return new Drone(id, tipo, bateria, EstadoDrone.DISPONIBLE, 0);
    }

    public static Drone drone(String id, TipoDrone tipo, int bateria, EstadoDrone estado, int minutosVuelo) {
        return new Drone(id, tipo, bateria, estado, minutosVuelo);
    }

    public static Paquete paquete(int pesoGramos, Prioridad prioridad) {
        return new Paquete(pesoGramos, TipoCarga.CARPETA, prioridad);
    }

    public static SolicitudReparto solicitud(String id, int pesoGramos, Prioridad prioridad) {
        return new SolicitudReparto(id, Destino.BLOQUE_A, Destino.BIBLIOTECA, paquete(pesoGramos, prioridad));
    }

    public static Mision mision(String id, Drone drone, EstadoMision estado, Prioridad prioridad,
                                LocalDateTime creadaEn) {
        return new Mision(id, drone, solicitud("S-" + id, 200, prioridad), estado, creadaEn);
    }
}
