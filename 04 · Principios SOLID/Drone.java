import java.util.Objects;

public record Drone(
        String id,
        String modelo,
        int bateria,
        boolean disponible,
        String ubicacion
) {
    public Drone {
        Objects.requireNonNull(id, "id no puede ser null");
        Objects.requireNonNull(modelo, "modelo no puede ser null");
        Objects.requireNonNull(ubicacion, "ubicacion no puede ser null");
    }
}
