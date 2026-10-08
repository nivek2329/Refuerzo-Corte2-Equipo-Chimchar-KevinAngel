/** Punto único donde se arma la cadena en el orden del enunciado: batería → destino → carga. */
public final class CadenaValidacionMision {

    private CadenaValidacionMision() {
    }

    public static ValidadorMision crear() {
        ValidadorMision primero = new ValidadorBateria();
        primero.siguiente(new ValidadorDestino())
                .siguiente(new ValidadorCarga());
        return primero;
    }
}
