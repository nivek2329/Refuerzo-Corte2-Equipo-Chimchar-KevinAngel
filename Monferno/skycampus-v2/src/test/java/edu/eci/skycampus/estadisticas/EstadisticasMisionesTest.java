package edu.eci.skycampus.estadisticas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.eci.skycampus.modelo.Drone;
import edu.eci.skycampus.modelo.EstadoMision;
import edu.eci.skycampus.modelo.Mision;
import edu.eci.skycampus.modelo.Prioridad;
import edu.eci.skycampus.modelo.TipoDrone;
import edu.eci.skycampus.soporte.Datos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@DisplayName("EstadisticasMisiones")
class EstadisticasMisionesTest {
    private Drone mini;
    private Drone cargo;
    private Drone express;
    private List<Mision> delDia;

    @BeforeEach
    void setUp() {
        mini = Datos.drone("D-01", TipoDrone.MINI, 80);
        cargo = Datos.drone("D-02", TipoDrone.CARGO, 70);
        express = Datos.drone("D-03", TipoDrone.EXPRESS, 90);
        delDia = List.of(
                Datos.mision("1", mini, EstadoMision.ENTREGADA, Prioridad.NORMAL, Datos.AHORA),
                Datos.mision("2", mini, EstadoMision.ENTREGADA, Prioridad.BAJO, Datos.AHORA),
                Datos.mision("3", cargo, EstadoMision.ENTREGADA, Prioridad.NORMAL, Datos.AHORA),
                Datos.mision("4", express, EstadoMision.FALLIDA, Prioridad.URGENTE, Datos.AHORA),
                Datos.mision("5", express, EstadoMision.EN_VUELO, Prioridad.NORMAL, Datos.AHORA));
    }

    @Test
    @DisplayName("cuenta solo las misiones ENTREGADA por tipo de drone")
    void completadasPorTipo_misionesDelDia_cuentaEntregadasPorTipo() {
        // Act
        Map<TipoDrone, Long> porTipo = EstadisticasMisiones.completadasPorTipo(delDia);

        // Assert
        assertEquals(Map.of(TipoDrone.MINI, 2L, TipoDrone.CARGO, 1L), porTipo);
    }

    @Test
    @DisplayName("sin misiones devuelve un mapa vacío")
    void completadasPorTipo_listaVacia_mapaVacio() {
        // Act / Assert
        assertTrue(EstadisticasMisiones.completadasPorTipo(List.of()).isEmpty());
    }

    @Test
    @DisplayName("el drone con más entregas es D-01")
    void droneConMasCompletadas_misionesDelDia_retornaD01() {
        // Act
        Optional<String> lider = EstadisticasMisiones.droneConMasCompletadas(delDia);

        // Assert
        assertEquals(Optional.of("D-01"), lider);
    }

    @Test
    @DisplayName("en empate gana el ID menor")
    void droneConMasCompletadas_empate_ganaIdMenor() {
        // Arrange
        List<Mision> empate = List.of(
                Datos.mision("1", express, EstadoMision.ENTREGADA, Prioridad.NORMAL, Datos.AHORA),
                Datos.mision("2", cargo, EstadoMision.ENTREGADA, Prioridad.NORMAL, Datos.AHORA));

        // Act
        Optional<String> lider = EstadisticasMisiones.droneConMasCompletadas(empate);

        // Assert
        assertEquals(Optional.of("D-02"), lider);
    }

    @Test
    @DisplayName("sin entregas no hay drone líder")
    void droneConMasCompletadas_sinEntregas_vacio() {
        // Arrange
        List<Mision> sinEntregas = List.of(
                Datos.mision("1", mini, EstadoMision.FALLIDA, Prioridad.NORMAL, Datos.AHORA));

        // Act / Assert
        assertTrue(EstadisticasMisiones.droneConMasCompletadas(sinEntregas).isEmpty());
    }

    @Test
    @DisplayName("1 fallida de 5 es el 20 %")
    void porcentajeFallidas_unaDeCinco_veintePorCiento() {
        // Act
        double porcentaje = EstadisticasMisiones.porcentajeFallidas(delDia);

        // Assert
        assertEquals(20.0, porcentaje, 0.0001);
    }

    @Test
    @DisplayName("sin misiones el porcentaje es 0")
    void porcentajeFallidas_listaVacia_cero() {
        // Act / Assert
        assertEquals(0.0, EstadisticasMisiones.porcentajeFallidas(List.of()), 0.0001);
    }

    @Test
    @DisplayName("detecta una URGENTE PENDIENTE creada hace 11 minutos")
    void hasUrgentePendienteDemorada_onceMinutos_true() {
        // Arrange
        List<Mision> misiones = List.of(Datos.mision("1", express, EstadoMision.PENDIENTE, Prioridad.URGENTE,
                Datos.AHORA.minusMinutes(11)));

        // Act / Assert
        assertTrue(EstadisticasMisiones.hasUrgentePendienteDemorada(misiones, Datos.AHORA));
    }

    @Test
    @DisplayName("exactamente 10 minutos todavía no cuenta como demorada")
    void hasUrgentePendienteDemorada_diezMinutosExactos_false() {
        // Arrange
        List<Mision> misiones = List.of(Datos.mision("1", express, EstadoMision.PENDIENTE, Prioridad.URGENTE,
                Datos.AHORA.minusMinutes(10)));

        // Act / Assert
        assertFalse(EstadisticasMisiones.hasUrgentePendienteDemorada(misiones, Datos.AHORA));
    }

    @Test
    @DisplayName("una NORMAL pendiente hace 30 minutos no cuenta")
    void hasUrgentePendienteDemorada_noUrgente_false() {
        // Arrange
        List<Mision> misiones = List.of(Datos.mision("1", mini, EstadoMision.PENDIENTE, Prioridad.NORMAL,
                Datos.AHORA.minusMinutes(30)));

        // Act / Assert
        assertFalse(EstadisticasMisiones.hasUrgentePendienteDemorada(misiones, Datos.AHORA));
    }

    @Test
    @DisplayName("una URGENTE ya en vuelo no cuenta aunque sea antigua")
    void hasUrgentePendienteDemorada_urgenteEnVuelo_false() {
        // Arrange
        List<Mision> misiones = List.of(Datos.mision("1", express, EstadoMision.EN_VUELO, Prioridad.URGENTE,
                Datos.AHORA.minusMinutes(30)));

        // Act / Assert
        assertFalse(EstadisticasMisiones.hasUrgentePendienteDemorada(misiones, Datos.AHORA));
    }

    @Test
    @DisplayName("rechaza una lista null con mensaje claro")
    void completadasPorTipo_null_lanzaExcepcion() {
        // Act
        NullPointerException error = assertThrows(NullPointerException.class,
                () -> EstadisticasMisiones.completadasPorTipo(null));

        // Assert
        assertEquals("misiones no puede ser null", error.getMessage());
    }
}
