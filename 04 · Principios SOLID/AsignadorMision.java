import java.util.Objects;

public class AsignadorMision {
    private final RepositorioMision repositorio;
    private final AlertaOperador alerta;
    private final EstrategiaRuta estrategiaRuta;

    public AsignadorMision(RepositorioMision repositorio, AlertaOperador alerta, EstrategiaRuta estrategiaRuta) {
        this.repositorio = Objects.requireNonNull(repositorio, "repositorio no puede ser null");
        this.alerta = Objects.requireNonNull(alerta, "alerta no puede ser null");
        this.estrategiaRuta = Objects.requireNonNull(estrategiaRuta, "estrategiaRuta no puede ser null");
    }

    public void asignar(String operador, Mision mision) {
        Objects.requireNonNull(operador, "operador no puede ser null");
        Objects.requireNonNull(mision, "mision no puede ser null");
        validarAsignacion(mision);
        String ruta = estrategiaRuta.calcular(mision.origen(), mision.destino());
        repositorio.guardar(mision);
        alerta.enviar(operador, "Misión " + mision.id() + " asignada al drone " + mision.drone().id() + ". " + ruta);
    }

    private void validarAsignacion(Mision mision) {
        if (!mision.drone().disponible()) {
            throw new IllegalStateException("El drone " + mision.drone().id() + " no está disponible");
        }
        if (mision.estado() != EstadoMision.PENDIENTE) {
            throw new IllegalStateException("Solo se asignan misiones PENDIENTE; la misión "
                    + mision.id() + " está " + mision.estado());
        }
    }
}
