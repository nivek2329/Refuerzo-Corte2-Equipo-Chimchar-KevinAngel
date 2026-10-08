import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;

/**
 * Misión de reparto. Solo se crea desde {@link MisionBuilder} (constructor de paquete),
 * pero el propio constructor protege sus invariantes.
 */
public final class Mision {
    public static final int PRIORIDAD_MINIMA = 1;
    public static final int PRIORIDAD_MAXIMA = 5;

    private final String id;
    private final Drone drone;
    private final String origen;
    private final String destino;
    private final TipoCarga tipoCarga;
    private final EstadoMision estado;
    private final int prioridad;
    private final String notas;
    private final LocalTime horaMaximaEntrega;

    Mision(MisionBuilder builder, String id) {
        this.id = textoObligatorio(id, "id");
        this.drone = Objects.requireNonNull(builder.drone(), "drone no puede ser null");
        this.origen = textoObligatorio(builder.origen(), "origen");
        this.destino = textoObligatorio(builder.destino(), "destino");
        this.tipoCarga = Objects.requireNonNull(builder.tipoCarga(), "tipoCarga no puede ser null");
        this.estado = EstadoMision.PENDIENTE;
        this.prioridad = prioridadEnRango(builder.prioridad());
        this.notas = Objects.requireNonNull(builder.notas(), "notas no puede ser null");
        this.horaMaximaEntrega = builder.horaMaximaEntrega();
    }

    private static String textoObligatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El campo " + campo + " no puede estar vacío");
        }
        return valor;
    }

    private static int prioridadEnRango(int prioridad) {
        if (prioridad < PRIORIDAD_MINIMA || prioridad > PRIORIDAD_MAXIMA) {
            throw new IllegalArgumentException(
                    "La prioridad debe estar entre " + PRIORIDAD_MINIMA + " y " + PRIORIDAD_MAXIMA);
        }
        return prioridad;
    }

    public String id() {
        return id;
    }

    public Drone drone() {
        return drone;
    }

    public String origen() {
        return origen;
    }

    public String destino() {
        return destino;
    }

    public TipoCarga tipoCarga() {
        return tipoCarga;
    }

    public EstadoMision estado() {
        return estado;
    }

    public int prioridad() {
        return prioridad;
    }

    public String notas() {
        return notas;
    }

    public Optional<LocalTime> horaMaximaEntrega() {
        return Optional.ofNullable(horaMaximaEntrega);
    }
}
