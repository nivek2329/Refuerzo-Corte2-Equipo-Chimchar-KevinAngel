# 08 · Sistema de diseño multi-sede con design tokens

| Archivo | Qué es |
|---|---|
| [`tokens.css`](tokens.css) | Especificación ejecutable de los tokens: marca por sede, derivados y del sistema. |
| [`componentes.css`](componentes.css) | Componentes reutilizables (Tarjeta de drone, Panel de misión activa, botones, estado, batería). Solo leen tokens. |
| [`Sistema_Tokens_Enterprise.html`](Sistema_Tokens_Enterprise.html) | Página de demostración: tabla de tokens leída en vivo, Panel de misión activa en ECI/UNAL/Uniandes, Tarjeta de drone con HTML idéntico en las 3 identidades (con verificación automática) y los 5 estados. Abrir en el navegador. |

El prototipo del [reto 11](../11%20·%20Mocks%20y%20Prototipos) importa estos dos mismos archivos: ahí la identidad cambia entre ECI y UNAL sin tocar el HTML.

## Tokens

### 1. Marca (lo único que cambia por sede)

| Token | Uso | ECI | UNAL | Uniandes | EAFIT |
|---|---|---|---|---|---|
| `--color-primary` | Botón principal, escudo, progreso de ruta, foco | `#00457C` | `#7B0000` | `#0057A8` | `#2D6A4F` |
| `--color-alert` | Cancelar misión, estado FALLO, batería < 30 % | `#E63946` | `#E63946` | `#E63946` | `#E63946` |
| `--font-ui` | Toda la interfaz | Space Grotesk | Merriweather | Inter | Source Sans 3 |
| `--border-radius` | Radio base de tarjetas, botones y campos | 10px | 4px | 8px | 12px |
| `--sede-sigla` | Texto del escudo (contenido, no estilo) | ECI | UN | UA | EA |

`--color-alert` es igual en las cuatro sedes a propósito: una alerta de seguridad debe verse igual en toda la red.

### 2. Derivados (se calculan solos)

| Token | Fórmula |
|---|---|
| `--color-primary-soft` | 10 % del primario sobre blanco (fondos de ruta, botones secundarios) |
| `--color-primary-line` | 28 % del primario (líneas, foco) |
| `--color-primary-strong` | 82 % del primario con negro (degradado del escudo) |
| `--shadow-tinted` | sombra teñida con el primario |
| `--radius-sm` / `--radius-lg` | `0.6×` y `1.6×` de `--border-radius` |

### 3. Sistema (iguales para toda la red)

Superficies y texto (`--color-bg`, `--color-surface`, `--color-text`, `--color-muted`, `--color-border`); estados de drone (`--estado-disponible` verde, `--estado-en-vuelo` azul, `--estado-en-carga` ámbar, `--estado-fallo` = `--color-alert`, `--estado-mantenimiento` gris); batería (≥ 60 % verde, 30–59 % ámbar, < 30 % alerta); escala de texto 12/14/16/20/28/44 px; espaciado 4–48 px; `--tap-min: 48px` (Fitts).

### Accesibilidad verificada

| Primario | Contraste con texto blanco | Resultado |
|---|---|---|
| ECI `#00457C` | 9.8:1 | AA y AAA |
| UNAL `#7B0000` | 11.4:1 | AA y AAA |
| Uniandes `#0057A8` | 7.2:1 | AA y AAA |
| EAFIT `#2D6A4F` | 6.4:1 | AA |
| Alerta `#E63946` | 4.2:1 | AA solo para texto grande o en negrita ≥ 14 px; por eso el botón "Cancelar misión" usa 14 px en negrita y el estado FALLO siempre lleva texto además del color. |

## Componentes y prueba de que no cambian

- **Panel de misión activa** (`.panel-mision`): escudo de la sede, indicador en vivo, ruta origen → estación → destino con progreso, 3 KPI y dos acciones grandes separadas (ver mapa / cancelar). Las tres variantes de la página usan las mismas clases; solo cambia `data-sede`.
- **Tarjeta de drone** (`.drone-card`): las tres copias son el mismo texto HTML. Al cargar, la página compara su `outerHTML` y muestra "HTML idéntico en las 3 identidades". Si alguien edita una copia, el aviso se pone en rojo.

## Configurar una sede nueva en menos de 2 horas

Ejemplo con una quinta sede, *UPB*:

| Paso | Qué hacer | Tiempo |
|---|---|---|
| 1 | Pedir a la universidad su color institucional, tipografía (con licencia web) y preferencia de esquinas. | 30 min |
| 2 | Verificar contraste del primario con blanco ≥ 4.5:1 (cualquier verificador WCAG). Si no llega, usar una variante más oscura del mismo tono. | 10 min |
| 3 | Copiar en `tokens.css` un bloque `[data-sede="upb"] { --sede-sigla; --color-primary; --color-alert: #E63946; --font-ui; --border-radius; }`. No se tocan los derivados ni los del sistema. | 10 min |
| 4 | Agregar la fuente al `<link>` de Google Fonts (o al `@font-face` si es propia). | 10 min |
| 5 | Agregar la columna "UPB" a `SEDES` en la página de tokens y abrirla: revisar tabla, panel, tarjeta y los 5 estados. | 20 min |
| 6 | Registrar la sede en la base (`sede`, `activa = true`) y probar una misión en el prototipo con `data-sede="upb"`. | 20 min |
| **Total** | | **≈ 1 h 40 min** |

No hace falta tocar `componentes.css` ni ningún HTML de componente.
