# 13 · JaCoCo — Cobertura de código

El plugin JaCoCo está configurado en `../12 · TDD/pom.xml`:

- `prepare-agent`: instrumenta las pruebas.
- `report` (fase `test`): genera `target/site/jacoco/index.html`.
- `check` (fase `verify`): exige mínimo 80 % de cobertura de líneas (`LINE COVEREDRATIO ≥ 0.80`).

## Cómo reproducirlo

```bash
cd "12 · TDD"
mvn verify
```

Abrir `target/site/jacoco/index.html`.

## Resultado

| Métrica | Valor |
|---|---|
| Pruebas | _pendiente: completar tras `mvn verify`_ |
| Cobertura de líneas | _pendiente_ |
| Cobertura de ramas | _pendiente_ |

Evidencia: `Reporte_JaCoCo.png` (captura de `index.html`) — _pendiente_.
