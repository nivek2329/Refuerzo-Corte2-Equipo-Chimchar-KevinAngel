import java.util.Objects;

public class RutaDirecta implements EstrategiaRuta {
    @Override
    public String calcular(String origen, String destino) {
        Objects.requireNonNull(origen, "origen no puede ser null");
        Objects.requireNonNull(destino, "destino no puede ser null");
        return "Ruta directa: " + origen + " → " + destino;
    }
}
