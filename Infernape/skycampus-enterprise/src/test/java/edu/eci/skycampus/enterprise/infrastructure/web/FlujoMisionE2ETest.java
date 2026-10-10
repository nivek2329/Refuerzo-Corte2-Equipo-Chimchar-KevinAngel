package edu.eci.skycampus.enterprise.infrastructure.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.skycampus.enterprise.infrastructure.config.RegistroNotificaciones;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Punta de la pirámide (E2E simulado): RF-10/RF-11 completo, sin mocks. Endpoint → caso de uso → JPA → H2,
 * con los adaptadores locales de clima y Aerocivil que usa la aplicación.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class FlujoMisionE2ETest {
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;
    @Autowired ObjectMapper json;
    @Autowired RegistroNotificaciones notificaciones;

    @Test
    void crearMision_persisteLaAsignacion_sacaElDroneDeLaFlotaYNotifica() throws Exception {
        mvc.perform(get("/api/v3/flota/UNAL/disponibles"))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"D-UNAL-01\"]"));

        String cuerpo = mvc.perform(post("/api/v3/misiones").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sede\":\"UNAL\",\"destino\":\"ECI\",\"pesoPaquete\":800,\"prioridad\":\"URGENTE\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        JsonNode respuesta = json.readTree(cuerpo);
        String misionId = respuesta.get("misionId").asText();

        assertEquals("D-UNAL-01", jdbc.queryForObject(
                "SELECT drone_id FROM asignacion WHERE mision_id = ?", String.class, misionId));
        assertEquals(Boolean.FALSE, jdbc.queryForObject(
                "SELECT disponible FROM drone WHERE id = 'D-UNAL-01'", Boolean.class));
        mvc.perform(get("/api/v3/flota/UNAL/disponibles")).andExpect(content().json("[]"));
        assertTrue(notificaciones.eventos().contains(misionId + ":D-UNAL-01:EN_VUELO"));

        mvc.perform(post("/api/v3/misiones").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sede\":\"UNAL\",\"destino\":\"ECI\",\"pesoPaquete\":800,\"prioridad\":\"URGENTE\"}"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void flotaDeSedeDesconocida_retorna404() throws Exception {
        mvc.perform(get("/api/v3/flota/UPB/disponibles")).andExpect(status().isNotFound());
    }
}
