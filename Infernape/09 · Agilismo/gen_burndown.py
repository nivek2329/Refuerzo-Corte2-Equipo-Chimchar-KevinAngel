"""Dibuja el burndown simulado del Sprint 1 de SkyCampus Enterprise (SVG). Uso: python gen_burndown.py"""
from pathlib import Path

DIAS = ["13", "14", "15", "16", "17", "20", "21", "22", "23", "24", "Fin"]
REAL = [21, 21, 19, 19, 16, 13, 13, 10, 8, 5, 3]
COMPROMETIDO = 21
W, H, X0, Y0, ANCHO, ALTO = 760, 380, 70, 40, 640, 270


def x(i):
    return X0 + i * ANCHO / (len(DIAS) - 1)


def y(puntos):
    return Y0 + ALTO - puntos * ALTO / COMPROMETIDO


def main():
    p = [f'<svg xmlns="http://www.w3.org/2000/svg" width="{W}" height="{H}" viewBox="0 0 {W} {H}" font-family="Arial">',
         f'<rect width="{W}" height="{H}" fill="#FBF9F2"/>',
         '<text x="70" y="24" font-size="16" font-weight="700" fill="#163A59">Burndown Sprint 1 · conectividad multi-sede (oct 13–24)</text>']
    for v in range(0, COMPROMETIDO + 1, 3):
        p.append(f'<line x1="{X0}" y1="{y(v):.1f}" x2="{X0 + ANCHO}" y2="{y(v):.1f}" stroke="#E2DED2"/>'
                 f'<text x="{X0 - 10}" y="{y(v) + 4:.1f}" font-size="11" text-anchor="end" fill="#64717C">{v}</text>')
    for i, d in enumerate(DIAS):
        p.append(f'<text x="{x(i):.1f}" y="{Y0 + ALTO + 18}" font-size="11" text-anchor="middle" fill="#64717C">{d}</text>')
    p.append(f'<line x1="{x(0)}" y1="{y(COMPROMETIDO)}" x2="{x(len(DIAS) - 1)}" y2="{y(0)}" stroke="#9AA6B2" stroke-width="2" stroke-dasharray="6 5"/>')
    puntos = " ".join(f"{x(i):.1f},{y(v):.1f}" for i, v in enumerate(REAL))
    p.append(f'<polyline points="{puntos}" fill="none" stroke="#1479B8" stroke-width="3"/>')
    for i, v in enumerate(REAL):
        p.append(f'<circle cx="{x(i):.1f}" cy="{y(v):.1f}" r="4" fill="#1479B8"/>')
    p.append(f'<text x="{x(10) - 6:.1f}" y="{y(3) - 10:.1f}" font-size="11" text-anchor="end" fill="#C74747">3 pts sin terminar (HU-E06)</text>')
    p.append(f'<text x="{X0}" y="{H - 14}" font-size="11" fill="#163A59">— real (puntos pendientes al cierre del día)   - - ideal   ·   eje x: días hábiles de octubre</text>')
    p.append('</svg>')
    (Path(__file__).resolve().parent / "Burndown_Sprint1.svg").write_text("\n".join(p), encoding="utf-8")


if __name__ == "__main__":
    main()
