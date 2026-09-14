import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConsultasFlota {

    static List<String> idsDisponiblesBateriaSuficiente(List<Drone> drones, int bateriaMinima) {
        Objects.requireNonNull(drones, "drones no puede ser null");
        if (bateriaMinima < 0) {
            throw new IllegalArgumentException("bateriaMinima no puede ser negativa");
        }
        return drones.stream()
                .filter(d -> d.disponible() && d.bateria() >= bateriaMinima)
                .sorted(Comparator.comparingInt(Drone::bateria).reversed())
                .map(Drone::id)
                .collect(Collectors.toList());
    }

    static boolean existeDroneDisponibleEn(List<Drone> drones, String bloque) {
        Objects.requireNonNull(drones, "drones no puede ser null");
        Objects.requireNonNull(bloque, "bloque no puede ser null");
        return drones.stream()
                .anyMatch(d -> d.disponible() && d.ubicacion().equals(bloque));
    }

    static long contarBateriaCritica(List<Drone> drones, int umbralCritico) {
        Objects.requireNonNull(drones, "drones no puede ser null");
        if (umbralCritico < 0) {
            throw new IllegalArgumentException("umbralCritico no puede ser negativo");
        }
        return drones.stream()
                .filter(d -> d.bateria() < umbralCritico)
                .count();
    }

    static List<String> listarIdYBateria(List<Drone> drones) {
        Objects.requireNonNull(drones, "drones no puede ser null");
        return drones.stream()
                .map(d -> d.id() + ": " + d.bateria() + "%")
                .collect(Collectors.toList());
    }
}