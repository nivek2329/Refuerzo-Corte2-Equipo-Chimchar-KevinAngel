package edu.eci.skycampus.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.eci.skycampus.soporte.Datos;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("Ciclo de estados del drone")
class EstadoDroneTest {

    @ParameterizedTest(name = "{0} → {1}: permitido = {2}")
    @CsvSource({
            "DISPONIBLE,EN_VUELO,true", "DISPONIBLE,ATERRIZANDO,false",
            "EN_VUELO,ATERRIZANDO,true", "EN_VUELO,DISPONIBLE,false",
            "ATERRIZANDO,DISPONIBLE,true", "ATERRIZANDO,EN_VUELO,false",
            "EN_CARGA,DISPONIBLE,true", "EN_CARGA,EN_VUELO,false",
            "FALLO,MANTENIMIENTO,true", "FALLO,EN_VUELO,false",
            "MANTENIMIENTO,DISPONIBLE,true", "MANTENIMIENTO,FALLO,false"})
    void puedePasarA_cadaEstado_respetaElCiclo(EstadoDrone origen, EstadoDrone destino, boolean esperado) {
        // Arrange: origen y destino por parámetro

        // Act
        boolean permitido = origen.puedePasarA(destino);

        // Assert
        assertEquals(esperado, permitido);
    }

    @Test
    @DisplayName("transicionarA devuelve una copia en el nuevo estado y no altera el original")
    void transicionarA_permitida_devuelveCopia() {
        // Arrange
        Drone original = Datos.drone("D-01", TipoDrone.MINI, 80);

        // Act
        Drone enVuelo = original.transicionarA(EstadoDrone.EN_VUELO);

        // Assert
        assertEquals(EstadoDrone.EN_VUELO, enVuelo.estado());
        assertTrue(original.isDisponible());
    }

    @Test
    @DisplayName("un drone en FALLO no puede volver a volar sin pasar por mantenimiento")
    void transicionarA_falloAEnVuelo_lanzaExcepcion() {
        // Arrange
        Drone enFallo = Datos.drone("D-07", TipoDrone.EXPRESS, 80, EstadoDrone.FALLO, 0);

        // Act
        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> enFallo.transicionarA(EstadoDrone.EN_VUELO));

        // Assert
        assertEquals("transición no permitida para D-07: FALLO → EN_VUELO", error.getMessage());
    }

    @Test
    @DisplayName("transicionarA rechaza un estado null")
    void transicionarA_null_lanzaExcepcion() {
        // Arrange
        Drone drone = Datos.drone("D-01", TipoDrone.MINI, 80);

        // Act
        NullPointerException error = assertThrows(NullPointerException.class, () -> drone.transicionarA(null));

        // Assert
        assertEquals("nuevoEstado no puede ser null", error.getMessage());
    }
}
