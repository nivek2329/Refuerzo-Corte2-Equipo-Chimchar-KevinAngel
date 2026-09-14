import java.util.UUID;

public class MisionBuilder {
    private Drone drone;
    private String origen;
    private String destino;
    private TipoCarga tipoCarga = TipoCarga.SOBRE;
    private EstadoMision estado = EstadoMision.PENDIENTE;
    private int prioridad = 3;
    private String notas = "";
    private String horaMaximaEntrega;

    public MisionBuilder drone(Drone drone) { this.drone = drone; return this; }
    public MisionBuilder origen(String origen) { this.origen = origen; return this; }
    public MisionBuilder destino(String destino) { this.destino = destino; return this; }
    public MisionBuilder tipoCarga(TipoCarga tipoCarga) { this.tipoCarga = tipoCarga; return this; }
    public MisionBuilder prioridad(int prioridad) { this.prioridad = prioridad; return this; }
    public MisionBuilder notas(String notas) { this.notas = notas; return this; }
    public MisionBuilder horaMaximaEntrega(String hora) { this.horaMaximaEntrega = hora; return this; }

    public Mision build() {
        if (drone == null || origen == null || origen.isBlank() || destino == null || destino.isBlank()) {
            throw new IllegalStateException("drone, origen y destino son obligatorios");
        }
        String id = UUID.randomUUID().toString();
        return new Mision(id, drone, origen, destino, tipoCarga, estado, prioridad, notas, horaMaximaEntrega);
    }
}
