"""Genera el diagrama de contexto C4 de SkyCampus v2 desde una sola especificación.

Salidas (en la carpeta del script):
  - Diagrama_Contexto_SkyCampus_v2.svg     -> imagen de la v2
  - Diagrama_Contexto_SkyCampus_v2.drawio  -> fuente editable con 2 páginas:
        "Contexto v2 (Monferno)"  (generada aquí, flechas ancladas a sus cajas)
        "Contexto MVP (Chimchar)" (copiada sin cambios de Diagrama_Contexto_SkyCampus_MVP.drawio,
                                   que es el archivo del reto 05 de Chimchar)
Uso, desde esta carpeta:  python gen_c4.py   (no necesita nada fuera de la carpeta)
"""
import html
import re
from pathlib import Path

AQUI = Path(__file__).resolve().parent
MVP_DRAWIO = AQUI / "Diagrama_Contexto_SkyCampus_MVP.drawio"

W, H = 1520, 990
GREEN, BLUE, RED, INK, MUTED, LINE = '#4A8C45', '#2F74B5', '#C0392B', '#172B4D', '#536273', '#5B6676'
AX, AW, AH = 60, 250, 96          # personas
SX, SW, SY, SH = 630, 290, 150, 720  # sistema
EX, EW, EH = 1210, 250, 112       # sistemas externos
BY = 930                          # carril inferior de la ruta Alertas -> Técnico

# (id, nombre, descripción, centro y, nuevo en v2)
PERSONAS = [
    ('op', 'Operador de drones', 'Supervisa la flota y las<br>misiones en vuelo', 205, False),
    ('sol', 'Solicitante', 'Pide repartos entre bloques<br>del campus', 405, False),
    ('adm', 'Admin', 'Configura flota, destinos<br>y política de asignación', 605, False),
    ('tec', 'Técnico de mantenimiento', 'Atiende drones en FALLO<br>y los devuelve al servicio', 805, True),
]
EXTERNOS = [
    ('met', 'API Meteorológica', 'Servicio de clima: viento<br>y lluvia sobre el campus', 265),
    ('ctl', 'Control Aéreo ECI', 'Registra vuelos y autoriza<br>rutas sobre el campus', 505),
    ('ale', 'Sistema de Alertas', 'Envía notificaciones<br>(SMS / correo) al personal', 745),
]
# persona <-> sistema: (hacia el sistema, hacia la persona)
P2S = {
    'op': ('Supervisa y confirma asignaciones;<br>cancela misiones en vuelo',
           'Flota agrupada por estado y avisos<br>(cambios de estado, clima adverso)'),
    'sol': ('Solicitud: origen, destino y paquete<br>(peso, tipo, prioridad)',
            'Código de misión, drone asignado<br>y estado de la entrega'),
    'adm': ('Configura los 20 drones (3 tipos),<br>destinos y política de asignación',
            'Estadísticas de misiones y<br>estado de la configuración'),
    'tec': ('Diagnóstico; marca el drone reparado<br>(FALLO → MANTENIMIENTO → DISPONIBLE)',
            'Drones en FALLO o MANTENIMIENTO<br>pendientes de revisión'),
}
# sistema <-> externo: (hacia el externo, hacia el sistema o None)
S2E = {
    'met': ('Consulta antes de cada despegue<br>(zona del campus, hora)',
            'Condiciones de viento y lluvia:<br>apto / no apto para volar'),
    'ctl': ('Registro de vuelo y solicitud de ruta<br>(drone, origen, destino, hora)',
            'Autorización o rechazo<br>de la ruta'),
    'ale': ('Evento de FALLO<br>(drone, tipo, hora)', None),
}
RUTA_LBL = 'Notificación: "drone D-XX en FALLO"<br>(la recibe el técnico de turno)'


def relaciones():
    """Lista de (y, x1, x2, texto, lado, id_origen, id_destino)."""
    rel = []
    for (pid, _, _, cy, _) in PERSONAS:
        ida, vuelta = P2S[pid]
        rel.append((cy - 14, AX + AW, SX, ida, 'up', pid, 'sis'))
        rel.append((cy + 14, SX, AX + AW, vuelta, 'down', 'sis', pid))
    for (eid, _, _, cy) in EXTERNOS:
        ida, vuelta = S2E[eid]
        if vuelta:
            rel.append((cy - 14, SX + SW, EX, ida, 'up', 'sis', eid))
            rel.append((cy + 14, EX, SX + SW, vuelta, 'down', eid, 'sis'))
        else:
            rel.append((cy, SX + SW, EX, ida, 'up', 'sis', eid))
    return rel


def caja(cid):
    """(x, y, w, h) de cada elemento, para anclar las flechas."""
    for (pid, _, _, cy, _) in PERSONAS:
        if pid == cid:
            return AX, cy - AH / 2, AW, AH
    for (eid, _, _, cy) in EXTERNOS:
        if eid == cid:
            return EX, cy - EH / 2, EW, EH
    return SX, SY, SW, SH


# ------------------------------------------------------------------ SVG
def svg_texto(x, y, s, size=13, color=INK, weight=400, lh=17):
    return ''.join(
        f'<text x="{x}" y="{y + i * lh}" font-size="{size}" fill="{color}" font-weight="{weight}" '
        f'text-anchor="middle">{html.escape(linea)}</text>'
        for i, linea in enumerate(s.split('<br>')))


def svg_insignia(x, y, color):
    return (f'<rect x="{x}" y="{y}" width="64" height="20" rx="10" fill="{color}"/>'
            f'<text x="{x + 32}" y="{y + 14}" font-size="11" font-weight="700" fill="#fff" '
            f'text-anchor="middle">NUEVO</text>')


def generar_svg(rel):
    s = [f'<svg xmlns="http://www.w3.org/2000/svg" width="{W}" height="{H}" viewBox="0 0 {W} {H}" '
         'font-family="Inter, Segoe UI, Arial, sans-serif">',
         f'<defs><marker id="arr" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="9" markerHeight="9" '
         f'orient="auto-start-reverse"><path d="M0,0 L10,5 L0,10 z" fill="{LINE}"/></marker></defs>',
         f'<rect width="{W}" height="{H}" fill="#F6F7F9"/>',
         f'<text x="55" y="52" font-size="26" font-weight="700" fill="{INK}">'
         'SkyCampus v2 — Diagrama de Contexto C4 · Nivel 1</text>',
         f'<text x="57" y="80" font-size="15" fill="{MUTED}">'
         'Monferno · flota autónoma con prioridades · primeros sistemas externos</text>']
    for (_, nombre, desc, cy, nuevo) in PERSONAS:
        top = cy - AH / 2
        s.append(f'<rect x="{AX}" y="{top}" width="{AW}" height="{AH}" rx="22" fill="#fff" '
                 f'stroke="{GREEN}" stroke-width="2"/>')
        s.append(f'<circle cx="{AX + AW / 2}" cy="{top - 20}" r="24" fill="#fff" stroke="{GREEN}" stroke-width="2"/>')
        s.append(svg_texto(AX + AW / 2, top + 28, nombre, 15, GREEN, 700))
        s.append(svg_texto(AX + AW / 2, top + 45, '[Persona]', 11, MUTED))
        s.append(svg_texto(AX + AW / 2, top + 64, desc, 12, '#3B4B5C'))
        if nuevo:
            s.append(svg_insignia(AX + AW - 58, top - 12, GREEN))
    cx = SX + SW / 2
    s.append(f'<rect x="{SX}" y="{SY}" width="{SW}" height="{SH}" rx="14" fill="#EEF5FC" '
             f'stroke="{BLUE}" stroke-width="2.5"/>')
    s.append(svg_texto(cx, SY + 250, 'SISTEMA DE SOFTWARE', 11, BLUE, 700))
    s.append(svg_texto(cx, SY + 290, 'SkyCampus v2', 24, INK, 700))
    s.append(svg_texto(cx, SY + 312, '[Sistema de software]', 12, MUTED))
    s.append(f'<line x1="{SX + 40}" y1="{SY + 330}" x2="{SX + SW - 40}" y2="{SY + 330}" stroke="#B9D3EC"/>')
    s.append(svg_texto(cx, SY + 358, 'Gestión autónoma del reparto<br>en el campus ECI<br><br>'
                       '20 drones · MINI, CARGO, EXPRESS<br>asignación automática por prioridad<br>'
                       'alertas de cambio de estado', 13, '#2B3A4A', lh=19))
    for (_, nombre, desc, cy) in EXTERNOS:
        top = cy - EH / 2
        s.append(f'<rect x="{EX}" y="{top}" width="{EW}" height="{EH}" rx="12" fill="#FDF2F1" '
                 f'stroke="{RED}" stroke-width="2"/>')
        s.append(svg_texto(EX + EW / 2, top + 30, nombre, 15, RED, 700))
        s.append(svg_texto(EX + EW / 2, top + 47, '[Sistema externo]', 11, MUTED))
        s.append(svg_texto(EX + EW / 2, top + 70, desc, 12, '#3B4B5C'))
        s.append(svg_insignia(EX + EW - 58, top - 10, RED))
    for (y, x1, x2, txt, lado, _, _) in rel:
        s.append(f'<line x1="{x1}" y1="{y}" x2="{x2}" y2="{y}" stroke="{LINE}" stroke-width="1.6" '
                 'stroke-dasharray="7 5" marker-end="url(#arr)"/>')
        n = txt.count('<br>') + 1
        ly = y - 10 - (n - 1) * 16 if lado == 'up' else y + 20
        s.append(svg_texto((x1 + x2) / 2, ly, txt, 12, '#2B3A4A', lh=16))
    ale_cy, tec_cy = EXTERNOS[2][3], PERSONAS[3][3]
    ruta = [(EX + EW / 2, ale_cy + EH / 2), (EX + EW / 2, BY), (AX + AW / 2, BY), (AX + AW / 2, tec_cy + AH / 2)]
    s.append(f'<polyline points="{" ".join(f"{x},{y}" for x, y in ruta)}" fill="none" stroke="{LINE}" '
             'stroke-width="1.6" stroke-dasharray="7 5" marker-end="url(#arr)"/>')
    s.append(svg_texto((EX + EW / 2 + AX + AW / 2) / 2 + 250, BY - 26, RUTA_LBL, 12, '#2B3A4A', lh=16))
    lx, ly = 905, 34
    s.append(f'<rect x="{lx}" y="{ly}" width="560" height="62" rx="8" fill="#fff" stroke="#D5DAE1"/>')
    s.append(f'<circle cx="{lx + 22}" cy="{ly + 20}" r="7" fill="#fff" stroke="{GREEN}" stroke-width="2"/>'
             f'<text x="{lx + 36}" y="{ly + 24}" font-size="12" fill="{INK}">Persona</text>')
    s.append(f'<rect x="{lx + 100}" y="{ly + 12}" width="18" height="14" rx="3" fill="#EEF5FC" stroke="{BLUE}" '
             f'stroke-width="2"/><text x="{lx + 125}" y="{ly + 24}" font-size="12" fill="{INK}">'
             'Sistema que se construye</text>')
    s.append(f'<rect x="{lx + 290}" y="{ly + 12}" width="18" height="14" rx="3" fill="#FDF2F1" stroke="{RED}" '
             f'stroke-width="2"/><text x="{lx + 315}" y="{ly + 24}" font-size="12" fill="{INK}">'
             'Sistema externo existente</text>')
    s.append(f'<line x1="{lx + 16}" y1="{ly + 45}" x2="{lx + 60}" y2="{ly + 45}" stroke="{LINE}" '
             'stroke-width="1.6" stroke-dasharray="7 5" marker-end="url(#arr)"/>'
             f'<text x="{lx + 70}" y="{ly + 49}" font-size="12" fill="{INK}">'
             'Relación: qué datos fluyen y hacia dónde</text>')
    s.append(svg_insignia(lx + 330, ly + 35, '#6B7785'))
    s.append(f'<text x="{lx + 402}" y="{ly + 49}" font-size="12" fill="{INK}">no existía en el MVP</text>')
    s.append('</svg>')
    return '\n'.join(s)


# ------------------------------------------------------------------ draw.io
def a(v):
    return html.escape(v, quote=True)


def generar_pagina_v2(rel):
    c = []
    txt = 'text;html=1;whiteSpace=wrap;strokeColor=none;fillColor=none;'

    def vertice(cid, val, style, x, y, w, h, parent='1'):
        c.append(f'<mxCell id="{cid}" value="{a(val)}" style="{style}" vertex="1" parent="{parent}">'
                 f'<mxGeometry x="{x}" y="{y}" width="{w}" height="{h}" as="geometry"/></mxCell>')

    vertice('titulo', 'SkyCampus v2 — Diagrama de Contexto C4 · Nivel 1',
            txt + f'align=left;fontSize=24;fontStyle=1;fontColor={INK};', 55, 24, 900, 38)
    vertice('subtitulo', 'Monferno · flota autónoma con prioridades · primeros sistemas externos',
            txt + f'align=left;fontSize=14;fontColor={MUTED};', 57, 62, 800, 26)
    for (pid, nombre, desc, cy, nuevo) in PERSONAS:
        top = cy - AH / 2
        vertice(pid, f'<b>{nombre}</b><br><font style="font-size:11px" color="{MUTED}">[Persona]</font><br>'
                     f'<font style="font-size:12px" color="#3B4B5C">{desc}</font>',
                f'shape=mxgraph.c4.person2;whiteSpace=wrap;html=1;align=center;verticalAlign=bottom;'
                f'spacingBottom=6;fillColor=#FFFFFF;strokeColor={GREEN};strokeWidth=2;fontColor={GREEN};fontSize=14;',
                AX, top - 44, AW, AH + 44)
        if nuevo:
            vertice(pid + '-nuevo', 'NUEVO', f'rounded=1;arcSize=50;html=1;fillColor={GREEN};strokeColor=none;'
                    'fontColor=#FFFFFF;fontSize=11;fontStyle=1;', AW - 58, 32, 64, 20, parent=pid)
    vertice('sis', f'<font style="font-size:11px" color="{BLUE}"><b>SISTEMA DE SOFTWARE</b></font><br><br>'
                   f'<b><font style="font-size:24px">SkyCampus v2</font></b><br>'
                   f'<font style="font-size:12px" color="{MUTED}">[Sistema de software]</font><br><br>'
                   'Gestión autónoma del reparto en el campus ECI<br><br>20 drones · MINI, CARGO, EXPRESS<br>'
                   'asignación automática por prioridad<br>alertas de cambio de estado',
            f'rounded=1;arcSize=5;whiteSpace=wrap;html=1;fillColor=#EEF5FC;strokeColor={BLUE};strokeWidth=2.5;'
            f'fontColor={INK};fontSize=13;', SX, SY, SW, SH)
    for (eid, nombre, desc, cy) in EXTERNOS:
        vertice(eid, f'<b>{nombre}</b><br><font style="font-size:11px" color="{MUTED}">[Sistema externo]</font><br>'
                     f'<font style="font-size:12px" color="#3B4B5C">{desc}</font>',
                f'rounded=1;arcSize=12;whiteSpace=wrap;html=1;fillColor=#FDF2F1;strokeColor={RED};strokeWidth=2;'
                f'fontColor={RED};fontSize=14;', EX, cy - EH / 2, EW, EH)
        vertice(eid + '-nuevo', 'NUEVO', f'rounded=1;arcSize=50;html=1;fillColor={RED};strokeColor=none;'
                'fontColor=#FFFFFF;fontSize=11;fontStyle=1;', EW - 58, -10, 64, 20, parent=eid)
    estilo = (f'endArrow=block;endFill=1;html=1;dashed=1;dashPattern=7 5;strokeColor={LINE};strokeWidth=1.6;'
              'fontSize=12;fontColor=#2B3A4A;labelBackgroundColor=none;edgeStyle=none;')

    def ancla(cid, y, lado_x):
        x, top, w, h = caja(cid)
        if cid in [p[0] for p in PERSONAS]:   # la figura person2 incluye la cabeza (44 px)
            top, h = top - 44, h + 44
        return lado_x, round((y - top) / h, 4)

    for i, (y, x1, x2, txt_rel, lado, ori, des) in enumerate(rel):
        ex, ey = ancla(ori, y, 1 if x1 < x2 else 0)
        nx, ny = ancla(des, y, 0 if x1 < x2 else 1)
        n = txt_rel.count('<br>') + 1
        off = -(n * 8 + 8) if lado == 'up' else n * 8 + 8
        c.append(f'<mxCell id="rel{i + 1}" value="{a(txt_rel)}" style="{estilo}exitX={ex};exitY={ey};exitDx=0;'
                 f'exitDy=0;entryX={nx};entryY={ny};entryDx=0;entryDy=0;" edge="1" parent="1" source="{ori}" '
                 f'target="{des}"><mxGeometry relative="1" as="geometry"><mxPoint x="0" y="{off}" as="offset"/>'
                 '</mxGeometry></mxCell>')
    c.append(f'<mxCell id="rel-alertas-tecnico" value="{a(RUTA_LBL)}" style="{estilo}exitX=0.5;exitY=1;exitDx=0;'
             'exitDy=0;entryX=0.5;entryY=1;entryDx=0;entryDy=0;" edge="1" parent="1" source="ale" target="tec">'
             f'<mxGeometry relative="1" as="geometry"><mxPoint x="250" y="-26" as="offset"/>'
             f'<Array as="points"><mxPoint x="{EX + EW / 2}" y="{BY}"/><mxPoint x="{AX + AW / 2}" y="{BY}"/>'
             '</Array></mxGeometry></mxCell>')
    return ('<diagram id="skycampus-contexto-v2" name="Contexto v2 (Monferno)"><mxGraphModel dx="1520" dy="990" '
            'grid="1" gridSize="10" guides="1" tooltips="1" connect="1" arrows="1" fold="1" page="1" pageScale="1" '
            f'pageWidth="{W}" pageHeight="{H}" math="0" shadow="0"><root><mxCell id="0"/><mxCell id="1" parent="0"/>'
            + ''.join(c) + '</root></mxGraphModel></diagram>')


def pagina_mvp():
    fuente = MVP_DRAWIO.read_text(encoding='utf-8')
    pagina = re.search(r'<diagram .*?</diagram>', fuente, re.S).group(0)
    return re.sub(r'name="[^"]*"', 'name="Contexto MVP (Chimchar)"', pagina, count=1)


if __name__ == '__main__':
    rel = relaciones()
    (AQUI / 'Diagrama_Contexto_SkyCampus_v2.svg').write_text(generar_svg(rel), encoding='utf-8')
    drawio = ('<mxfile host="app.diagrams.net" agent="gen_c4.py" version="24.7.17" type="device">\n'
              + generar_pagina_v2(rel) + '\n' + pagina_mvp() + '\n</mxfile>\n')
    (AQUI / 'Diagrama_Contexto_SkyCampus_v2.drawio').write_text(drawio, encoding='utf-8')
    print(f'{len(rel) + 1} relaciones; SVG y .drawio (2 páginas) generados')
