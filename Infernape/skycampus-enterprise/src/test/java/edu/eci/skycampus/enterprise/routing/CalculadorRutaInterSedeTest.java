package edu.eci.skycampus.enterprise.routing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CalculadorRutaInterSedeTest {
    private final CalculadorRutaInterSede calculador = new CalculadorRutaInterSede();

    @Test
    void vientoDesdeElSurAceleraLaRutaNorteEciUnal() {
        assertEquals(1.5, calculador.tiempoEstimadoHoras(60, 30, 10, 0, 180), 1e-9);
    }

    @Test
    void vientoDesdeElNorteReduceLaVelocidadSobreSuelo() {
        assertEquals(3.0, calculador.tiempoEstimadoHoras(60, 30, 10, 0, 0), 1e-9);
    }

    @Test
    void vientoCruzadoNoCambiaLaComponenteDeAvance() {
        assertEquals(2.0, calculador.tiempoEstimadoHoras(60, 30, 10, 0, 90), 1e-9);
    }

    @Test
    void rechazaVientoQueImpideAvanzar() {
        assertThrows(IllegalArgumentException.class,
                () -> calculador.tiempoEstimadoHoras(60, 30, 30, 0, 0));
    }

    @Test
    void rechazaDireccionesFueraDelRango() {
        assertThrows(IllegalArgumentException.class,
                () -> calculador.tiempoEstimadoHoras(60, 30, 10, 360, 180));
    }
}
