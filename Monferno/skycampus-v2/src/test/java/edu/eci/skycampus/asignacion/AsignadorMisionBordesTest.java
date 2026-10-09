package edu.eci.skycampus.asignacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import edu.eci.skycampus.externo.ApiMeteorologica;
import edu.eci.skycampus.modelo.Drone;
import edu.eci.skycampus.modelo.EstadoDrone;
import edu.eci.skycampus.modelo.Prioridad;
import edu.eci.skycampus.modelo.SolicitudReparto;
import edu.eci.skycampus.modelo.TipoDrone;
import edu.eci.skycampus.notificacion.GestorFlota;
import edu.eci.skycampus.soporte.Datos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("AsignadorMision: casos borde")
class AsignadorMisionBordesTest {
    @Mock
    private ApiMeteorologica clima;

    private AsignadorMision asignador;
    private List<Drone> flota;

    @BeforeEach
    void setUp() {
        asignador = new AsignadorMision(clima, new GestorFlota(), PoliticaAsignacion.porDefecto());
        flota = List.of(
                Datos.drone("D-01", TipoDrone.MINI, 91),
                Datos.drone("D-02", TipoDrone.EXPRESS, 60),
                Datos.drone("D-03", TipoDrone.CARGO, 85));
    }

    @Test
    @DisplayName("un paquete de exactamente 2000 g sí se asigna (al CARGO): el límite es inclusivo")
    void asignar_pesoIgualAlMaximo_asignaCargo() {
        // Arrange
        when(clima.esApto()).thenReturn(true);
        SolicitudReparto limite = Datos.solicitud("S-6", 2000, Prioridad.NORMAL);

        // Act
        Optional<Drone> asignado = asignador.asignar(flota, limite);

        // Assert
        assertEquals("D-03", asignado.orElseThrow().id());
    }

    @Test
    @DisplayName("2001 g, el primer peso inválido después del límite, se rechaza sin consultar el clima")
    void asignar_pesoUnGramoSobreElMaximo_lanzaExcepcion() {
        // Arrange
        SolicitudReparto sobreLimite = Datos.solicitud("S-7", 2001, Prioridad.NORMAL);

        // Act
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> asignador.asignar(flota, sobreLimite));

        // Assert
        assertEquals("el paquete pesa 2001 g y supera la capacidad del drone más grande (2000 g)", error.getMessage());
        verifyNoInteractions(clima);
    }

    @Test
    @DisplayName("URGENTE sin EXPRESS apto usa el siguiente más rápido (MINI antes que CARGO)")
    void asignar_urgenteSinExpress_asignaElSiguienteMasRapido() {
        // Arrange
        when(clima.esApto()).thenReturn(true);
        List<Drone> sinExpress = List.of(
                Datos.drone("D-03", TipoDrone.CARGO, 99),
                Datos.drone("D-01", TipoDrone.MINI, 50));
        SolicitudReparto urgente = Datos.solicitud("S-8", 300, Prioridad.URGENTE);

        // Act
        Optional<Drone> asignado = asignador.asignar(sinExpress, urgente);

        // Assert
        assertEquals("D-01", asignado.orElseThrow().id());
    }

    @Test
    @DisplayName("URGENTE no vuela con un EXPRESS bajo el 30 %: usa el siguiente más rápido que sí es apto (RF-08)")
    void asignar_urgenteConExpressBajoMinimo_asignaElSiguienteMasRapidoApto() {
        // Arrange
        when(clima.esApto()).thenReturn(true);
        List<Drone> expressDescargado = List.of(
                Datos.drone("D-02", TipoDrone.EXPRESS, 25),
                Datos.drone("D-03", TipoDrone.CARGO, 99),
                Datos.drone("D-01", TipoDrone.MINI, 50));
        SolicitudReparto urgente = Datos.solicitud("S-12", 300, Prioridad.URGENTE);

        // Act
        Optional<Drone> asignado = asignador.asignar(expressDescargado, urgente);

        // Assert
        assertEquals("D-01", asignado.orElseThrow().id());
    }

    @Test
    @DisplayName("una misión BAJO usa la estrategia normal (mayor batería), no la urgente")
    void asignar_misionBaja_usaEstrategiaNormal() {
        // Arrange
        when(clima.esApto()).thenReturn(true);
        List<Drone> conUsosDistintos = List.of(
                Datos.drone("D-01", TipoDrone.MINI, 91, EstadoDrone.DISPONIBLE, 300),
                Datos.drone("D-03", TipoDrone.CARGO, 85, EstadoDrone.DISPONIBLE, 5));
        SolicitudReparto baja = Datos.solicitud("S-9", 300, Prioridad.BAJO);

        // Act
        Optional<Drone> asignado = asignador.asignar(conUsosDistintos, baja);

        // Assert
        assertEquals("D-01", asignado.orElseThrow().id());
    }

    @Test
    @DisplayName("OCP: dar a BAJO su propia estrategia es solo configuración, AsignadorMision no cambia")
    void asignar_bajoConEstrategiaPropia_usaLaConfigurada() {
        // Arrange
        when(clima.esApto()).thenReturn(true);
        Map<Prioridad, EstrategiaAsignacion> estrategias = new EnumMap<>(PoliticaAsignacion.porDefecto());
        estrategias.put(Prioridad.BAJO, new AsignacionMenorUso());
        AsignadorMision conBajoPropio = new AsignadorMision(clima, new GestorFlota(), estrategias);
        List<Drone> usados = List.of(
                Datos.drone("D-01", TipoDrone.MINI, 91, EstadoDrone.DISPONIBLE, 300),
                Datos.drone("D-03", TipoDrone.CARGO, 85, EstadoDrone.DISPONIBLE, 5));
        SolicitudReparto baja = Datos.solicitud("S-11", 300, Prioridad.BAJO);

        // Act
        Optional<Drone> asignado = conBajoPropio.asignar(usados, baja);

        // Assert
        assertEquals("D-03", asignado.orElseThrow().id());
    }

    @Test
    @DisplayName("con la flota vacía y clima apto no asigna")
    void asignar_flotaVacia_vacio() {
        // Arrange
        when(clima.esApto()).thenReturn(true);
        SolicitudReparto normal = Datos.solicitud("S-10", 200, Prioridad.NORMAL);

        // Act
        Optional<Drone> asignado = asignador.asignar(List.of(), normal);

        // Assert
        assertTrue(asignado.isEmpty());
    }
}
