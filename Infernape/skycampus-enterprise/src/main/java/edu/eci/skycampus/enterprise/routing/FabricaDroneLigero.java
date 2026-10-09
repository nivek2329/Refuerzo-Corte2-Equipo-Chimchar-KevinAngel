package edu.eci.skycampus.enterprise.routing;

public final class FabricaDroneLigero extends FabricaDroneEtapa {
    @Override
    public TipoDroneEtapa tipoSoportado() {
        return TipoDroneEtapa.LIGERO;
    }

    @Override
    protected DroneEtapa crearDrone(EtapaRuta etapa) {
        TipoDroneEtapa tipo = tipoSoportado();
        return new DroneEtapa("DR-L-" + etapa.id(), tipo, tipo.capacidadCargaKg());
    }
}
