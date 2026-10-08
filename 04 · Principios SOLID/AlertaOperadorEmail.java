import java.util.Objects;

/**
 * Canal de alerta por correo. Stub del reto 04: el envío SMTP real está fuera del
 * alcance de Chimchar; esta clase muestra dónde vive esa dependencia concreta
 * detrás de la interfaz {@link AlertaOperador}.
 */
public class AlertaOperadorEmail implements AlertaOperador {
    @Override
    public void enviar(String operador, String mensaje) {
        Objects.requireNonNull(operador, "operador no puede ser null");
        Objects.requireNonNull(mensaje, "mensaje no puede ser null");
    }
}
