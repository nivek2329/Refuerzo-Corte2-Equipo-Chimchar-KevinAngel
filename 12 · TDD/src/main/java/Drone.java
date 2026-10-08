import java.util.Objects;

/** Drone mínimo del módulo TDD, con los atributos usados por SC-01. */
public record Drone(String id, String modelo, int bateria, boolean disponible, String ubicacion) {
    public Drone {
        Objects.requireNonNull(id, "id no puede ser null");
        Objects.requireNonNull(modelo, "modelo no puede ser null");
        Objects.requireNonNull(ubicacion, "ubicacion no puede ser null");
        if (bateria < 0 || bateria > 100) {
            throw new IllegalArgumentException("bateria debe estar entre 0 y 100");
        }
    }
}
