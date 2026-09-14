import java.util.Optional;

public class ValidadorBateria extends ValidadorMision {
    private static final int BATERIA_MINIMA = 30;

    @Override
    protected Optional<String> validarPropio(Mision mision) {
        return mision.drone().bateria() < BATERIA_MINIMA
                ? Optional.of("Batería insuficiente: " + mision.drone().bateria() + "%")
                : Optional.empty();
    }
}
