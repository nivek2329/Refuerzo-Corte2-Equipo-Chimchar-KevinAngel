package edu.eci.skycampus.modelo;

import java.util.Objects;

public record SolicitudReparto(String id, Destino origen, Destino destino, Paquete paquete) {
    public SolicitudReparto {
        Objects.requireNonNull(id, "id no puede ser null");
        Objects.requireNonNull(origen, "origen no puede ser null");
        Objects.requireNonNull(destino, "destino no puede ser null");
        Objects.requireNonNull(paquete, "paquete no puede ser null");
        if (origen == destino) {
            throw new IllegalArgumentException("origen y destino deben ser distintos");
        }
    }
}
