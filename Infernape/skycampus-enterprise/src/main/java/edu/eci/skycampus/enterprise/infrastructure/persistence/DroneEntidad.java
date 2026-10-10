package edu.eci.skycampus.enterprise.infrastructure.persistence;

import edu.eci.skycampus.enterprise.domain.Drone;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "drone")
public class DroneEntidad {
    @Id
    private String id;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "sede_codigo")
    private SedeEntidad sede;
    private int bateria;
    private double capacidadCargaKg;
    private boolean disponible;
    private boolean express;

    protected DroneEntidad() { }

    public boolean isDisponible() { return disponible; }

    public void marcarNoDisponible() { this.disponible = false; }

    public Drone aDominio() {
        return new Drone(id, sede.aDominio(), bateria, capacidadCargaKg, disponible, express);
    }
}
