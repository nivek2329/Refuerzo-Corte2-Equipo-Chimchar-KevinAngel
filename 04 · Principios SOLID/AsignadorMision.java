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

    public void asignar(Drone drone, Mision mision) {
        estrategiaRuta.calcular(mision.origen(), mision.destino());
        repositorio.guardar(mision);
        alerta.enviar(mision.origen(), "Misión " + mision.id() + " asignada al drone " + drone.id());
    }
}
