package edu.eci.skycampus.enterprise.routing;

public final class FabricaDroneCarga extends FabricaDroneEtapa {
    @Override
    public TipoDroneEtapa tipoSoportado() {
        return TipoDroneEtapa.CARGA;
    }

    @Override
    protected DroneEtapa crearDrone(EtapaRuta etapa) {
        TipoDroneEtapa tipo = tipoSoportado();
        return new DroneEtapa("DR-C-" + etapa.id(), tipo, tipo.capacidadCargaKg());
    }
}
