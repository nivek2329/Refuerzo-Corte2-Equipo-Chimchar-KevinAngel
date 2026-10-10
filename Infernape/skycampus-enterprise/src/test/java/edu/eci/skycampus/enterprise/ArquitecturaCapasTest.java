package edu.eci.skycampus.enterprise;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Regla de dependencias de las capas, comprobada sobre el código fuente:
 * dominio solo usa el JDK; aplicación solo usa el JDK y el dominio (nunca infraestructura).
 */
class ArquitecturaCapasTest {
    private static final Path RAIZ = Path.of("src", "main", "java", "edu", "eci", "skycampus", "enterprise");

    @ParameterizedTest(name = "{0} solo importa {1}")
    @CsvSource({
        "domain, 'java.'",
        "application, 'java.|edu.eci.skycampus.enterprise.domain.'"
    })
    void cadaCapaSoloImportaLoPermitido(String capa, String prefijosPermitidos) throws IOException {
        List<String> prefijos = List.of(prefijosPermitidos.split("\\|"));
        List<Path> fuentes;
        try (Stream<Path> archivos = Files.list(RAIZ.resolve(capa))) {
            fuentes = archivos.filter(archivo -> archivo.toString().endsWith(".java")).toList();
        }
        List<String> prohibidos = fuentes.stream().flatMap(ArquitecturaCapasTest::importaciones)
                .filter(importacion -> prefijos.stream().noneMatch(importacion::startsWith))
                .toList();

        assertFalse(fuentes.isEmpty(), "no se encontraron fuentes de la capa " + capa);
        assertEquals(List.of(), prohibidos);
    }

    private static Stream<String> importaciones(Path archivo) {
        try {
            return Files.readAllLines(archivo).stream()
                    .filter(linea -> linea.startsWith("import "))
                    .map(linea -> linea.replace("import static ", "").replace("import ", "").replace(";", "").trim());
        } catch (IOException e) {
            throw new IllegalStateException("no se pudo leer " + archivo, e);
        }
    }
}
