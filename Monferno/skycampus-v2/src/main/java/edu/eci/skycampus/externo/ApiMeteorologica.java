package edu.eci.skycampus.externo;

/** Sistema externo: servicio meteorológico que dice si las condiciones (viento, lluvia) permiten volar. */
@FunctionalInterface
public interface ApiMeteorologica {
    boolean esApto();
}
