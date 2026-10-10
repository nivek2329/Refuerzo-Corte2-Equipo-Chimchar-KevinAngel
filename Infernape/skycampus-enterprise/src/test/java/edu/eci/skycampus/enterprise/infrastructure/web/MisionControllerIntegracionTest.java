package edu.eci.skycampus.enterprise.infrastructure.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edu.eci.skycampus.enterprise.domain.ServicioAerocivil;
import edu.eci.skycampus.enterprise.domain.ServicioClima;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

/**
 * Capa de integración del reto 12: endpoint REST + Spring + JPA sobre H2 en memoria.
 * Los dos sistemas externos (clima y Aerocivil) son @MockBean; la flota y las sedes son reales en H2.
 * Cada prueba corre en una transacción que se revierte, así la flota sembrada no cambia entre pruebas.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MisionControllerIntegracionTest {
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;
    @MockBean ServicioClima climaMock;
    @MockBean ServicioAerocivil aerocivilMock;

    @BeforeEach
    void climaAptoYRutaAutorizada() {
        when(climaMock.condicionesAptas(any(), any())).thenReturn(true);
        when(aerocivilMock.autorizaRuta(any(), any())).thenReturn(true);
    }

    @Test
    void crearMision_climaApto_retornaCreatedConDroneAsignado() throws Exception {
        crear("ECI", "UNAL", 300, "NORMAL")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.droneAsignado.id").value("D-ECI-01"))
                .andExpect(jsonPath("$.estado").value("EN_VUELO"))
                .andExpect(jsonPath("$.misionId").exists());
    }

    @Test
    void crearMision_urgente_asignaElExpress() throws Exception {
        crear("ECI", "UNIANDES", 300, "URGENTE")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.droneAsignado.id").value("D-ECI-02"))
                .andExpect(jsonPath("$.droneAsignado.express").value(true));
    }

    @Test
    void flujoAlterno_climaAdverso_retorna422() throws Exception {
        when(climaMock.condicionesAptas(any(), any())).thenReturn(false);

        rechazo(crear("ECI", "UNAL", 300, "NORMAL"), "CLIMA_ADVERSO");
    }

    @Test
    void flujoAlterno_aerocivilRechaza_retorna422() throws Exception {
        when(aerocivilMock.autorizaRuta("ECI", "EAFIT")).thenReturn(false);

        rechazo(crear("ECI", "EAFIT", 300, "NORMAL"), "AEROCIVIL_RECHAZA");
    }

    @Test
    void flujoAlterno_sinDronesDisponibles_retorna422() throws Exception {
        rechazo(crear("UNIANDES", "ECI", 300, "NORMAL"), "SIN_DRONES_DISPONIBLES");
    }

    @Test
    void flujoAlterno_paqueteDemasiadoPesado_retorna422() throws Exception {
        rechazo(crear("ECI", "UNAL", 20_000, "NORMAL"), "PAQUETE_DEMASIADO_PESADO");
    }

    @Test
    void flujoAlterno_sedeInactiva_retorna422() throws Exception {
        jdbc.update("UPDATE sede SET activa = FALSE WHERE codigo = 'EAFIT'");

        rechazo(crear("EAFIT", "ECI", 300, "NORMAL"), "SEDE_INACTIVA");
    }

    @Test
    void sedeDesconocida_retorna404() throws Exception {
        crear("UPB", "ECI", 300, "NORMAL").andExpect(status().isNotFound());
    }

    @Test
    void cuerpoInvalido_retorna400() throws Exception {
        mvc.perform(post("/api/v3/misiones").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sede\":\"ECI\",\"destino\":\"\",\"pesoPaquete\":-5,\"prioridad\":\"NORMAL\"}"))
                .andExpect(status().isBadRequest());
    }

    private ResultActions crear(String sede, String destino, int gramos, String prioridad) throws Exception {
        return mvc.perform(post("/api/v3/misiones").contentType(MediaType.APPLICATION_JSON).content(
                "{\"sede\":\"%s\",\"destino\":\"%s\",\"pesoPaquete\":%d,\"prioridad\":\"%s\"}"
                        .formatted(sede, destino, gramos, prioridad)));
    }

    private static void rechazo(ResultActions resultado, String motivo) throws Exception {
        resultado.andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.estado").value("RECHAZADA"))
                .andExpect(jsonPath("$.motivo").value(motivo))
                .andExpect(jsonPath("$.mensaje").exists())
                .andExpect(jsonPath("$.droneAsignado").doesNotExist());
    }
}
