package edu.eci.skycampus.enterprise.routing;

import java.util.Objects;

public abstract class FabricaDroneEtapa {
    public final DroneEtapa crearPara(EtapaRuta etapa) {
        Objects.requireNonNull(etapa, "etapa no puede ser null");
        DroneEtapa drone = crearDrone(etapa);
        validarCapacidad(etapa, drone);
        return drone;
    }

    public abstract TipoDroneEtapa tipoSoportado();

    protected abstract DroneEtapa crearDrone(EtapaRuta etapa);

    private void validarCapacidad(EtapaRuta etapa, DroneEtapa drone) {
        if (etapa.cargaKg() > drone.capacidadCargaKg()) {
            throw new IllegalArgumentException("la carga excede la capacidad del drone de etapa");
        }
    }
}
