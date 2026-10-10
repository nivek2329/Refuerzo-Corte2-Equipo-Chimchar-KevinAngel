package edu.eci.skycampus.enterprise.domain;

/** Los cinco flujos alternos de la asignación Enterprise (SC-15 / reto 12). */
public enum MotivoRechazo {
    SEDE_INACTIVA("la sede no está operando"),
    CLIMA_ADVERSO("las condiciones climáticas no permiten volar"),
    AEROCIVIL_RECHAZA("la Aerocivil no autorizó la ruta"),
    SIN_DRONES_DISPONIBLES("no hay drones disponibles aptos para la misión"),
    PAQUETE_DEMASIADO_PESADO("ningún drone disponible soporta el peso del paquete");

    private final String mensaje;

    MotivoRechazo(String mensaje) {
        this.mensaje = mensaje;
    }

    public String mensaje() {
        return mensaje;
    }
}
