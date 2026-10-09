import java.util.Optional;

public class ValidadorCarga extends ValidadorMision {
    @Override
    protected Optional<String> validarPropio(Mision mision) {
        return mision.tipoCarga().capacidadRequerida().nivel()
                > mision.drone().capacidadMaxima().nivel()
                ? Optional.of("Carga " + mision.tipoCarga() + " supera la capacidad del drone")
                : Optional.empty();
    }
}
