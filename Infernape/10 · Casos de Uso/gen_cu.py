"""Genera el diagrama de casos de uso de SkyCampus Enterprise en 5 paquetes (SVG + .drawio) desde una sola
especificación y comprueba que ninguna línea atraviese un caso de uso. Uso, desde esta carpeta:  python gen_cu.py"""
import html
import math
from pathlib import Path

AQUI = Path(__file__).resolve().parent
W, H = 2000, 1660
INK, MUTED, GREEN, BLUE, AMBER, RED, LINE = '#172B4D', '#536273', '#2E7D32', '#1F5F99', '#B7791F', '#C0392B', '#33475B'
BX, BY, BW, BH = 300, 110, 1400, 1490          # frontera del sistema
PX, PW = 330, 1340                               # paquetes: x y ancho
CU_H = 70

PAQUETES = [  # nombre, y, alto, color de la pestaña
    ('Gestión de Misiones', 140, 270, '#E8F1FB'),
    ('Gestión de Flota', 430, 300, '#EAF6EE'),
    ('Rutas y Navegación', 750, 300, '#FFF6E6'),
    ('Mantenimiento', 1070, 230, '#F3EEFB'),
    ('Administración', 1320, 250, '#FDF0F0'),
]
# actores: id, nombre, x, y (centro), tipo
ACTORES = [
    ('sol', 'Solicitante', 160, 235, 'persona'),
    ('op', 'Operador de sede', 160, 600, 'persona'),
    ('coo', 'Coordinador\nde sede', 160, 1180, 'persona'),
    ('sup', 'Superadmin\nde la red', 160, 1500, 'persona'),
    ('api', 'API Meteorológica', 1840, 820, 'sistema'),
    ('aer', 'Aerocivil', 1840, 960, 'sistema'),
    ('est', 'Estación de carga\nautónoma', 1840, 1060, 'sistema'),
    ('tec', 'Técnico de\nmantenimiento', 1840, 1180, 'persona'),
]
# casos de uso: id, texto, cx, cy, ancho, tipo
CU = [
    ('crear', 'Crear misión', 560, 225, 220, 'base'),
    ('pend', 'Ver solicitudes\npendientes', 560, 330, 220, 'base'),
    ('cancel', 'Cancelar misión', 1000, 225, 220, 'base'),
    ('hist', 'Ver historial', 1330, 300, 200, 'base'),
    ('estado', 'Ver estado\nde drones', 560, 600, 220, 'base'),
    ('asig', 'Asignar drone', 1000, 560, 220, 'incluido'),
    ('transf', 'Transferir drone\nentre sedes', 1000, 670, 230, 'extension'),
    ('anal', 'Ver analytics', 1400, 520, 200, 'incluido'),
    ('simple', 'Calcular ruta\nsimple', 1000, 820, 220, 'base'),
    ('multi', 'Calcular ruta\nmulti-etapa', 1000, 960, 220, 'extension'),
    ('aero', 'Autorizar con\nAerocivil', 1420, 960, 210, 'incluido'),
    ('retorno', 'Aprobar retorno\na servicio', 600, 1180, 220, 'base'),
    ('mant', 'Marcar en\nmantenimiento', 1000, 1250, 220, 'extension'),
    ('diag', 'Diagnosticar fallo', 1400, 1140, 220, 'base'),
    ('config', 'Configurar sede', 560, 1390, 220, 'base'),
    ('report', 'Generar reportes', 900, 1390, 220, 'base'),
    ('users', 'Gestionar usuarios', 900, 1505, 220, 'base'),
    ('dash', 'Ver dashboard\nEnterprise', 1300, 1430, 220, 'base'),
]
ASOC = [('sol', 'crear'), ('op', 'pend'), ('op', 'estado'), ('op', 'cancel'), ('op', 'hist'), ('op', 'simple'),
        ('coo', 'retorno'), ('coo', 'config'), ('coo', 'report'), ('coo', 'transf'),
        ('sup', 'users'), ('sup', 'dash'), ('api', 'simple'), ('aer', 'aero'), ('est', 'multi'),
        ('tec', 'diag'), ('tec', 'mant')]
# include (base -> incluido): posición de la etiqueta, justificación y puntos intermedios opcionales
INCLUDE = [
    ('crear', 'asig', (800, 400), 'Toda misión creada se asigna sola (RF-11)', []),
    ('multi', 'aero', (1210, 945), 'Ninguna ruta inter-sede sin autorización (RF-13)', []),
    ('dash', 'anal', (1600, 1300), 'El dashboard siempre muestra la eficiencia por sede', [(1600, 1430), (1600, 520)]),
]
# extend (extensión -> base): condición, posición de la etiqueta
EXTEND = [
    ('transf', 'asig', '[la sede no tiene drones\ndisponibles y otra sí]', (1180, 625)),
    ('multi', 'simple', '[con carga supera 5 km\nsin recargar]', (860, 890)),
    ('mant', 'diag', '[el diagnóstico exige\nreparación]', (1205, 1240)),
]
PUNTOS = {'asig': 'punto de extensión: sin drones', 'simple': 'punto de extensión: autonomía', 'diag': 'punto de extensión: reparación'}
GENERALIZACION = [('coo', 'op'), ('sup', 'coo')]   # hijo -> padre (triángulo hueco)

act = {a[0]: a for a in ACTORES}
cu = {c[0]: c for c in CU}


def alto(cid):
    return CU_H + (14 if cid in PUNTOS else 0)


def borde_elipse(c, hacia):
    cid, _, cx, cy, w, _ = c
    a, b = w / 2, alto(cid) / 2
    dx, dy = hacia[0] - cx, hacia[1] - cy
    t = 1 / math.sqrt((dx / a) ** 2 + (dy / b) ** 2)
    return cx + dx * t, cy + dy * t


def punto_actor(a, hacia):
    _, _, x, y, tipo = a
    if tipo == 'sistema':
        return (x - 90 if hacia[0] < x else x + 90), y
    return (x + 30 if hacia[0] > x else x - 30), y - 10


def segmentos():
    """Todas las líneas del diagrama, con los casos que unen (para no contarlos como cruce)."""
    lineas = []
    for a_id, c_id in ASOC:
        p1 = punto_actor(act[a_id], cu[c_id][2:4]); p2 = borde_elipse(cu[c_id], p1)
        lineas.append((p1, p2, {c_id}))
    for o, d, *resto in INCLUDE + EXTEND:
        for p1, p2 in tramos(o, d):
            lineas.append((p1, p2, {o, d}))
    return lineas


def via(o, d):
    for inc in INCLUDE:
        if inc[0] == o and inc[1] == d:
            return inc[4]
    return []


def tramos(o, d):
    """Segmentos de una relación entre casos; si tiene puntos intermedios, sale y entra por el lado más cercano."""
    puntos = via(o, d)
    inicio = borde_elipse(cu[o], puntos[0] if puntos else cu[d][2:4])
    fin = borde_elipse(cu[d], puntos[-1] if puntos else cu[o][2:4])
    camino = [inicio] + puntos + [fin]
    return list(zip(camino, camino[1:]))


def cruces():
    encontrados = []
    for p1, p2, propios in segmentos():
        for c in CU:
            if c[0] in propios:
                continue
            cid, _, cx, cy, w, _ = c
            a, b = w / 2 + 6, alto(cid) / 2 + 6
            for i in range(1, 200):
                x = p1[0] + (p2[0] - p1[0]) * i / 200; y = p1[1] + (p2[1] - p1[1]) * i / 200
                if ((x - cx) / a) ** 2 + ((y - cy) / b) ** 2 < 1:
                    encontrados.append((sorted(propios), cid)); break
    return encontrados


def texto(x, y, s, size=13, color=INK, weight=400, italic=False, lh=16, anchor='middle'):
    st = ' font-style="italic"' if italic else ''
    lineas = s.split('\n')
    y0 = y - (len(lineas) - 1) * lh / 2 + size * 0.35
    return ''.join(f'<text x="{x}" y="{y0 + i * lh:.1f}" font-size="{size}" fill="{color}" font-weight="{weight}"'
                   f'{st} text-anchor="{anchor}">{html.escape(l)}</text>' for i, l in enumerate(lineas))


ESTILOS = {'base': ('#FFFFFF', BLUE), 'incluido': ('#EAF2FB', BLUE), 'extension': ('#FFF6E6', AMBER)}


def svg():
    s = [f'<svg xmlns="http://www.w3.org/2000/svg" width="{W}" height="{H}" viewBox="0 0 {W} {H}" '
         'font-family="Inter, Segoe UI, Arial, sans-serif">',
         '<defs><marker id="abierta" viewBox="0 0 12 12" refX="11" refY="6" markerWidth="11" markerHeight="11" '
         f'orient="auto"><path d="M1,1 L11,6 L1,11" fill="none" stroke="{LINE}" stroke-width="1.5"/></marker>'
         '<marker id="herencia" viewBox="0 0 16 16" refX="15" refY="8" markerWidth="16" markerHeight="16" '
         f'orient="auto"><path d="M1,1 L15,8 L1,15 z" fill="#fff" stroke="{LINE}" stroke-width="1.5"/></marker></defs>',
         f'<rect width="{W}" height="{H}" fill="#F7F8FA"/>',
         f'<text x="40" y="48" font-size="26" font-weight="700" fill="{INK}">SkyCampus Enterprise — Casos de uso de la red de 4 sedes</text>',
         f'<text x="42" y="78" font-size="15" fill="{MUTED}">5 paquetes por módulo · herencia de actores · «include» = siempre ocurre · «extend» = solo si se cumple la condición</text>',
         f'<rect x="{BX}" y="{BY}" width="{BW}" height="{BH}" rx="14" fill="#FFFFFF" stroke="{INK}" stroke-width="2"/>',
         texto(BX + BW - 20, BY + 18, 'Sistema SkyCampus Enterprise', 14, INK, 700, anchor='end')]
    for nombre, y, h, color in PAQUETES:
        s.append(f'<path d="M{PX} {y + 26} V{y + h} H{PX + PW} V{y + 26} H{PX + 250} L{PX + 236} {y} H{PX} Z" '
                 f'fill="{color}" stroke="#8796A8" stroke-width="1.3"/>')
        s.append(texto(PX + 14, y + 14, nombre, 15, INK, 700, anchor='start'))
    for p1, p2, propios in segmentos()[:len(ASOC)]:
        s.append(f'<line x1="{p1[0]:.1f}" y1="{p1[1]:.1f}" x2="{p2[0]:.1f}" y2="{p2[1]:.1f}" stroke="{LINE}" stroke-width="1.4"/>')
    for (o, d, *_), color in [(i, BLUE) for i in INCLUDE] + [(e, AMBER) for e in EXTEND]:
        camino = [t[0] for t in tramos(o, d)] + [tramos(o, d)[-1][1]]
        puntos = " ".join(f"{x:.1f},{y:.1f}" for x, y in camino)
        s.append(f'<polyline points="{puntos}" fill="none" stroke="{color}" '
                 'stroke-width="1.7" stroke-dasharray="7 5" marker-end="url(#abierta)"/>')
    for hijo, padre in GENERALIZACION:
        h, p = act[hijo], act[padre]
        s.append(f'<line x1="{h[2]}" y1="{h[3] - 56}" x2="{p[2]}" y2="{p[3] + 92}" stroke="{LINE}" stroke-width="1.7" marker-end="url(#herencia)"/>')
    for (cid, txt, cx, cy, w, tipo) in CU:
        fill, stroke = ESTILOS[tipo]
        hh = alto(cid)
        s.append(f'<ellipse cx="{cx}" cy="{cy}" rx="{w / 2}" ry="{hh / 2}" fill="{fill}" stroke="{stroke}" stroke-width="1.8"/>')
        if cid in PUNTOS:
            s.append(texto(cx, cy - 10, txt, 13, INK, 700))
            s.append(texto(cx, cy + 22, PUNTOS[cid], 10, MUTED))
        else:
            s.append(texto(cx, cy, txt, 13, INK, 700))
    for (aid, nombre, x, y, tipo) in ACTORES:
        if tipo == 'persona':
            s.append(f'<g stroke="{GREEN}" stroke-width="2.4" fill="none"><circle cx="{x}" cy="{y - 38}" r="14" fill="#fff"/>'
                     f'<line x1="{x}" y1="{y - 24}" x2="{x}" y2="{y + 14}"/><line x1="{x - 24}" y1="{y - 10}" x2="{x + 24}" y2="{y - 10}"/>'
                     f'<line x1="{x}" y1="{y + 14}" x2="{x - 18}" y2="{y + 44}"/><line x1="{x}" y1="{y + 14}" x2="{x + 18}" y2="{y + 44}"/></g>')
            s.append(texto(x, y + 66 + nombre.count('\n') * 8, nombre, 14, GREEN, 700))
        else:
            s.append(f'<rect x="{x - 90}" y="{y - 36}" width="180" height="72" rx="6" fill="#FDF2F1" stroke="{RED}" stroke-width="2"/>')
            s.append(texto(x, y - 16, '«sistema externo»', 11, RED, italic=True))
            s.append(texto(x, y + 10, nombre, 13, RED, 700))

    def etiqueta(x, y, lineas, color):
        ancho = max(len(l) for l in lineas) * 6.3 + 14
        s.append(f'<rect x="{x - ancho / 2:.1f}" y="{y - 11:.1f}" width="{ancho:.1f}" height="{len(lineas) * 14 + 8}" rx="4" fill="#FFFFFF" fill-opacity="0.95"/>')
        for i, l in enumerate(lineas):
            estilo = 'font-weight="700" font-style="italic"' if i == 0 else ''
            s.append(f'<text x="{x}" y="{y + 4 + i * 14:.1f}" font-size="{12 if i == 0 else 11}" fill="{color}" {estilo} text-anchor="middle">{html.escape(l)}</text>')
    for o, d, (x, y), _, _ in INCLUDE:
        etiqueta(x, y, ['«include»'], BLUE)
    for o, d, cond, (x, y) in EXTEND:
        etiqueta(x, y, ['«extend»'] + cond.split('\n'), AMBER)
    ly = H - 22
    s.append(texto(40, ly, 'Herencia: el Coordinador hace todo lo del Operador y además lo suyo; el Superadmin hace todo lo del Coordinador. Blanco = caso base · azul = incluido · ámbar = extensión.', 13, MUTED, anchor='start'))
    s.append('</svg>')
    return '\n'.join(s)


def drawio():
    a = lambda v: html.escape(v, quote=True)
    c = []
    def v(cid, val, style, x, y, w, h):
        c.append(f'<mxCell id="{cid}" value="{a(val)}" style="{style}" vertex="1" parent="1"><mxGeometry x="{x}" y="{y}" width="{w}" height="{h}" as="geometry"/></mxCell>')
    def e(eid, src, tgt, val, style, puntos=()):
        arreglo = ('<Array as="points">' + ''.join(f'<mxPoint x="{x}" y="{y}"/>' for x, y in puntos) + '</Array>') if puntos else ''
        c.append(f'<mxCell id="{eid}" value="{a(val)}" style="{style}" edge="1" parent="1" source="{src}" target="{tgt}"><mxGeometry relative="1" as="geometry">{arreglo}</mxGeometry></mxCell>')
    txt = 'text;html=1;strokeColor=none;fillColor=none;align=left;verticalAlign=middle;whiteSpace=wrap;'
    v('titulo', 'SkyCampus Enterprise — Casos de uso de la red de 4 sedes', txt + f'fontSize=24;fontStyle=1;fontColor={INK};', 40, 26, 1000, 34)
    v('frontera', 'Sistema SkyCampus Enterprise', f'rounded=1;arcSize=1;html=1;whiteSpace=wrap;fillColor=#FFFFFF;strokeColor={INK};strokeWidth=2;verticalAlign=top;align=right;spacingRight=20;spacingTop=4;fontStyle=1;fontSize=14;fontColor={INK};', BX, BY, BW, BH)
    for i, (nombre, y, h, color) in enumerate(PAQUETES):
        v(f'paq{i + 1}', nombre, f'shape=folder;fontStyle=1;spacingTop=4;tabWidth=250;tabHeight=26;tabPosition=left;html=1;whiteSpace=wrap;verticalAlign=top;align=left;spacingLeft=12;fillColor={color};strokeColor=#8796A8;fontSize=15;fontColor={INK};', PX, y, PW, h)
    for (cid, t, cx, cy, w, tipo) in CU:
        fill, stroke = ESTILOS[tipo]
        val = f'<b>{t.replace(chr(10), "<br>")}</b>'
        if cid in PUNTOS:
            val += f'<br><font style="font-size:10px" color="{MUTED}">{PUNTOS[cid]}</font>'
        hh = alto(cid)
        v(cid, val, f'ellipse;whiteSpace=wrap;html=1;fillColor={fill};strokeColor={stroke};strokeWidth=1.8;fontSize=13;fontColor={INK};', cx - w / 2, cy - hh / 2, w, hh)
    for (aid, nombre, x, y, tipo) in ACTORES:
        if tipo == 'persona':
            v(aid, nombre.replace('\n', '<br>'), f'shape=umlActor;verticalLabelPosition=bottom;verticalAlign=top;html=1;outlineConnect=0;strokeColor={GREEN};strokeWidth=2.4;fillColor=#FFFFFF;fontColor={GREEN};fontStyle=1;fontSize=14;', x - 24, y - 52, 48, 96)
        else:
            v(aid, f'<i>«sistema externo»</i><br><b>{nombre.replace(chr(10), "<br>")}</b>', f'rounded=1;arcSize=8;whiteSpace=wrap;html=1;fillColor=#FDF2F1;strokeColor={RED};strokeWidth=2;fontColor={RED};fontSize=13;', x - 90, y - 36, 180, 72)
    for i, (aid, cid) in enumerate(ASOC):
        e(f'asoc{i + 1}', aid, cid, '', f'endArrow=none;html=1;strokeColor={LINE};strokeWidth=1.4;edgeStyle=none;')
    for i, (o, d, _, _, puntos) in enumerate(INCLUDE):
        e(f'inc{i + 1}', o, d, '«include»', f'endArrow=open;endSize=11;html=1;dashed=1;dashPattern=7 5;strokeColor={BLUE};fontColor={BLUE};fontStyle=3;strokeWidth=1.7;edgeStyle=none;labelBackgroundColor=#FFFFFF;', puntos)
    for i, (o, d, cond, _) in enumerate(EXTEND):
        e(f'ext{i + 1}', o, d, f'«extend»<br><span style="font-weight:normal;font-style:normal">{cond.replace(chr(10), "<br>")}</span>', f'endArrow=open;endSize=11;html=1;dashed=1;dashPattern=7 5;strokeColor={AMBER};fontColor={AMBER};fontStyle=3;strokeWidth=1.7;edgeStyle=none;labelBackgroundColor=#FFFFFF;fontSize=11;')
    for i, (hijo, padre) in enumerate(GENERALIZACION):
        e(f'herencia{i + 1}', hijo, padre, '', f'endArrow=block;endFill=0;endSize=16;html=1;strokeColor={LINE};strokeWidth=1.7;edgeStyle=none;')
    pagina = ('<diagram id="skycampus-cu-enterprise" name="Casos de uso Enterprise (Infernape)"><mxGraphModel dx="2000" dy="1640" grid="1" '
              'gridSize="10" guides="1" tooltips="1" connect="1" arrows="1" fold="1" page="1" pageScale="1" '
              f'pageWidth="{W}" pageHeight="{H}" background="#F7F8FA" math="0" shadow="0"><root><mxCell id="0"/><mxCell id="1" parent="0"/>'
              + ''.join(c) + '</root></mxGraphModel></diagram>')
    return '<mxfile host="app.diagrams.net" agent="gen_cu.py" version="24.7.17" type="device">\n' + pagina + '\n</mxfile>\n'


if __name__ == '__main__':
    problemas = cruces()
    (AQUI / 'Diagrama_Casos_Uso_Enterprise.svg').write_text(svg(), encoding='utf-8')
    (AQUI / 'Diagrama_Casos_Uso_Enterprise.drawio').write_text(drawio(), encoding='utf-8')
    print(f'{len(PAQUETES)} paquetes, {len(CU)} casos, {len(INCLUDE)} include, {len(EXTEND)} extend, {len(ASOC)} asociaciones')
    print('cruces línea/caso:', problemas or 'ninguno')
