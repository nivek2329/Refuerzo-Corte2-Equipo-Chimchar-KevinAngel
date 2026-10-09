import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MisionBuilder {
    private static final int PRIORIDAD_POR_DEFECTO = 3;

    private Drone drone;
    private String origen;
    private String destino;
    private TipoCarga tipoCarga;
    private int prioridad = PRIORIDAD_POR_DEFECTO;
    private String notas = "";
    private LocalTime horaMaximaEntrega;

    public MisionBuilder drone(Drone drone) {
        this.drone = Objects.requireNonNull(drone, "drone no puede ser null");
        return this;
    }

    public MisionBuilder origen(String origen) {
        this.origen = Objects.requireNonNull(origen, "origen no puede ser null");
        return this;
    }

    public MisionBuilder destino(String destino) {
        this.destino = Objects.requireNonNull(destino, "destino no puede ser null");
        return this;
    }

    public MisionBuilder tipoCarga(TipoCarga tipoCarga) {
        this.tipoCarga = Objects.requireNonNull(tipoCarga, "tipoCarga no puede ser null");
        return this;
    }

    public MisionBuilder prioridad(int prioridad) {
        this.prioridad = prioridad;
        return this;
    }

    public MisionBuilder notas(String notas) {
        this.notas = Objects.requireNonNull(notas, "notas no puede ser null");
        return this;
    }

    public MisionBuilder horaMaximaEntrega(LocalTime hora) {
        this.horaMaximaEntrega = Objects.requireNonNull(hora, "hora no puede ser null");
        return this;
    }

    public Mision build() {
        List<String> camposFaltantes = new ArrayList<>();
        if (drone == null) {
            camposFaltantes.add("drone");
        }
        if (origen == null || origen.isBlank()) {
            camposFaltantes.add("origen");
        }
        if (destino == null || destino.isBlank()) {
            camposFaltantes.add("destino");
        }
        if (tipoCarga == null) {
            camposFaltantes.add("tipoCarga");
        }
        if (!camposFaltantes.isEmpty()) {
            throw new IllegalStateException("Faltan campos obligatorios: " + String.join(", ", camposFaltantes));
        }
        return new Mision(this, UUID.randomUUID().toString());
    }

    Drone drone() {
        return drone;
    }

    String origen() {
        return origen;
    }

    String destino() {
        return destino;
    }

    TipoCarga tipoCarga() {
        return tipoCarga;
    }

    int prioridad() {
        return prioridad;
    }

    String notas() {
        return notas;
    }

    LocalTime horaMaximaEntrega() {
        return horaMaximaEntrega;
    }
}
