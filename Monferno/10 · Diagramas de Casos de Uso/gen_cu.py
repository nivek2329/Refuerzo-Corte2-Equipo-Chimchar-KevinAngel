"""Genera el diagrama de casos de uso del módulo de misiones de SkyCampus v2 (SVG + .drawio) desde una sola
especificación. Uso, desde esta carpeta:  python gen_cu.py"""
import html, math
from pathlib import Path

AQUI = Path(__file__).resolve().parent
W, H = 1720, 1220
INK, MUTED, GREEN, BLUE, AMBER, RED, LINE = '#172B4D', '#536273', '#2E7D32', '#1F5F99', '#B7791F', '#C0392B', '#33475B'
BX, BY, BW, BH = 330, 110, 1040, 1020   # frontera del sistema

# actores: id, nombre, x, y (centro de la figura), tipo ('persona' | 'sistema')
ACTORES = [
    ('sol', 'Solicitante', 170, 230, 'persona'),
    ('op', 'Operador de drones', 170, 600, 'persona'),
    ('tec', 'Técnico de\nmantenimiento', 170, 1060, 'persona'),
    ('adm', 'Admin', 1560, 640, 'persona'),
    ('api', 'API Meteorológica', 1560, 270, 'sistema'),
    ('ale', 'Sistema de Alertas', 1560, 960, 'sistema'),
]
# casos de uso: id, texto, cx, cy, ancho, tipo ('base' | 'incluido' | 'extension')
CU = [
    ('reg', 'Registrar solicitud\nde reparto', 540, 195, 220, 'base'),
    ('est', 'Consultar estado\nde la misión', 540, 300, 220, 'base'),
    ('cla', 'Notificar clima\nadverso', 560, 405, 210, 'extension'),
    ('sin', 'Notificar sin\ndrones aptos', 560, 505, 210, 'extension'),
    ('flo', 'Ver flota agrupada\npor estado', 540, 605, 220, 'base'),
    ('sup', 'Supervisar misiones\nen vuelo', 540, 700, 220, 'base'),
    ('can', 'Cancelar misión\nen vuelo', 540, 795, 220, 'base'),
    ('asg', 'Asignar drone\nautomáticamente', 870, 270, 270, 'base'),
    ('estr', 'Aplicar estrategia\nsegún la prioridad', 1200, 155, 230, 'incluido'),
    ('clm', 'Validar condiciones\nclimáticas', 1200, 270, 230, 'incluido'),
    ('apt', 'Filtrar drones aptos\n(batería, capacidad,\ndisponibilidad)', 1200, 390, 240, 'incluido'),
    ('alt', 'Alertar técnico\n(batería baja)', 870, 760, 210, 'extension'),
    ('fal', 'Notificar drone\nen FALLO', 870, 880, 220, 'base'),
    ('dia', 'Diagnosticar fallo', 870, 980, 220, 'base'),
    ('rep', 'Marcar drone\ncomo reparado', 870, 1075, 220, 'base'),
    ('cfl', 'Configurar flota\ny destinos', 1200, 535, 220, 'base'),
    ('pol', 'Configurar política de\nasignación por prioridad', 1200, 640, 250, 'base'),
    ('sta', 'Consultar estadísticas\nde misiones', 1200, 745, 230, 'base'),
]
CU_H = 76
# asociaciones actor — caso de uso
ASOC = [('sol', 'reg'), ('sol', 'est'), ('op', 'cla'), ('op', 'sin'), ('op', 'flo'), ('op', 'sup'), ('op', 'can'),
        ('tec', 'alt'), ('tec', 'fal'), ('tec', 'dia'), ('tec', 'rep'), ('api', 'clm'), ('ale', 'fal'),
        ('adm', 'cfl'), ('adm', 'pol'), ('adm', 'sta')]
# include: base -> incluido (siempre)
INCLUDE = [('reg', 'asg'), ('asg', 'clm'), ('asg', 'apt'), ('asg', 'estr')]
# extend: extensión -> base, con condición y punto de extensión
# (extensión, base, condición, posición (x, y) de la etiqueta, alineación)
EXTEND = [
    ('cla', 'asg', '[la API responde "no apto",\nno responde en 2 s o da error]', (718, 338), 'middle'),
    ('sin', 'asg', '[ningún drone es apto]', (705, 455), 'middle'),
    ('alt', 'asg', '[batería del drone asignado\nentre 30 % y 40 %]', (880, 560), 'start'),
]
INCLUDE_LBL = {('reg', 'asg'): (690, 212), ('asg', 'clm'): (1040, 255), ('asg', 'apt'): (1045, 352), ('asg', 'estr'): (1040, 188)}
PUNTOS = {'asg': 'puntos de extensión:\nclima · aptos · batería'}
ALTO = {'asg': 96}

act = {a[0]: a for a in ACTORES}
cu = {c[0]: c for c in CU}


def borde_elipse(c, hacia):
    """Punto del borde de la elipse del caso c en dirección al punto 'hacia'."""
    cid, _, cx, cy, w, _ = c
    a, b = w / 2, ALTO.get(cid, CU_H) / 2
    dx, dy = hacia[0] - cx, hacia[1] - cy
    if dx == dy == 0:
        return cx, cy
    t = 1 / math.sqrt((dx / a) ** 2 + (dy / b) ** 2)
    return cx + dx * t, cy + dy * t


def punto_actor(a, hacia):
    _, _, x, y, tipo = a
    if tipo == 'sistema':
        return (x - 80 if hacia[0] < x else x + 80), y
    return (x + 26 if hacia[0] > x else x - 26), y - 10


def texto(x, y, s, size=13, color=INK, weight=400, italic=False, lh=16, anchor='middle'):
    st = ' font-style="italic"' if italic else ''
    lineas = s.split('\n')
    y0 = y - (len(lineas) - 1) * lh / 2 + size * 0.35
    return ''.join(f'<text x="{x}" y="{y0 + i * lh:.1f}" font-size="{size}" fill="{color}" font-weight="{weight}"'
                   f'{st} text-anchor="{anchor}">{html.escape(l)}</text>' for i, l in enumerate(lineas))


def svg():
    s = [f'<svg xmlns="http://www.w3.org/2000/svg" width="{W}" height="{H}" viewBox="0 0 {W} {H}" '
         'font-family="Inter, Segoe UI, Arial, sans-serif">',
         '<defs><marker id="abierta" viewBox="0 0 12 12" refX="11" refY="6" markerWidth="11" markerHeight="11" '
         f'orient="auto"><path d="M1,1 L11,6 L1,11" fill="none" stroke="{LINE}" stroke-width="1.5"/></marker>'
         '<marker id="herencia" viewBox="0 0 16 16" refX="15" refY="8" markerWidth="16" markerHeight="16" '
         f'orient="auto"><path d="M1,1 L15,8 L1,15 z" fill="#fff" stroke="{LINE}" stroke-width="1.5"/></marker></defs>',
         f'<rect width="{W}" height="{H}" fill="#F7F8FA"/>',
         f'<text x="40" y="50" font-size="26" font-weight="700" fill="{INK}">SkyCampus v2 — Casos de uso del módulo de misiones</text>',
         f'<text x="42" y="80" font-size="15" fill="{MUTED}">Monferno · 4 actores con herencia · «include» siempre · «extend» con condición</text>',
         f'<rect x="{BX}" y="{BY}" width="{BW}" height="{BH}" rx="14" fill="#FFFFFF" stroke="{INK}" stroke-width="2"/>',
         texto(BX + 20, BY + 26, 'Sistema SkyCampus v2 · módulo de misiones', 15, INK, 700, anchor='start')]
    # asociaciones
    for (a_id, c_id) in ASOC:
        c = cu[c_id]; a = act[a_id]
        p1 = punto_actor(a, (c[2], c[3])); p2 = borde_elipse(c, p1)
        s.append(f'<line x1="{p1[0]:.1f}" y1="{p1[1]:.1f}" x2="{p2[0]:.1f}" y2="{p2[1]:.1f}" stroke="{LINE}" stroke-width="1.4"/>')
    # include / extend: etiqueta en posición fija con fondo blanco para que no se monte sobre líneas
    def etiqueta(x, y, lineas, color, anchor):
        ancho = max(len(l) for l in lineas) * 6.4 + 12
        x0 = x - ancho / 2 if anchor == 'middle' else x - 6
        s.append(f'<rect x="{x0:.1f}" y="{y - 10:.1f}" width="{ancho:.1f}" height="{len(lineas) * 15 + 6}" rx="4" '
                 f'fill="#FFFFFF" fill-opacity="0.94"/>')
        for i, l in enumerate(lineas):
            estilo = 'font-weight="700" font-style="italic"' if i == 0 else ''
            s.append(f'<text x="{x}" y="{y + 4 + i * 15:.1f}" font-size="{12 if i == 0 else 11}" fill="{color}" '
                     f'{estilo} text-anchor="{anchor}">{html.escape(l)}</text>')
    def flecha(o, d, color):
        co, cd = cu[o], cu[d]
        p1 = borde_elipse(co, (cd[2], cd[3])); p2 = borde_elipse(cd, (co[2], co[3]))
        s.append(f'<line x1="{p1[0]:.1f}" y1="{p1[1]:.1f}" x2="{p2[0]:.1f}" y2="{p2[1]:.1f}" stroke="{color}" '
                 'stroke-width="1.6" stroke-dasharray="7 5" marker-end="url(#abierta)"/>')
    for (o, d) in INCLUDE:
        flecha(o, d, BLUE)
    for (o, d, cond, pos, anchor) in EXTEND:
        flecha(o, d, AMBER)
    # generalización Técnico -> Operador
    t, o = act['tec'], act['op']
    s.append(f'<line x1="{t[2]}" y1="{t[3] - 54}" x2="{o[2]}" y2="{o[3] + 84}" stroke="{LINE}" stroke-width="1.6" '
             'marker-end="url(#herencia)"/>')
    # casos de uso
    estilos = {'base': ('#EAF2FB', BLUE), 'incluido': ('#EAF2FB', BLUE), 'extension': ('#FFF6E6', AMBER)}
    for (cid, txt, cx, cy, w, tipo) in CU:
        fill, stroke = estilos[tipo]
        hh = ALTO.get(cid, CU_H)
        s.append(f'<ellipse cx="{cx}" cy="{cy}" rx="{w / 2}" ry="{hh / 2}" fill="{fill}" stroke="{stroke}" stroke-width="1.8"/>')
        if cid in PUNTOS:
            s.append(texto(cx, cy - 14, txt, 13, INK, 700))
            s.append(f'<line x1="{cx - w / 2 + 34}" y1="{cy + 6}" x2="{cx + w / 2 - 34}" y2="{cy + 6}" stroke="{stroke}" stroke-width="0.8"/>')
            s.append(texto(cx, cy + 21, PUNTOS[cid], 10, MUTED, lh=12))
        else:
            s.append(texto(cx, cy, txt, 13, INK, 700))
    # actores
    for (aid, nombre, x, y, tipo) in ACTORES:
        if tipo == 'persona':
            s.append(f'<g stroke="{GREEN}" stroke-width="2.4" fill="none"><circle cx="{x}" cy="{y - 38}" r="14" fill="#fff"/>'
                     f'<line x1="{x}" y1="{y - 24}" x2="{x}" y2="{y + 14}"/><line x1="{x - 24}" y1="{y - 10}" x2="{x + 24}" y2="{y - 10}"/>'
                     f'<line x1="{x}" y1="{y + 14}" x2="{x - 18}" y2="{y + 44}"/><line x1="{x}" y1="{y + 14}" x2="{x + 18}" y2="{y + 44}"/></g>')
            s.append(texto(x, y + 68 + nombre.count('\n') * 8, nombre, 14, GREEN, 700))
        else:
            s.append(f'<rect x="{x - 80}" y="{y - 34}" width="160" height="68" rx="6" fill="#FDF2F1" stroke="{RED}" stroke-width="2"/>')
            s.append(texto(x, y - 12, '«sistema»', 11, RED, italic=True))
            s.append(texto(x, y + 10, nombre, 13, RED, 700))
    # etiquetas de include/extend al final, encima de todo
    for (o, d) in INCLUDE:
        x, y = INCLUDE_LBL[(o, d)]
        etiqueta(x, y, ['«include»'], BLUE, 'middle')
    for (o, d, cond, (x, y), anchor) in EXTEND:
        etiqueta(x, y, ['«extend»'] + cond.split('\n'), AMBER, anchor)
    # leyenda
    lx, ly = 40, 1190
    s.append(texto(lx, ly - 20, 'Actores secundarios (sistemas externos del reto 05) en rojo.', 12, MUTED, anchor='start'))
    s.append(texto(lx, ly, 'Generalización (triángulo hueco): el Técnico hereda todos los casos del Operador (no se repiten).', 12, MUTED, anchor='start'))
    s.append('</svg>')
    return '\n'.join(s)


def drawio():
    a = lambda v: html.escape(v, quote=True)
    c = []
    def v(cid, val, style, x, y, w, h):
        c.append(f'<mxCell id="{cid}" value="{a(val)}" style="{style}" vertex="1" parent="1"><mxGeometry x="{x}" y="{y}" width="{w}" height="{h}" as="geometry"/></mxCell>')
    def e(eid, src, tgt, val, style):
        c.append(f'<mxCell id="{eid}" value="{a(val)}" style="{style}" edge="1" parent="1" source="{src}" target="{tgt}"><mxGeometry relative="1" as="geometry"/></mxCell>')
    txt = 'text;html=1;strokeColor=none;fillColor=none;align=left;verticalAlign=middle;whiteSpace=wrap;'
    v('titulo', 'SkyCampus v2 — Casos de uso del módulo de misiones', txt + f'fontSize=24;fontStyle=1;fontColor={INK};', 40, 26, 900, 34)
    v('subtitulo', 'Monferno · 4 actores con herencia · «include» siempre · «extend» con condición', txt + f'fontSize=14;fontColor={MUTED};', 42, 62, 900, 24)
    v('frontera', 'Sistema SkyCampus v2 · módulo de misiones', f'rounded=1;arcSize=2;html=1;whiteSpace=wrap;fillColor=#FFFFFF;strokeColor={INK};strokeWidth=2;verticalAlign=top;align=left;spacingLeft=20;spacingTop=8;fontStyle=1;fontSize=15;fontColor={INK};', BX, BY, BW, BH)
    estilos = {'base': ('#EAF2FB', BLUE), 'incluido': ('#EAF2FB', BLUE), 'extension': ('#FFF6E6', AMBER)}
    for (cid, t, cx, cy, w, tipo) in CU:
        fill, stroke = estilos[tipo]
        val = f'<b>{t.replace(chr(10), "<br>")}</b>'
        if cid in PUNTOS:
            val += f'<hr><font style="font-size:10px" color="{MUTED}">{PUNTOS[cid].replace(chr(10), "<br>")}</font>'
        hh = ALTO.get(cid, CU_H)
        v(cid, val, f'ellipse;whiteSpace=wrap;html=1;fillColor={fill};strokeColor={stroke};strokeWidth=1.8;fontSize=13;fontColor={INK};', cx - w / 2, cy - hh / 2, w, hh)
    for (aid, nombre, x, y, tipo) in ACTORES:
        if tipo == 'persona':
            v(aid, nombre.replace('\n', '<br>'), f'shape=umlActor;verticalLabelPosition=bottom;verticalAlign=top;html=1;outlineConnect=0;strokeColor={GREEN};strokeWidth=2.4;fillColor=#FFFFFF;fontColor={GREEN};fontStyle=1;fontSize=14;', x - 24, y - 52, 48, 96)
        else:
            v(aid, f'<i>«sistema»</i><br><b>{nombre}</b>', f'rounded=1;arcSize=8;whiteSpace=wrap;html=1;fillColor=#FDF2F1;strokeColor={RED};strokeWidth=2;fontColor={RED};fontSize=13;', x - 80, y - 34, 160, 68)
    for i, (aid, cid) in enumerate(ASOC):
        e(f'asoc{i + 1}', aid, cid, '', f'endArrow=none;html=1;strokeColor={LINE};strokeWidth=1.4;edgeStyle=none;')
    for i, (o, d) in enumerate(INCLUDE):
        e(f'inc{i + 1}', o, d, '«include»', f'endArrow=open;endSize=11;html=1;dashed=1;dashPattern=7 5;strokeColor={BLUE};fontColor={BLUE};fontStyle=3;strokeWidth=1.6;edgeStyle=none;labelBackgroundColor=#FFFFFF;')
    for i, (o, d, cond, _, _) in enumerate(EXTEND):
        e(f'ext{i + 1}', o, d, f'«extend»<br><span style="font-weight:normal;font-style:normal">{cond.replace(chr(10), "<br>")}</span>', f'endArrow=open;endSize=11;html=1;dashed=1;dashPattern=7 5;strokeColor={AMBER};fontColor={AMBER};fontStyle=3;strokeWidth=1.6;edgeStyle=none;labelBackgroundColor=#FFFFFF;fontSize=11;')
    e('herencia', 'tec', 'op', '', f'endArrow=block;endFill=0;endSize=16;html=1;strokeColor={LINE};strokeWidth=1.6;edgeStyle=none;fontColor={MUTED};fontSize=11;labelBackgroundColor=#F7F8FA;')
    pagina = ('<diagram id="skycampus-cu-v2" name="Casos de uso v2 (Monferno)"><mxGraphModel dx="1720" dy="1130" grid="1" '
              'gridSize="10" guides="1" tooltips="1" connect="1" arrows="1" fold="1" page="1" pageScale="1" '
              f'pageWidth="{W}" pageHeight="{H}" background="#F7F8FA" math="0" shadow="0"><root><mxCell id="0"/><mxCell id="1" parent="0"/>'
              + ''.join(c) + '</root></mxGraphModel></diagram>')
    return '<mxfile host="app.diagrams.net" agent="gen_cu.py" version="24.7.17" type="device">\n' + pagina + '\n</mxfile>\n'


if __name__ == '__main__':
    (AQUI / 'Diagrama_Casos_Uso_Misiones_v2.svg').write_text(svg(), encoding='utf-8')
    (AQUI / 'Diagrama_Casos_Uso_Misiones_v2.drawio').write_text(drawio(), encoding='utf-8')
    print(f'{sum(1 for a in ACTORES if a[4] == "persona")} actores + {sum(1 for a in ACTORES if a[4] == "sistema")} sistemas externos, {len(CU)} casos, {len(INCLUDE)} include, {len(EXTEND)} extend, {len(ASOC)} asociaciones')
