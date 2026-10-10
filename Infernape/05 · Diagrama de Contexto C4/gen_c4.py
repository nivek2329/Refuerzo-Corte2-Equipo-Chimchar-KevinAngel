"""Genera los SVG Enterprise y un documento draw.io con los cuatro diagramas C4."""

from copy import deepcopy
from pathlib import Path
import shutil
import xml.etree.ElementTree as ET


HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[1]
CHIMCHAR = ROOT / "05 · Diagrama de Contexto C4"
MONFERNO = ROOT / "Monferno" / "05 · Diagrama de Contexto C4"
INK = "#163A59"
BLUE = "#1479B8"
BLUE_LIGHT = "#EAF5FB"
AMBER = "#D49A25"
AMBER_LIGHT = "#FFF5DE"
RED = "#C74747"
RED_LIGHT = "#FCEEEE"
GREEN = "#47894A"
GREEN_LIGHT = "#EFF8ED"
PAPER = "#FBF9F2"
GRAY = "#64717C"


def person(x, y, w, name, tag, desc):
    """Persona C4: cabeza circular sobre un cuerpo redondeado (notación vista en clase)."""
    cx = x + w // 2
    return (f'<circle cx="{cx}" cy="{y+16}" r="17" fill="{GREEN_LIGHT}" stroke="{GREEN}" stroke-width="3"/>'
            f'<rect x="{x}" y="{y+34}" width="{w}" height="74" rx="14" fill="{GREEN_LIGHT}" stroke="{GREEN}" stroke-width="3"/>'
            f'<text x="{cx}" y="{y+61}" text-anchor="middle" font-family="Arial" font-size="16" font-weight="700" fill="{GREEN}">{name}</text>'
            f'<text x="{cx}" y="{y+81}" text-anchor="middle" font-family="Arial" font-size="12" fill="{INK}">[Persona · {tag}]</text>'
            f'<text x="{cx}" y="{y+99}" text-anchor="middle" font-family="Arial" font-size="11" fill="{GRAY}">{desc}</text>')


def label(x, y, text, anchor="middle", size=12):
    """Rótulo de relación con halo del color del papel para que nunca se lea sobre una línea."""
    return (f'<text x="{x}" y="{y}" text-anchor="{anchor}" font-family="Arial" font-size="{size}" fill="{INK}" '
            f'stroke="{PAPER}" stroke-width="5" paint-order="stroke">{text}</text>')


def box(x, y, w, h, color, fill, title, tech, desc, title_size=16):
    cx = x + w / 2
    return (f'<rect x="{x}" y="{y}" width="{w}" height="{h}" rx="14" fill="{fill}" stroke="{color}" stroke-width="3"/>'
            f'<text x="{cx}" y="{y + h/2 - 14}" text-anchor="middle" font-family="Arial" font-size="{title_size}" font-weight="700" fill="{color}">{title}</text>'
            f'<text x="{cx}" y="{y + h/2 + 8}" text-anchor="middle" font-family="Arial" font-size="12" fill="{INK}">[{tech}]</text>'
            f'<text x="{cx}" y="{y + h/2 + 27}" text-anchor="middle" font-family="Arial" font-size="11" fill="{GRAY}">{desc}</text>')


MARKERS = (f'<defs><marker id="arrow" markerWidth="10" markerHeight="10" refX="9" refY="5" orient="auto-start-reverse">'
           f'<path d="M0 0L10 5L0 10z" fill="{GRAY}"/></marker></defs>')


def line(points, both=False, dashed=True):
    d = "M" + " L".join(f"{x} {y}" for x, y in points)
    dash = ' stroke-dasharray="7 6"' if dashed else ''
    start = ' marker-start="url(#arrow)"' if both else ''
    return f'<path d="{d}" fill="none" stroke="{GRAY}" stroke-width="2"{dash}{start} marker-end="url(#arrow)"/>'


def svg_context():
    width, height = 2100, 1180
    actors = [
        ("Operador de drones", "Supervisa operaciones y misiones", "MVP", "Comandos operativos ↔ estado en vivo"),
        ("Solicitante", "Solicita entregas y consulta su estado", "MVP", "Solicitud de entrega ↔ seguimiento"),
        ("Administrador", "Configura la operación de su sede", "MVP", "Parámetros de sede ↔ configuración"),
        ("Técnico de mantenimiento", "Atiende fallos y libera drones", "Monferno", "Fallos y diagnóstico ↔ estado técnico"),
        ("Superadmin", "Administra la red completa", "NUEVO", "Políticas y consultas ↔ métricas de red"),
        ("Coordinador de sede", "Configura límites locales", "NUEVO", "Radio máximo ↔ configuración aplicada"),
    ]
    external = [
        ("API Meteorológica", "Viento y lluvia", "existente · Monferno", "Zona/hora ↔ condiciones de vuelo", True),
        ("Control Aéreo ECI", "Registro y rutas locales", "existente · Monferno", "Plan de vuelo ↔ autorización local", True),
        ("Sistema de Alertas", "Notificaciones de fallos", "existente · Monferno", "Evento FALLO → notificación técnica", False),
        ("Aerocivil", "Autorización del espacio aéreo", "NUEVO", "Ruta propuesta ↔ restricciones nacionales", True),
        ("ERP universitario", "Datos institucionales", "NUEVO · relación propuesta*", "Identidad y catálogos ↔ datos (propuesto)", True),
        ("Plataforma Analytics", "Analítica agregada de la red", "NUEVO", "Métricas agregadas → plataforma", False),
    ]
    sys_x, sys_y, sys_w, sys_h = 760, 190, 520, 860
    parts = [f'<svg xmlns="http://www.w3.org/2000/svg" width="{width}" height="{height}" viewBox="0 0 {width} {height}">', MARKERS,
             f'<rect width="100%" height="100%" fill="{PAPER}"/>'
             f'<text x="50" y="56" font-family="Arial" font-size="30" font-weight="700" fill="{INK}">SkyCampus Enterprise · C4 nivel 1</text>'
             f'<text x="50" y="88" font-family="Arial" font-size="16" fill="{GRAY}">Contexto de la red universitaria · personas, sistema en construcción y sistemas externos</text>'
             f'<rect x="30" y="112" width="2040" height="1000" rx="18" fill="none" stroke="#C8C4B8" stroke-dasharray="8 7"/>'
             f'<text x="52" y="143" font-family="Arial" font-size="13" fill="{GRAY}">ÁMBITO ORGANIZACIONAL: RED DE SEDES UNIVERSITARIAS (ECI · UNAL · UNIANDES · EAFIT)</text>']
    # Relaciones rectas y horizontales: cada caja está a la altura de un tramo del sistema, así nada se cruza.
    for index, (name, desc, tag, relation) in enumerate(actors):
        cy = 160 + index * 150 + 71
        parts.append(line([(335, cy), (sys_x, cy)], both=True))
        parts.append(label((335 + sys_x) // 2, cy - 10, relation))
    for index, (name, desc, tag, relation, both) in enumerate(external):
        cy = 180 + index * 150 + 49
        parts.append(line([(sys_x + sys_w, cy), (1530, cy)], both=both))
        parts.append(label((sys_x + sys_w + 1530) // 2, cy - 10, relation))
    for index, (name, desc, tag, relation) in enumerate(actors):
        parts.append(person(55, 160 + index * 150, 280, name, tag, desc))
    cx = sys_x + sys_w // 2
    parts.append(f'<rect x="{sys_x}" y="{sys_y}" width="{sys_w}" height="{sys_h}" rx="24" fill="{BLUE_LIGHT}" stroke="{BLUE}" stroke-width="5"/>'
                 f'<text x="{cx}" y="560" text-anchor="middle" font-family="Arial" font-size="28" font-weight="700" fill="{BLUE}">SkyCampus Enterprise</text>'
                 f'<text x="{cx}" y="596" text-anchor="middle" font-family="Arial" font-size="16" fill="{INK}">[Sistema de software · en construcción]</text>'
                 f'<line x1="{sys_x+60}" y1="622" x2="{sys_x+sys_w-60}" y2="622" stroke="{BLUE}" opacity=".35"/>'
                 f'<text x="{cx}" y="660" text-anchor="middle" font-family="Arial" font-size="15" fill="{INK}">Red de 100 drones (5 tipos) entre 4 sedes</text>'
                 f'<text x="{cx}" y="688" text-anchor="middle" font-family="Arial" font-size="15" fill="{INK}">misiones · flota compartida · rutas multi-etapa</text>'
                 f'<text x="{cx}" y="716" text-anchor="middle" font-family="Arial" font-size="15" fill="{INK}">estaciones de carga · analítica por sede</text>')
    for index, (name, desc, tag, relation, both) in enumerate(external):
        parts.append(box(1530, 180 + index * 150, 510, 98, RED, RED_LIGHT, name, f"Sistema externo · {tag}", desc, 17))
    parts.append(f'<rect x="55" y="1132" width="20" height="14" fill="{GREEN_LIGHT}" stroke="{GREEN}" stroke-width="2"/><text x="82" y="1145" font-family="Arial" font-size="12" fill="{INK}">Persona</text>'
                 f'<rect x="190" y="1132" width="20" height="14" fill="{BLUE_LIGHT}" stroke="{BLUE}" stroke-width="2"/><text x="217" y="1145" font-family="Arial" font-size="12" fill="{INK}">Sistema en construcción</text>'
                 f'<rect x="400" y="1132" width="20" height="14" fill="{RED_LIGHT}" stroke="{RED}" stroke-width="2"/><text x="427" y="1145" font-family="Arial" font-size="12" fill="{INK}">Sistema externo (la etiqueta dice si es existente o nuevo)</text>'
                 f'{line([(830, 1139), (890, 1139)], both=True)}<text x="900" y="1145" font-family="Arial" font-size="12" fill="{INK}">Intercambio en ambos sentidos</text>'
                 f'{line([(1130, 1139), (1190, 1139)])}<text x="1200" y="1145" font-family="Arial" font-size="12" fill="{INK}">Flujo en un sentido</text>'
                 f'<text x="1380" y="1145" font-family="Arial" font-size="12" fill="{GRAY}">*El enunciado nombra el ERP pero no define su contrato.</text></svg>')
    (HERE / "Contexto_Enterprise_Nivel_1.svg").write_text("\n".join(parts), encoding="utf-8")


# Nivel 2: coordenadas compartidas por el SVG y la página draw.io.
L2_NODES = [
    # id, x, y, w, h, tipo, título, tecnología, descripción
    ("staff", 40, 250, 250, 110, "person", "Personal de sede", "Persona", "Operadores, técnicos y coordinadores"),
    ("superadmin", 40, 540, 250, 110, "person", "Superadmin", "Persona", "Administra la red completa"),
    ("web", 380, 230, 280, 130, "app", "App Web Operadores", "Contenedor · navegador", "Una instancia por sede"),
    ("panel", 380, 520, 280, 130, "app", "Panel Superadmin", "Contenedor · aplicación web", "Gestión de la red completa"),
    ("gateway", 760, 380, 280, 140, "service", "API Gateway", "Contenedor · gateway", "Autenticación y enrutamiento"),
    ("missions", 1150, 165, 290, 130, "service", "Servicio de Misiones", "Contenedor · servicio REST", "Ciclo de vida y asignación"),
    ("fleet", 1150, 395, 290, 130, "service", "Servicio de Flota", "Contenedor · servicio REST", "Estado y transferencia de drones"),
    ("routes", 1150, 600, 290, 230, "service", "Servicio de Rutas", "Contenedor · servicio", "Multi-etapa, estaciones y espacio aéreo"),
    ("analytics", 1150, 900, 290, 130, "service", "Servicio de Analytics", "Contenedor · servicio REST", "Métricas por sede"),
    ("dbm", 1500, 165, 190, 90, "db", "BD Misiones", "Relacional", "Misiones e historial"),
    ("dbf", 1500, 395, 190, 90, "db", "BD Flota", "Relacional", "Drones y estaciones"),
    ("alerts", 1800, 475, 420, 70, "ext", "Sistema de Alertas", "Externo · existente", "Avisos de FALLO al técnico"),
    ("control", 1800, 605, 420, 70, "ext", "Control Aéreo ECI", "Externo · existente", "Rutas sobre el campus"),
    ("weather", 1800, 685, 420, 70, "ext", "API Meteorológica", "Externo · existente", "Viento y lluvia"),
    ("aero", 1800, 765, 420, 70, "ext", "Aerocivil", "Externo · nuevo", "Restricciones del espacio aéreo"),
    ("platform", 1800, 930, 420, 70, "ext", "Plataforma Analytics", "Externo · nuevo", "Analítica agregada"),
    ("erp", 780, 1130, 240, 80, "ext", "ERP universitario", "Externo · nuevo · propuesto", "Identidad y catálogos"),
]
# id, puntos de la polilínea, rótulo, posición del rótulo (x, y, anchor), discontinua
L2_LINKS = [
    ("staff-web", "staff", "web", [(290, 305), (380, 305)], "HTTPS", (335, 295, "middle"), False),
    ("admin-panel", "superadmin", "panel", [(290, 595), (380, 595)], "HTTPS", (335, 585, "middle"), False),
    ("web-gateway", "web", "gateway", [(660, 295), (710, 295), (710, 420), (760, 420)], "HTTPS", (718, 360, "start"), False),
    ("panel-gateway", "panel", "gateway", [(660, 585), (710, 585), (710, 480), (760, 480)], "HTTPS", (718, 540, "start"), False),
    ("gateway-missions", "gateway", "missions", [(1040, 450), (1095, 450), (1095, 230), (1150, 230)], "REST", (1100, 220, "start"), False),
    ("gateway-fleet", "gateway", "fleet", [(1040, 450), (1150, 450)], "REST", (1100, 442, "start"), False),
    ("gateway-routes", "gateway", "routes", [(1040, 450), (1095, 450), (1095, 715), (1150, 715)], "REST", (1100, 705, "start"), False),
    ("gateway-analytics", "gateway", "analytics", [(1040, 450), (1095, 450), (1095, 965), (1150, 965)], "REST", (1100, 955, "start"), False),
    ("missions-db", "missions", "dbm", [(1440, 210), (1500, 210)], "JDBC", (1470, 200, "middle"), False),
    ("fleet-db", "fleet", "dbf", [(1440, 440), (1500, 440)], "JDBC", (1470, 430, "middle"), False),
    ("fleet-alerts", "fleet", "alerts", [(1440, 512), (1800, 512)], "REST · evento FALLO", (1620, 533, "middle"), False),
    ("routes-control", "routes", "control", [(1440, 640), (1800, 640)], "HTTP · plan de vuelo local", (1620, 630, "middle"), False),
    ("routes-weather", "routes", "weather", [(1440, 720), (1800, 720)], "HTTP · condiciones", (1620, 710, "middle"), False),
    ("routes-aero", "routes", "aero", [(1440, 800), (1800, 800)], "HTTP · autorización inter-sede", (1620, 790, "middle"), False),
    ("analytics-platform", "analytics", "platform", [(1440, 965), (1800, 965)], "REST · métricas agregadas", (1620, 955, "middle"), False),
    ("gateway-erp", "gateway", "erp", [(900, 520), (900, 1130)], "REST · identidad y catálogos (propuesto)", (912, 1100, "start"), True),
]
L2_STYLE = {"person": (GREEN, GREEN_LIGHT), "app": (BLUE, BLUE_LIGHT), "service": (AMBER, AMBER_LIGHT),
            "db": (BLUE, BLUE_LIGHT), "ext": (RED, RED_LIGHT)}


def svg_containers():
    width, height = 2260, 1300
    parts = [f'<svg xmlns="http://www.w3.org/2000/svg" width="{width}" height="{height}" viewBox="0 0 {width} {height}">', MARKERS,
             f'<rect width="100%" height="100%" fill="{PAPER}"/>'
             f'<text x="40" y="52" font-family="Arial" font-size="29" font-weight="700" fill="{INK}">SkyCampus Enterprise · C4 nivel 2</text>'
             f'<text x="40" y="83" font-family="Arial" font-size="15" fill="{GRAY}">Contenedores, responsabilidades y protocolos · las personas y los sistemas externos son los mismos del nivel 1</text>'
             f'<rect x="330" y="120" width="1400" height="960" rx="20" fill="none" stroke="{BLUE}" stroke-width="4" stroke-dasharray="10 6"/>'
             f'<text x="355" y="152" font-family="Arial" font-size="14" font-weight="700" fill="{BLUE}">SKYCAMPUS ENTERPRISE · [Sistema de software en construcción]</text>']
    for _, _, _, points, text, (lx, ly, anchor), dashed in L2_LINKS:
        parts.append(line(points, dashed=dashed))
    for node_id, x, y, w, h, kind, title, tech, desc in L2_NODES:
        color, fill = L2_STYLE[kind]
        if kind == "person":
            parts.append(person(x, y - 34, w, title, "sede" if node_id == "staff" else "red", desc))
        elif kind == "db":
            parts.append(f'<ellipse cx="{x+w/2}" cy="{y+10}" rx="{w/2}" ry="12" fill="{fill}" stroke="{color}" stroke-width="3"/>'
                         f'<path d="M{x} {y+10} V{y+h-10} A{w/2} 12 0 0 0 {x+w} {y+h-10} V{y+10}" fill="{fill}" stroke="{color}" stroke-width="3"/>'
                         f'<ellipse cx="{x+w/2}" cy="{y+10}" rx="{w/2}" ry="12" fill="{fill}" stroke="{color}" stroke-width="3"/>'
                         f'<text x="{x+w/2}" y="{y+46}" text-anchor="middle" font-family="Arial" font-size="15" font-weight="700" fill="{color}">{title}</text>'
                         f'<text x="{x+w/2}" y="{y+66}" text-anchor="middle" font-family="Arial" font-size="11" fill="{INK}">[{tech}] {desc}</text>')
        else:
            parts.append(box(x, y, w, h, color, fill, title, tech, desc, 15 if kind == "ext" else 16))
    for _, _, _, points, text, (lx, ly, anchor), dashed in L2_LINKS:
        parts.append(label(lx, ly, text, anchor, 11))
    parts.append(f'<rect x="40" y="1250" width="18" height="14" fill="{GREEN_LIGHT}" stroke="{GREEN}" stroke-width="2"/><text x="65" y="1262" font-family="Arial" font-size="11" fill="{INK}">Persona</text>'
                 f'<rect x="150" y="1250" width="18" height="14" fill="{BLUE_LIGHT}" stroke="{BLUE}" stroke-width="2"/><text x="175" y="1262" font-family="Arial" font-size="11" fill="{INK}">Aplicación / base de datos</text>'
                 f'<rect x="360" y="1250" width="18" height="14" fill="{AMBER_LIGHT}" stroke="{AMBER}" stroke-width="2"/><text x="385" y="1262" font-family="Arial" font-size="11" fill="{INK}">Servicio</text>'
                 f'<rect x="470" y="1250" width="18" height="14" fill="{RED_LIGHT}" stroke="{RED}" stroke-width="2"/><text x="495" y="1262" font-family="Arial" font-size="11" fill="{INK}">Sistema externo</text>'
                 f'{line([(620, 1257), (680, 1257)], dashed=False)}<text x="690" y="1262" font-family="Arial" font-size="11" fill="{INK}">Llamada (protocolo en el rótulo)</text>'
                 f'{line([(900, 1257), (960, 1257)])}<text x="970" y="1262" font-family="Arial" font-size="11" fill="{INK}">Integración propuesta: el enunciado no define el contrato del ERP</text></svg>')
    (HERE / "Contenedores_Enterprise_Nivel_2.svg").write_text("\n".join(parts), encoding="utf-8")


def drawio_page(name, title, subtitle, width, height, nodes, edges):
    """nodes: (id, valor, x, y, w, h, relleno, borde, forma). edges: (id, origen, destino, rótulo, ambos, puntos, discontinua)."""
    diagram = ET.Element("diagram", {"id": name.lower().replace(" ", "-"), "name": name})
    model = ET.SubElement(diagram, "mxGraphModel", {"dx": str(width), "dy": str(height), "grid": "1", "gridSize": "10", "guides": "1", "tooltips": "1", "connect": "1", "arrows": "1", "fold": "1", "page": "1", "pageScale": "1", "pageWidth": str(width), "pageHeight": str(height)})
    root = ET.SubElement(model, "root")
    ET.SubElement(root, "mxCell", {"id": "0"})
    ET.SubElement(root, "mxCell", {"id": "1", "parent": "0"})
    geometry = {}

    def vertex(cell_id, value, x, y, w, h, fill, stroke, shape="rounded=1"):
        geometry[cell_id] = (x, y, w, h)
        style = f"{shape};whiteSpace=wrap;html=1;fillColor={fill};strokeColor={stroke};strokeWidth=2;fontColor={INK};fontSize=14;align=center;verticalAlign=middle;"
        cell = ET.SubElement(root, "mxCell", {"id": cell_id, "value": value, "style": style, "vertex": "1", "parent": "1"})
        ET.SubElement(cell, "mxGeometry", {"x": str(x), "y": str(y), "width": str(w), "height": str(h), "as": "geometry"})

    vertex("page-title", f"<b>{title}</b><br><font style='font-size:12px'>{subtitle}</font>", 40, 20, width-80, 55, "#FBF9F2", "#FBF9F2", "text;strokeColor=none")
    for node in nodes:
        vertex(*node)
    for edge_id, source, target, text, both, points, dashed in edges:
        arrows = "endArrow=block;endFill=1;" + ("startArrow=block;startFill=1;" if both else "startArrow=none;")
        sx, sy, sw, sh = geometry[source]
        tx, ty, tw, th = geometry[target]
        (px, py), (qx, qy) = points[0], points[-1]
        anchors = (f"exitX={(px-sx)/sw:.4f};exitY={(py-sy)/sh:.4f};exitDx=0;exitDy=0;"
                   f"entryX={(qx-tx)/tw:.4f};entryY={(qy-ty)/th:.4f};entryDx=0;entryDy=0;")
        style = (f"edgeStyle=none;rounded=0;html=1;{arrows}{anchors}dashed={1 if dashed else 0};strokeColor={GRAY};"
                 f"fontSize=11;fontColor={INK};labelBackgroundColor=#FBF9F2;")
        cell = ET.SubElement(root, "mxCell", {"id": edge_id, "value": text, "style": style, "edge": "1", "parent": "1", "source": source, "target": target})
        geo = ET.SubElement(cell, "mxGeometry", {"relative": "1", "as": "geometry"})
        if len(points) > 2:
            array = ET.SubElement(geo, "Array", {"as": "points"})
            for x, y in points[1:-1]:
                ET.SubElement(array, "mxPoint", {"x": str(x), "y": str(y)})
    return diagram


L1_ACTORS = [("operator", "Operador de drones", "MVP", "Comandos operativos ↔ estado en vivo"),
             ("requester", "Solicitante", "MVP", "Solicitud de entrega ↔ seguimiento"),
             ("admin", "Administrador", "MVP", "Parámetros de sede ↔ configuración"),
             ("tech", "Técnico de mantenimiento", "Monferno", "Fallos y diagnóstico ↔ estado técnico"),
             ("superadmin", "Superadmin", "NUEVO", "Políticas y consultas ↔ métricas de red"),
             ("coordinator", "Coordinador de sede", "NUEVO", "Radio máximo ↔ configuración aplicada")]
L1_EXTERNAL = [("climate", "API Meteorológica", "existente · Monferno", "Zona/hora ↔ condiciones de vuelo", True),
               ("control", "Control Aéreo ECI", "existente · Monferno", "Plan de vuelo ↔ autorización local", True),
               ("alerts", "Sistema de Alertas", "existente · Monferno", "Evento FALLO → notificación técnica", False),
               ("aerocivil", "Aerocivil", "NUEVO", "Ruta propuesta ↔ restricciones nacionales", True),
               ("erp", "ERP universitario", "NUEVO · relación propuesta*", "Identidad y catálogos ↔ datos (propuesto)", True),
               ("analytics", "Plataforma Analytics", "NUEVO", "Métricas agregadas → plataforma", False)]


def generate_drawio():
    original = ET.parse(MONFERNO / "Diagrama_Contexto_SkyCampus_v2.drawio").getroot()
    pages = list(original.findall("diagram"))
    # Las páginas aprobadas de Chimchar y Monferno se conservan sin cambios; las de Enterprise usan las coordenadas del SVG.
    nodes, edges = [], []
    nodes.append(("system", "<b>SkyCampus Enterprise</b><br>[Sistema de software · en construcción]<br><br>Red de 100 drones (5 tipos) entre 4 sedes<br>misiones · flota compartida · rutas multi-etapa<br>estaciones de carga · analítica por sede",
                  760, 190, 520, 860, BLUE_LIGHT, BLUE, "rounded=1;arcSize=4"))
    for index, (cell, name, tag, relation) in enumerate(L1_ACTORS):
        y = 160 + index * 150
        nodes.append((cell, f"<b>{name}</b><br>[Persona · {tag}]", 55, y + 34, 280, 74, GREEN_LIGHT, GREEN, "shape=mxgraph.c4.person2"))
        cy = y + 71
        edges.append((f"rel-{cell}", cell, "system", relation, True, [(335, cy), (760, cy)], True))
    for index, (cell, name, tag, relation, both) in enumerate(L1_EXTERNAL):
        y = 180 + index * 150
        nodes.append((cell, f"<b>{name}</b><br>[Sistema externo · {tag}]", 1530, y, 510, 98, RED_LIGHT, RED, "rounded=1"))
        edges.append((f"rel-{cell}", "system", cell, relation, both, [(1280, y + 49), (1530, y + 49)], True))
    pages.append(drawio_page("Enterprise · Contexto L1", "SkyCampus Enterprise — Contexto C4 nivel 1",
                             "*El enunciado nombra el ERP pero no define su contrato", 2100, 1180, nodes, edges))

    shapes = {"person": "shape=mxgraph.c4.person2", "app": "rounded=1", "service": "rounded=1",
              "db": "shape=cylinder3;boundedLbl=1;size=12", "ext": "rounded=1"}
    l2_nodes = [("scope", "", 330, 120, 1400, 960, "none", BLUE, "rounded=1;arcSize=2;dashed=1;strokeWidth=3")]
    l2_nodes.append(("scope-label", f"<b><font color='{BLUE}'>SKYCAMPUS ENTERPRISE · [Sistema de software en construcción]</font></b>",
                     350, 130, 600, 30, "none", "none", "text;align=left"))
    for node_id, x, y, w, h, kind, title, tech, desc in L2_NODES:
        color, fill = L2_STYLE[kind]
        l2_nodes.append((node_id, f"<b>{title}</b><br>[{tech}]<br><font style='font-size:11px'>{desc}</font>", x, y, w, h, fill, color, shapes[kind]))
    l2_edges = [(edge_id, source, target, text, False, points, dashed)
                for edge_id, source, target, points, text, _, dashed in L2_LINKS]
    pages.append(drawio_page("Enterprise · Contenedores L2", "SkyCampus Enterprise — Contenedores C4 nivel 2",
                             "Personas y sistemas externos iguales al nivel 1; el ERP es una integración propuesta", 2260, 1300, l2_nodes, l2_edges))
    out = ET.Element("mxfile", {"host": "app.diagrams.net", "agent": "SkyCampus Enterprise C4", "version": "24.7.17", "type": "device"})
    out.extend(pages)
    ET.indent(out, space="  ")
    ET.ElementTree(out).write(HERE / "SkyCampus_Enterprise_C4.drawio", encoding="utf-8", xml_declaration=True)


def main():
    shutil.copy2(CHIMCHAR / "Diagrama_Contexto_SkyCampus.svg", HERE / "Contexto_Chimchar.svg")
    shutil.copy2(MONFERNO / "Diagrama_Contexto_SkyCampus_v2.svg", HERE / "Contexto_Monferno.svg")
    svg_context()
    svg_containers()
    generate_drawio()


if __name__ == "__main__":
    main()
