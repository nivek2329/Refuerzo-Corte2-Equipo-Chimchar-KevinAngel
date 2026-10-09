package edu.eci.skycampus.enterprise.routing;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class FabricaDronesPorEtapa {
    private final Map<TipoDroneEtapa, FabricaDroneEtapa> fabricas;

    public FabricaDronesPorEtapa(Collection<FabricaDroneEtapa> fabricas) {
        Objects.requireNonNull(fabricas, "fabricas no puede ser null");
        this.fabricas = fabricas.stream().collect(Collectors.toUnmodifiableMap(
                FabricaDroneEtapa::tipoSoportado, fabrica -> fabrica));
    }

    public DroneEtapa crear(EtapaRuta etapa) {
        EtapaRuta etapaValidada = Objects.requireNonNull(etapa, "etapa no puede ser null");
        return obtenerFabrica(etapaValidada.tipoDroneRequerido()).crearPara(etapaValidada);
    }

    private FabricaDroneEtapa obtenerFabrica(TipoDroneEtapa tipo) {
        FabricaDroneEtapa fabrica = fabricas.get(tipo);
        if (fabrica == null) throw new IllegalArgumentException("no hay fábrica para el tipo de drone " + tipo);
        return fabrica;
    }
}
