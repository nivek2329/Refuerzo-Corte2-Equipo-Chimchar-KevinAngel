package edu.eci.skycampus.enterprise.infrastructure.web;

import edu.eci.skycampus.enterprise.application.AsignadorMision;
import edu.eci.skycampus.enterprise.domain.EstadoMision;
import edu.eci.skycampus.enterprise.domain.MotivoRechazo;
import edu.eci.skycampus.enterprise.domain.ResultadoAsignacion;
import edu.eci.skycampus.enterprise.domain.Sede;
import edu.eci.skycampus.enterprise.domain.SolicitudAsignacion;
import edu.eci.skycampus.enterprise.infrastructure.persistence.RepositorioSedesJpa;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/** Adaptador de entrada REST: traduce HTTP ↔ caso de uso. 201 si asigna, 422 con el motivo si no. */
@RestController
@RequestMapping("/api/v3/misiones")
public class MisionController {
    private static final double GRAMOS_POR_KG = 1000.0;
    private final AsignadorMision asignador;
    private final RepositorioSedesJpa sedes;

    public MisionController(AsignadorMision asignador, RepositorioSedesJpa sedes) {
        this.asignador = asignador;
        this.sedes = sedes;
    }

    @PostMapping
    public ResponseEntity<RespuestaMisionDto> crear(@Valid @RequestBody SolicitudMisionDto cuerpo) {
        Sede sede = sedes.buscar(cuerpo.sede())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "sede desconocida"));
        String misionId = "M-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        SolicitudAsignacion solicitud = new SolicitudAsignacion(misionId, sede, sede.codigo(), cuerpo.destino(),
                cuerpo.pesoPaquete() / GRAMOS_POR_KG, cuerpo.prioridad());
        ResultadoAsignacion resultado = asignador.evaluar(solicitud);
        if (resultado.fueAsignado()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(new RespuestaMisionDto(misionId,
                    EstadoMision.EN_VUELO.name(), RespuestaMisionDto.DroneAsignadoDto.de(resultado.drone().orElseThrow()),
                    null, null));
        }
        MotivoRechazo motivo = resultado.motivo().orElseThrow();
        return ResponseEntity.unprocessableEntity().body(new RespuestaMisionDto(misionId, "RECHAZADA", null,
                motivo.name(), motivo.mensaje()));
    }
}
