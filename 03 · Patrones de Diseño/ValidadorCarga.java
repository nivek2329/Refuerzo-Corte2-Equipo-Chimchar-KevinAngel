import java.util.Objects;
import java.util.Optional;

public class ValidadorCarga extends ValidadorMision {
    private final TipoCarga capacidadMaxima;

    public ValidadorCarga(TipoCarga capacidadMaxima) {
        this.capacidadMaxima = Objects.requireNonNull(capacidadMaxima, "capacidadMaxima no puede ser null");
    }

    @Override
    protected Optional<String> validarPropio(Mision mision) {
        return mision.tipoCarga().nivelCapacidad() > capacidadMaxima.nivelCapacidad()
                ? Optional.of("Carga " + mision.tipoCarga() + " supera la capacidad del drone")
                : Optional.empty();
    }
}
