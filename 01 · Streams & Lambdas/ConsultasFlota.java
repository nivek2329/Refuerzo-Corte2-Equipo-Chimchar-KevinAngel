import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class ConsultasFlota {
    public static final int BATERIA_SUFICIENTE = 50;
    public static final int BATERIA_CRITICA = 20;

    private ConsultasFlota() {
    }

    public static List<String> idsDisponiblesConBateriaSuficiente(List<Drone> drones) {
        Objects.requireNonNull(drones, "drones no puede ser null");
        return drones.stream()
                .filter(Drone::disponible)
                .filter(drone -> drone.bateria() >= BATERIA_SUFICIENTE)
                .sorted(Comparator.comparingInt(Drone::bateria).reversed())
                .map(Drone::id)
                .toList();
    }

    public static boolean hasDroneDisponibleEn(List<Drone> drones, String ubicacion) {
        Objects.requireNonNull(drones, "drones no puede ser null");
        Objects.requireNonNull(ubicacion, "ubicacion no puede ser null");
        return drones.stream()
                .filter(Drone::disponible)
                .anyMatch(drone -> ubicacion.equals(drone.ubicacion()));
    }

    public static long contarBateriaCritica(List<Drone> drones) {
        Objects.requireNonNull(drones, "drones no puede ser null");
        return drones.stream()
                .filter(drone -> drone.bateria() < BATERIA_CRITICA)
                .count();
    }

    public static List<String> listarIdYBateria(List<Drone> drones) {
        Objects.requireNonNull(drones, "drones no puede ser null");
        return drones.stream()
                .map(drone -> drone.id() + ": " + drone.bateria() + "%")
                .toList();
    }
}
