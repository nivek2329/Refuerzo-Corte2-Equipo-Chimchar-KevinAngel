package edu.eci.skycampus.enterprise.infrastructure.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import edu.eci.skycampus.enterprise.domain.Drone;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RespuestaMisionDto(String misionId, String estado, DroneAsignadoDto droneAsignado,
                                 String motivo, String mensaje) {

    public record DroneAsignadoDto(String id, int bateria, boolean express) {
        static DroneAsignadoDto de(Drone drone) {
            return new DroneAsignadoDto(drone.id(), drone.bateria(), drone.express());
        }
    }
}
