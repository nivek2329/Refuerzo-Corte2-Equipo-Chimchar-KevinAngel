public enum TipoCarga {
    SOBRE(CapacidadCarga.LIGERA),
    CARPETA(CapacidadCarga.MEDIA),
    LIBRO(CapacidadCarga.PESADA);

    private final CapacidadCarga capacidadRequerida;

    TipoCarga(CapacidadCarga capacidadRequerida) {
        this.capacidadRequerida = capacidadRequerida;
    }

    public CapacidadCarga capacidadRequerida() {
        return capacidadRequerida;
    }
}
