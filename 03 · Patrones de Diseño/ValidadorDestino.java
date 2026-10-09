import java.util.Optional;
import java.util.Set;

public class ValidadorDestino extends ValidadorMision {
    private static final Set<String> DESTINOS_VALIDOS = Set.of(
            "Bloque A", "Bloque B", "Bloque C", "Bloque D", "Biblioteca");

    @Override
    protected Optional<String> validarPropio(Mision mision) {
        return DESTINOS_VALIDOS.contains(mision.destino())
                ? Optional.empty()
                : Optional.of("Destino no válido: " + mision.destino());
    }
}
