import java.util.Comparator;
import java.util.List;

public class AsignarPorMayorBateria implements EstrategiaAsignacion {
    @Override
    public Drone elegir(List<Drone> disponibles) {
        return disponibles.stream()
                .filter(Drone::disponible)
                .max(Comparator.comparingInt(Drone::bateria))
                .orElseThrow(() -> new IllegalStateException("No hay drones disponibles"));
    }
}
