package edu.eci.skycampus.enterprise.infrastructure.web;

import edu.eci.skycampus.enterprise.domain.PrioridadMision;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** Cuerpo de POST /api/v3/misiones. El peso llega en gramos, como en el enunciado. */
public record SolicitudMisionDto(
        @NotBlank String sede,
        @NotBlank String destino,
        @NotNull @Positive Integer pesoPaquete,
        @NotNull PrioridadMision prioridad) { }
