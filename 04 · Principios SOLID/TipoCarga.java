public enum TipoCarga {
    SOBRE(1),
    CARPETA(2),
    LIBRO(3);

    private final int nivelCapacidad;

    TipoCarga(int nivelCapacidad) {
        this.nivelCapacidad = nivelCapacidad;
    }

    public int nivelCapacidad() {
        return nivelCapacidad;
    }
}
