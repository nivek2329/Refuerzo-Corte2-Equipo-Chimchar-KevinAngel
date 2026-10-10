package edu.eci.skycampus.enterprise.infrastructure.config;

import edu.eci.skycampus.enterprise.application.AsignadorMision;
import edu.eci.skycampus.enterprise.domain.EstrategiaAsignacion;
import edu.eci.skycampus.enterprise.domain.EstrategiaMayorBateria;
import edu.eci.skycampus.enterprise.domain.ObservadorDrone;
import edu.eci.skycampus.enterprise.domain.RepositorioFlota;
import edu.eci.skycampus.enterprise.domain.RepositorioSedes;
import edu.eci.skycampus.enterprise.domain.ServicioAerocivil;
import edu.eci.skycampus.enterprise.domain.ServicioClima;
import edu.eci.skycampus.enterprise.infrastructure.ServicioAerocivilSimulado;
import edu.eci.skycampus.enterprise.infrastructure.ServicioClimaSimulado;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Ensamblaje de las capas: aquí, y solo aquí, se elige qué adaptador concreto recibe cada puerto.
 * Clima y Aerocivil usan adaptadores locales hasta tener los clientes HTTP reales.
 */
@Configuration
public class ConfiguracionEnterprise {
    @Bean
    Clock reloj() {
        return Clock.systemUTC();
    }

    @Bean
    ServicioClima servicioClima() {
        return new ServicioClimaSimulado((origen, destino) -> true);
    }

    @Bean
    ServicioAerocivil servicioAerocivil() {
        return new ServicioAerocivilSimulado((origen, destino) -> true);
    }

    @Bean
    EstrategiaAsignacion estrategiaAsignacion() {
        return new EstrategiaMayorBateria();
    }

    @Bean
    AsignadorMision asignadorMision(RepositorioFlota flota, ServicioClima clima, ServicioAerocivil aerocivil,
                                    RepositorioSedes sedes, EstrategiaAsignacion estrategia,
                                    ObservadorDrone notificador) {
        return new AsignadorMision(flota, clima, aerocivil, sedes, estrategia, notificador);
    }
}
