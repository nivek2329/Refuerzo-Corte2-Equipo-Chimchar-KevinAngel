import java.util.List;
import java.util.Objects;

/**
 * Genera el reporte de misiones en PDF, el único formato del MVP. Stub del reto 04:
 * la generación real con iText está fuera del alcance de Chimchar.
 */
public class GeneradorReporte {
    public void generarPdf(List<Mision> misiones) {
        Objects.requireNonNull(misiones, "misiones no puede ser null");
    }
}
