package edu.eci.skycampus.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.eci.skycampus.soporte.Datos;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Modelo SkyCampus v2")
class ModeloTest {

    @ParameterizedTest(name = "{0} con {1} g → compatible: {2}")
    @CsvSource({"MINI,500,true", "MINI,501,false", "EXPRESS,800,true", "EXPRESS,801,false",
            "CARGO,99,false", "CARGO,100,true", "CARGO,2000,true", "CARGO,2001,false"})
    void esCompatibleCon_limitesDeCadaTipo_respetaRango(TipoDrone tipo, int peso, boolean esperado) {
        // Arrange: tipo y peso por parámetro

        // Act
        boolean compatible = tipo.esCompatibleCon(peso);

        // Assert
        assertEquals(esperado, compatible);
    }

    @Test
    @DisplayName("un drone solo está disponible en estado DISPONIBLE")
    void isDisponible_estadoEnVuelo_retornaFalse() {
        // Arrange
        Drone drone = Datos.drone("D-01", TipoDrone.MINI, 80, EstadoDrone.EN_VUELO, 0);

        // Act
        boolean disponible = drone.isDisponible();

        // Assert
        assertFalse(disponible);
    }

    @ParameterizedTest(name = "rechaza batería {0}")
    @ValueSource(ints = {-1, 101})
    void crearDrone_bateriaFueraDeRango_lanzaExcepcion(int bateria) {
        // Arrange: batería por parámetro

        // Act
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> Datos.drone("D-01", TipoDrone.MINI, bateria));

        // Assert
        assertEquals("batería debe estar entre 0 y 100", error.getMessage());
    }

    @Test
    @DisplayName("rechaza minutos de vuelo negativos")
    void crearDrone_minutosNegativos_lanzaExcepcion() {
        // Act
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> Datos.drone("D-01", TipoDrone.MINI, 50, EstadoDrone.DISPONIBLE, -1));

        // Assert
        assertEquals("minutosVueloAcumulados no puede ser negativo", error.getMessage());
    }

    @Test
    @DisplayName("rechaza paquetes sin peso")
    void crearPaquete_pesoCero_lanzaExcepcion() {
        // Act
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> new Paquete(0, TipoCarga.SOBRE, Prioridad.NORMAL));

        // Assert
        assertEquals("pesoGramos debe ser mayor que 0", error.getMessage());
    }

    @Test
    @DisplayName("rechaza solicitudes con origen igual al destino")
    void crearSolicitud_origenIgualDestino_lanzaExcepcion() {
        // Arrange
        Paquete paquete = Datos.paquete(200, Prioridad.NORMAL);

        // Act
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> new SolicitudReparto("S-1", Destino.BLOQUE_A, Destino.BLOQUE_A, paquete));

        // Assert
        assertEquals("origen y destino deben ser distintos", error.getMessage());
    }

    @Test
    @DisplayName("la misión expone la prioridad y el peso de su paquete")
    void mision_datosDelPaquete_seExponen() {
        // Arrange
        Mision mision = new Mision("M-1", Datos.drone("D-01", TipoDrone.MINI, 80),
                Datos.solicitud("S-1", 300, Prioridad.URGENTE), EstadoMision.PENDIENTE, Datos.AHORA);

        // Act / Assert
        assertEquals(Prioridad.URGENTE, mision.prioridad());
        assertEquals(300, mision.pesoPaqueteGramos());
    }
}
