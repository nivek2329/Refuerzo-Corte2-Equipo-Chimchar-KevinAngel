package edu.eci.skycampus.enterprise.infrastructure;

import edu.eci.skycampus.enterprise.domain.RepositorioSedes;
import edu.eci.skycampus.enterprise.domain.Sede;
import java.util.Objects;
import java.util.Set;

/** Adaptador de demostración: las sedes activas se pasan al construirlo. */
public final class RepositorioSedesEnMemoria implements RepositorioSedes {
    private final Set<Sede> activas;

    public RepositorioSedesEnMemoria(Set<Sede> activas) {
        this.activas = Set.copyOf(Objects.requireNonNull(activas, "activas no puede ser null"));
    }

    @Override
    public boolean estaActiva(Sede sede) {
        return activas.contains(Objects.requireNonNull(sede, "sede no puede ser null"));
    }
}
