package edu.eci.skycampus.asignacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.eci.skycampus.modelo.Drone;
import edu.eci.skycampus.modelo.EstadoDrone;
import edu.eci.skycampus.modelo.Paquete;
import edu.eci.skycampus.modelo.Prioridad;
import edu.eci.skycampus.modelo.TipoDrone;
import edu.eci.skycampus.soporte.Datos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

@DisplayName("Estrategias de asignación")
class EstrategiasAsignacionTest {
    private List<Drone> flota;
    private Paquete paqueteLiviano;

    @BeforeEach
    void setUp() {
        flota = List.of(
                Datos.drone("D-01", TipoDrone.MINI, 60, EstadoDrone.DISPONIBLE, 300),
                Datos.drone("D-02", TipoDrone.CARGO, 95, EstadoDrone.DISPONIBLE, 900),
                Datos.drone("D-03", TipoDrone.EXPRESS, 75, EstadoDrone.DISPONIBLE, 50),
                Datos.drone("D-04", TipoDrone.MINI, 99, EstadoDrone.EN_VUELO, 10),
                Datos.drone("D-05", TipoDrone.MINI, 29, EstadoDrone.DISPONIBLE, 0));
        paqueteLiviano = Datos.paquete(300, Prioridad.NORMAL);
    }

    @Nested
    @DisplayName("CriterioAptitud")
    class CriterioAptitudTests {
        @Test
        @DisplayName("descarta drones en vuelo, con batería < 30 % o incompatibles con el peso")
        void aptos_flotaMixta_soloLosQueCumplenTodo() {
            // Act
            List<String> aptos = CriterioAptitud.aptos(flota, paqueteLiviano).map(Drone::id).toList();

            // Assert
            assertEquals(List.of("D-01", "D-02", "D-03"), aptos);
        }

        @Test
        @DisplayName("acepta batería exactamente en el mínimo de 30 %")
        void isApto_bateriaEnElMinimo_true() {
            // Act / Assert
            assertTrue(CriterioAptitud.isApto(Datos.drone("D-09", TipoDrone.MINI, 30), paqueteLiviano));
        }

        @Test
        @DisplayName("rechaza una flota null con mensaje claro")
        void aptos_flotaNula_lanzaExcepcion() {
            // Act
            NullPointerException error = assertThrows(NullPointerException.class,
                    () -> CriterioAptitud.aptos(null, paqueteLiviano));

            // Assert
            assertEquals("flota no puede ser null", error.getMessage());
        }
    }

    @Test
    @DisplayName("mayor batería elige D-02 (95 %) entre los aptos")
    void mayorBateria_flotaMixta_eligeD02() {
        // Act
        Optional<Drone> elegido = new AsignacionMayorBateria().seleccionar(flota, paqueteLiviano);

        // Assert
        assertEquals("D-02", elegido.orElseThrow().id());
    }

    @Test
    @DisplayName("menor uso elige D-03 (50 min de vuelo)")
    void menorUso_flotaMixta_eligeD03() {
        // Act
        Optional<Drone> elegido = new AsignacionMenorUso().seleccionar(flota, paqueteLiviano);

        // Assert
        assertEquals("D-03", elegido.orElseThrow().id());
    }

    @Test
    @DisplayName("tipo compatible usa el MINI para 300 g en lugar del CARGO")
    void tipoCompatible_paqueteLiviano_eligeMini() {
        // Act
        Optional<Drone> elegido = new AsignacionTipoCompatible().seleccionar(flota, paqueteLiviano);

        // Assert
        assertEquals("D-01", elegido.orElseThrow().id());
    }

    @Test
    @DisplayName("tipo compatible usa el EXPRESS para 700 g (no cabe en MINI)")
    void tipoCompatible_paqueteMediano_eligeExpress() {
        // Arrange
        Paquete mediano = Datos.paquete(700, Prioridad.NORMAL);

        // Act
        Optional<Drone> elegido = new AsignacionTipoCompatible().seleccionar(flota, mediano);

        // Assert
        assertEquals("D-03", elegido.orElseThrow().id());
    }

    @Test
    @DisplayName("tipo compatible desempata por mayor batería dentro del mismo tipo")
    void tipoCompatible_dosMiniAptos_eligeMayorBateria() {
        // Arrange
        List<Drone> dosMini = List.of(Datos.drone("D-10", TipoDrone.MINI, 40), Datos.drone("D-11", TipoDrone.MINI, 85));

        // Act
        Optional<Drone> elegido = new AsignacionTipoCompatible().seleccionar(dosMini, paqueteLiviano);

        // Assert
        assertEquals("D-11", elegido.orElseThrow().id());
    }

    @Test
    @DisplayName("sin drones aptos ninguna estrategia elige")
    void seleccionar_sinAptos_vacio() {
        // Arrange
        Paquete muyPesado = Datos.paquete(2500, Prioridad.NORMAL);

        // Act / Assert
        assertTrue(new AsignacionMayorBateria().seleccionar(flota, muyPesado).isEmpty());
        assertTrue(new AsignacionMenorUso().seleccionar(flota, muyPesado).isEmpty());
        assertTrue(new AsignacionTipoCompatible().seleccionar(flota, muyPesado).isEmpty());
    }
}
