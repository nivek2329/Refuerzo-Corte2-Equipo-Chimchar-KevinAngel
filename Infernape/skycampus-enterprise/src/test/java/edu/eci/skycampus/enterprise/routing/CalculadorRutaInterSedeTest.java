package edu.eci.skycampus.enterprise.routing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CalculadorRutaInterSedeTest {
    private static final double TOLERANCIA = 1e-6;
    private final CalculadorRutaInterSede calculador = new CalculadorRutaInterSede();

    @Test
    void vientoDesdeElSurAceleraLaRutaNorteEciUnal() {
        assertEquals(1.5, calculador.tiempoEstimadoHoras(60, 30, 10, 0, 180), TOLERANCIA);
    }

    @Test
    void vientoDesdeElNorteReduceLaVelocidadSobreSuelo() {
        assertEquals(3.0, calculador.tiempoEstimadoHoras(60, 30, 10, 0, 0), TOLERANCIA);
    }

    /** El drone debe corregir la deriva: avanza a sqrt(30^2 - 10^2) = 28,28 km/h, no a 30 km/h. */
    @Test
    void vientoCruzadoObligaACorregirDerivaYAlargaElVuelo() {
        assertEquals(60 / Math.sqrt(800), calculador.tiempoEstimadoHoras(60, 30, 10, 0, 90), TOLERANCIA);
    }

    /** Viento desde 45 grados: 7,07 km/h en contra y 7,07 km/h de costado. */
    @Test
    void vientoDiagonalSumaComponenteEnContraYDeriva() {
        assertEquals(2.7169370464568985, calculador.tiempoEstimadoHoras(60, 30, 10, 0, 45), TOLERANCIA);
    }

    /** Mismo viento cruzado medido cruzando el norte (rumbo 350, viento desde 80). */
    @Test
    void vientoCruzadoAlCruzarElNorteDaElMismoTiempo() {
        assertEquals(60 / Math.sqrt(800), calculador.tiempoEstimadoHoras(60, 30, 10, 350, 80), TOLERANCIA);
    }

    @Test
    void rechazaVientoQueImpideAvanzar() {
        assertThrows(IllegalArgumentException.class,
                () -> calculador.tiempoEstimadoHoras(60, 30, 30, 0, 0));
    }

    @Test
    void rechazaVientoCruzadoQueNoPermiteMantenerElRumbo() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> calculador.tiempoEstimadoHoras(60, 30, 31, 0, 90));
        assertEquals("el viento cruzado impide mantener el rumbo", error.getMessage());
    }

    @Test
    void rechazaDireccionesFueraDelRango() {
        assertThrows(IllegalArgumentException.class,
                () -> calculador.tiempoEstimadoHoras(60, 30, 10, 360, 180));
    }
}
