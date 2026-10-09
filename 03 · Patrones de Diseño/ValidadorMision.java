import java.util.Objects;
import java.util.Optional;

public abstract class ValidadorMision {
    private ValidadorMision siguiente;

    /** Enlaza el siguiente validador y lo retorna para construir la cadena de forma fluida. */
    public ValidadorMision siguiente(ValidadorMision siguiente) {
        Objects.requireNonNull(siguiente, "siguiente no puede ser null");
        if (siguiente == this) {
            throw new IllegalArgumentException("Un validador no puede enlazarse consigo mismo");
        }
        if (this.siguiente != null) {
            throw new IllegalStateException("Este validador ya tiene un siguiente enlazado");
        }
        this.siguiente = siguiente;
        return siguiente;
    }

    public Optional<String> validar(Mision mision) {
        Objects.requireNonNull(mision, "mision no puede ser null");
        Optional<String> motivo = validarPropio(mision);
        if (motivo.isPresent()) {
            return motivo;
        }
        return siguiente != null ? siguiente.validar(mision) : Optional.empty();
    }

    protected abstract Optional<String> validarPropio(Mision mision);
}
