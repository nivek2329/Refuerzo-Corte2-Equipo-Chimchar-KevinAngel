import java.util.Optional;

public abstract class ValidadorMision {
    private ValidadorMision siguiente;

    public ValidadorMision siguiente(ValidadorMision siguiente) {
        // Retorna el nodo agregado (no this) para poder encadenar
        // a.siguiente(b).siguiente(c) y construir toda la cadena en una sola expresión.
        this.siguiente = siguiente;
        return siguiente;
    }

    public Optional<String> validar(Mision mision) {
        Optional<String> motivo = validarPropio(mision);
        if (motivo.isPresent()) {
            return motivo;
        }
        return siguiente != null ? siguiente.validar(mision) : Optional.empty();
    }

    protected abstract Optional<String> validarPropio(Mision mision);
}
