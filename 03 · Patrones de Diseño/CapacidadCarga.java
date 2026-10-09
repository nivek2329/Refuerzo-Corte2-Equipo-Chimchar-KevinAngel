public enum CapacidadCarga {
    LIGERA(1),
    MEDIA(2),
    PESADA(3);

    private final int nivel;

    CapacidadCarga(int nivel) {
        this.nivel = nivel;
    }

    public int nivel() {
        return nivel;
    }
}
