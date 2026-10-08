import java.util.List;

public class FlotaDronesApp {
    private static final String UBICACION_CONSULTADA = "Bloque C";

    public static void main(String[] args) {
        List<Drone> drones = List.of(
                new Drone("D-01", "DJI Mini 3", 85, true, "Bloque A"),
                new Drone("D-02", "DJI Mini 3", 42, false, "Biblioteca"),
                new Drone("D-03", "DJI Mini 3", 91, true, "Bloque C"),
                new Drone("D-04", "DJI Mini 3", 18, true, "Bloque B"),
                new Drone("D-05", "DJI Mini 3", 67, true, "Bloque D")
        );

        System.out.println("1. Disponibles con batería >=" + ConsultasFlota.BATERIA_SUFICIENTE
                + "%, de mayor a menor:");
        System.out.println("   " + ConsultasFlota.idsDisponiblesConBateriaSuficiente(drones));

        System.out.println("2. ¿Drone disponible en " + UBICACION_CONSULTADA + "?: "
                + ConsultasFlota.hasDroneDisponibleEn(drones, UBICACION_CONSULTADA));

        System.out.println("3. Drones con batería crítica (<" + ConsultasFlota.BATERIA_CRITICA + "%): "
                + ConsultasFlota.contarBateriaCritica(drones));

        System.out.println("4. ID y batería de todos los drones:");
        System.out.println("   " + ConsultasFlota.listarIdYBateria(drones));
    }
}
