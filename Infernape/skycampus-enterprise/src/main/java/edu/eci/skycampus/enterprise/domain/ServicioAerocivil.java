package edu.eci.skycampus.enterprise.domain;

/** Puerto hacia la Aerocivil (sistema externo de regulación aérea). */
public interface ServicioAerocivil {
    boolean autorizaRuta(String origen, String destino);
}
