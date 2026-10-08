import java.util.Objects;

/**
 * Drone del reto 03. Añade {@code capacidadMaxima} al modelo Chimchar porque el
 * Problema 2 exige que ValidadorCarga compare la carga con la capacidad del drone.
 */
public record Drone(
        String id,
        String modelo,
        int bateria,
        boolean disponible,
        String ubicacion,
        CapacidadCarga capacidadMaxima
) {
    private static final int BATERIA_MINIMA_VALIDA = 0;
    private static final int BATERIA_MAXIMA_VALIDA = 100;

    public Drone {
        Objects.requireNonNull(id, "id no puede ser null");
        Objects.requireNonNull(modelo, "modelo no puede ser null");
        Objects.requireNonNull(ubicacion, "ubicacion no puede ser null");
        Objects.requireNonNull(capacidadMaxima, "capacidadMaxima no puede ser null");
        if (bateria < BATERIA_MINIMA_VALIDA || bateria > BATERIA_MAXIMA_VALIDA) {
            throw new IllegalArgumentException("La batería debe estar entre "
                    + BATERIA_MINIMA_VALIDA + " y " + BATERIA_MAXIMA_VALIDA + "%: " + bateria);
        }
    }
}
