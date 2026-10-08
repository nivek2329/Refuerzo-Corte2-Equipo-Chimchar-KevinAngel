import java.util.Objects;

public record Mision(
        String id,
        Drone drone,
        String origen,
        String destino,
        TipoCarga tipoCarga,
        EstadoMision estado
) {
    public Mision {
        Objects.requireNonNull(id, "id no puede ser null");
        Objects.requireNonNull(drone, "drone no puede ser null");
        Objects.requireNonNull(origen, "origen no puede ser null");
        Objects.requireNonNull(destino, "destino no puede ser null");
        Objects.requireNonNull(tipoCarga, "tipoCarga no puede ser null");
        Objects.requireNonNull(estado, "estado no puede ser null");
    }
}
