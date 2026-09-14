import java.util.List;
import java.util.Objects;

public class AsignadorDeMision {
    private EstrategiaAsignacion estrategia;

    public AsignadorDeMision(EstrategiaAsignacion estrategia) {
        this.estrategia = Objects.requireNonNull(estrategia, "estrategia no puede ser null");
    }

    public void cambiarEstrategia(EstrategiaAsignacion estrategia) {
        this.estrategia = Objects.requireNonNull(estrategia, "estrategia no puede ser null");
    }

    public Drone asignar(List<Drone> flota) {
        return estrategia.elegir(flota);
    }
}
