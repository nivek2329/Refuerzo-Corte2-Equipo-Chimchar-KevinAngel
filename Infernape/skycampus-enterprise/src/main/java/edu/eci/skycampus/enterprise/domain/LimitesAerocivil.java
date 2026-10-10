package edu.eci.skycampus.enterprise.domain;

/**
 * RNF-09: restricciones de la Aerocivil que el sistema hace cumplir siempre.
 * {@code radioMaximoKm} es el radio autorizado alrededor de una sede; en zona urbana nadie vuela sobre 120 m.
 */
public record LimitesAerocivil(double radioMaximoKm, int alturaMaximaUrbanaMetros) {
    public static final int ALTURA_MAXIMA_URBANA_METROS = 120;

    public LimitesAerocivil {
        if (!Double.isFinite(radioMaximoKm) || radioMaximoKm <= 0
                || alturaMaximaUrbanaMetros <= 0 || alturaMaximaUrbanaMetros > ALTURA_MAXIMA_URBANA_METROS) {
            throw new IllegalArgumentException("límites de la Aerocivil inválidos");
        }
    }

    public static LimitesAerocivil urbanos(double radioMaximoKm) {
        return new LimitesAerocivil(radioMaximoKm, ALTURA_MAXIMA_URBANA_METROS);
    }

    public boolean permiteAltura(int alturaMetros, boolean zonaUrbana) {
        return alturaMetros >= 0 && (!zonaUrbana || alturaMetros <= alturaMaximaUrbanaMetros);
    }
}
