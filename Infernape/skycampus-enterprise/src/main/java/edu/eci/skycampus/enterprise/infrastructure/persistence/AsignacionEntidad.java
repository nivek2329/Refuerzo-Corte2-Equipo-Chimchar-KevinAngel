package edu.eci.skycampus.enterprise.infrastructure.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "asignacion")
public class AsignacionEntidad {
    @Id
    private String misionId;
    private String droneId;
    private Instant asignadaEn;

    protected AsignacionEntidad() { }

    public AsignacionEntidad(String misionId, String droneId, Instant asignadaEn) {
        this.misionId = misionId;
        this.droneId = droneId;
        this.asignadaEn = asignadaEn;
    }
}
