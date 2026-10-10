package edu.eci.skycampus.enterprise.infrastructure.web;

import edu.eci.skycampus.enterprise.domain.Drone;
import edu.eci.skycampus.enterprise.domain.RepositorioFlota;
import edu.eci.skycampus.enterprise.domain.Sede;
import edu.eci.skycampus.enterprise.infrastructure.persistence.RepositorioSedesJpa;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v3/flota")
public class FlotaController {
    private final RepositorioFlota flota;
    private final RepositorioSedesJpa sedes;

    public FlotaController(RepositorioFlota flota, RepositorioSedesJpa sedes) {
        this.flota = flota;
        this.sedes = sedes;
    }

    @GetMapping("/{codigoSede}/disponibles")
    public List<String> disponibles(@PathVariable String codigoSede) {
        Sede sede = sedes.buscar(codigoSede)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "sede desconocida"));
        return flota.findDisponibles(sede).stream().map(Drone::id).toList();
    }
}
