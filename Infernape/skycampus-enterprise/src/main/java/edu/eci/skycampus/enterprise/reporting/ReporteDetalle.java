package edu.eci.skycampus.enterprise.reporting;

import edu.eci.skycampus.enterprise.domain.Mision;
import edu.eci.skycampus.enterprise.domain.Sede;
import java.util.List;

/** Producto B de la familia: una fila por misión de la sede. */
public interface ReporteDetalle {
    FormatoReporte formato();

    String generar(Sede sede, List<Mision> misiones);
}
