import java.util.Objects;

public class RutaEvitandoEdificios implements EstrategiaRuta {
    @Override
    public String calcular(String origen, String destino) {
        Objects.requireNonNull(origen, "origen no puede ser null");
        Objects.requireNonNull(destino, "destino no puede ser null");
        return "Ruta desde " + origen + " hasta " + destino + " evitando edificios altos";
    }
}
