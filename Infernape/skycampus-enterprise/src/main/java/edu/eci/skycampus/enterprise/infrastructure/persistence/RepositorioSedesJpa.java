package edu.eci.skycampus.enterprise.infrastructure.persistence;

import edu.eci.skycampus.enterprise.domain.RepositorioSedes;
import edu.eci.skycampus.enterprise.domain.Sede;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class RepositorioSedesJpa implements RepositorioSedes {
    private final SedeJpaRepository sedes;

    public RepositorioSedesJpa(SedeJpaRepository sedes) {
        this.sedes = sedes;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean estaActiva(Sede sede) {
        return sedes.findById(sede.codigo()).map(SedeEntidad::isActiva).orElse(false);
    }

    @Transactional(readOnly = true)
    public Optional<Sede> buscar(String codigo) {
        return sedes.findById(codigo).map(SedeEntidad::aDominio);
    }
}
