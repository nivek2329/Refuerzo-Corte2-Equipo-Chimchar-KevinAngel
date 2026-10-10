package edu.eci.skycampus.enterprise.routing;

/**
 * Tiempo de un tramo inter-sede con viento. {@code vientoDesdeGrados} es la dirección meteorológica desde la que
 * sopla. El viento se descompone respecto al rumbo: la componente longitudinal frena o empuja, y la lateral obliga
 * al drone a desviar la proa para no salirse de la ruta, lo que consume parte de su velocidad.
 */
public final class CalculadorRutaInterSede {
    public double tiempoEstimadoHoras(double distanciaKm, double velocidadDronKmH,
                                      double velocidadVientoKmH, double rumboGrados,
                                      double vientoDesdeGrados) {
        validarEntradas(distanciaKm, velocidadDronKmH, velocidadVientoKmH, rumboGrados, vientoDesdeGrados);
        double velocidadSobreSuelo = velocidadSobreSuelo(velocidadDronKmH, velocidadVientoKmH,
                rumboGrados, vientoDesdeGrados);
        if (velocidadSobreSuelo <= 0) {
            throw new IllegalArgumentException("el viento impide avanzar en la ruta");
        }
        return distanciaKm / velocidadSobreSuelo;
    }

    private static double velocidadSobreSuelo(double velocidadDronKmH, double velocidadVientoKmH,
                                               double rumboGrados, double vientoDesdeGrados) {
        double diferencia = Math.toRadians(vientoDesdeGrados - rumboGrados);
        double vientoEnContra = velocidadVientoKmH * Math.cos(diferencia);
        double vientoCruzado = velocidadVientoKmH * Math.sin(diferencia);
        if (Math.abs(vientoCruzado) >= velocidadDronKmH) {
            throw new IllegalArgumentException("el viento cruzado impide mantener el rumbo");
        }
        double avanceEnElAire = Math.sqrt(velocidadDronKmH * velocidadDronKmH - vientoCruzado * vientoCruzado);
        return avanceEnElAire - vientoEnContra;
    }

    private static void validarEntradas(double distanciaKm, double velocidadDronKmH,
                                        double velocidadVientoKmH, double rumboGrados,
                                        double vientoDesdeGrados) {
        if (!Double.isFinite(distanciaKm) || !Double.isFinite(velocidadDronKmH)
                || !Double.isFinite(velocidadVientoKmH) || distanciaKm <= 0
                || velocidadDronKmH <= 0 || velocidadVientoKmH < 0) {
            throw new IllegalArgumentException("distancia y velocidades deben ser finitas y válidas");
        }
        validarDireccion(rumboGrados);
        validarDireccion(vientoDesdeGrados);
    }

    private static void validarDireccion(double grados) {
        if (!Double.isFinite(grados) || grados < 0 || grados >= 360) {
            throw new IllegalArgumentException("las direcciones deben estar entre 0 y menos de 360 grados");
        }
    }
}
