package edu.eci.skycampus.enterprise.domain;

/** Puerto para saber si una sede está operando (un superadmin puede desactivarla). */
public interface RepositorioSedes {
    boolean estaActiva(Sede sede);
}
