package edu.eci.skycampus.enterprise.infrastructure.persistence;

import edu.eci.skycampus.enterprise.domain.Sede;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sede")
public class SedeEntidad {
    @Id
    private String codigo;
    private String universidad;
    private boolean activa;

    protected SedeEntidad() { }

    public boolean isActiva() { return activa; }

    public Sede aDominio() {
        return new Sede(codigo, universidad);
    }
}
