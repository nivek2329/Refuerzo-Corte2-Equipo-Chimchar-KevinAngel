import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class AsignacionPorMayorBateria implements EstrategiaAsignacion {
    @Override
    public Drone elegir(List<Drone> flota) {
        return Objects.requireNonNull(flota, "flota no puede ser null").stream()
                .filter(Drone::disponible)
                .max(Comparator.comparingInt(Drone::bateria))
                .orElseThrow(() -> new IllegalStateException("No hay drones disponibles"));
    }
}
