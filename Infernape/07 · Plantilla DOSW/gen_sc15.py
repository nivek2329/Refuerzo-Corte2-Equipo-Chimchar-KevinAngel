"""Genera SC-15 a partir de la ficha DOSW aprobada en Monferno (SC-07): misma plantilla, estilos y encabezado.

Uso (desde esta carpeta): python gen_sc15.py
"""
import re
import shutil
import zipfile
from pathlib import Path
from xml.sax.saxutils import escape

HERE = Path(__file__).resolve().parent
BASE = HERE.parents[1] / "Monferno" / "07 · Plantilla DOSW" / "SC-07_Asignar_automaticamente_drone_DOSW.docx"
SALIDA = HERE / "SC-15_Planificar_ruta_multi_etapa_inter_sede_DOSW.docx"

FICHA = [("Código:", "SC-15"), ("Nombre:", "Planificar ruta multi-etapa inter-sede")]
FUNCIONALIDAD = [
    ("Descripción:", "SkyCampus Enterprise planifica la ruta de un paquete entre dos sedes que no se alcanzan en un solo "
     "vuelo con carga (ejemplo de referencia: ECI → estación de carga de la calle 116 → Uniandes). Verifica la "
     "autorización de la Aerocivil, divide el trayecto en etapas de máximo 5 km con carga, reserva estaciones de "
     "carga intermedias, asigna un drone por etapa, confirma la ruta e inicia el primer vuelo."),
    ("Cómo se ejecutará:", "El coordinador de la sede origen abre la misión pendiente en el panel de misiones y pulsa "
     "\"Planificar ruta\" (CU \"Calcular ruta multi-etapa\", paquete Rutas y Navegación). El sistema calcula la propuesta "
     "completa y la muestra para que el coordinador la confirme; después inicia el vuelo sin más intervención."),
    ("Actor principal:", "Coordinador de sede (sede origen). Actores secundarios: Aerocivil (sistema externo de regulación "
     "aérea) y Estación de carga autónoma (reporta ocupación y acepta reservas)."),
    ("Precondiciones:", "La misión existe en estado PENDIENTE con su paquete registrado; el coordinador inició sesión en su "
     "sede; las estaciones de carga publican su ocupación. El estado de la Aerocivil y de las sedes se verifica dentro "
     "del flujo (pasos 2 y 3), no se asume."),
]
ENTRADA = [
    ("mision", "Misión inter-sede que se va a planificar.",
     "MisionInterSede(id:String, paquete:Paquete, sedeOrigen:Sede, sedeDestino:Sede, restricciones:RestriccionesEspeciales, prioridad:PrioridadMision)",
     "Objeto existente en estado PENDIENTE; se desglosan sus atributos abajo.", "Sí"),
    ("mision.id", "Identificador de la misión.", "String", "Formato M-XXXXXXXX (8 caracteres alfanuméricos).", "Sí"),
    ("mision.prioridad", "Urgencia de la entrega.", "Enum(BAJO, NORMAL, URGENTE)",
     "URGENTE ordena las alternativas por tiempo; las demás por riesgo (reto 03, Strategy).", "Sí"),
    ("mision.paquete", "Carga que se transporta.",
     "Paquete(pesoGramos:Integer, dimensiones:Dimensiones, tipoCarga:TipoCarga, fragil:Boolean, valorDeclarado:Decimal)",
     "Objeto anidado; se desglosan sus atributos abajo.", "Sí"),
    ("mision.paquete.pesoGramos", "Peso del paquete.", "Integer", "Entre 1 y 15000 g. Determina el tipo de drone de cada etapa (RN-5).", "Sí"),
    ("mision.paquete.dimensiones", "Tamaño del paquete.", "Dimensiones(largoCm:Integer, anchoCm:Integer, altoCm:Integer)",
     "Objeto anidado; se desglosan sus atributos abajo.", "Sí"),
    ("mision.paquete.dimensiones.largoCm", "Largo.", "Integer", "Entre 1 y 60 cm.", "Sí"),
    ("mision.paquete.dimensiones.anchoCm", "Ancho.", "Integer", "Entre 1 y 40 cm.", "Sí"),
    ("mision.paquete.dimensiones.altoCm", "Alto.", "Integer", "Entre 1 y 30 cm.", "Sí"),
    ("mision.paquete.tipoCarga", "Tipo de carga.", "Enum(SOBRE, CARPETA, LIBRO, EQUIPO, MUESTRA_LABORATORIO)",
     "MUESTRA_LABORATORIO exige cadena de frío (RN-7).", "Sí"),
    ("mision.paquete.fragil", "Indica manejo delicado.", "Boolean", "Si es true, cada estación recibe la marca \"frágil\" en la reserva.", "Sí"),
    ("mision.paquete.valorDeclarado", "Valor declarado del contenido.", "Decimal(12,2) en COP", "Mayor o igual a 0; solo informativo para el seguro.", "No"),
    ("mision.sedeOrigen", "Sede desde la que sale el paquete.", "Sede(codigo:CodigoSede, universidad:String, ubicacion:Coordenada, activa:Boolean)",
     "Objeto existente; se desglosan sus atributos abajo.", "Sí"),
    ("mision.sedeOrigen.codigo", "Código de la sede.", "Enum(ECI, UNAL, UNIANDES, EAFIT)", "Debe coincidir con la sede del coordinador.", "Sí"),
    ("mision.sedeOrigen.ubicacion", "Punto de despegue.", "Coordenada(latitud:Decimal(9,6), longitud:Decimal(9,6))",
     "Coordenadas WGS84 del helipuerto de la sede.", "Sí"),
    ("mision.sedeOrigen.activa", "Estado operativo de la sede.", "Boolean", "Debe ser true (RN-6).", "Sí"),
    ("mision.sedeDestino", "Sede de entrega.", "Sede(codigo:CodigoSede, universidad:String, ubicacion:Coordenada, activa:Boolean)",
     "Mismos atributos que sedeOrigen; codigo distinto del origen, activa = true y en la misma área metropolitana (RN-6).", "Sí"),
    ("mision.restricciones", "Condiciones especiales del envío.",
     "RestriccionesEspeciales(alturaMaximaMetros:Integer, ventanaEntrega:Ventana, requiereCadenaFrio:Boolean)",
     "Objeto anidado; se desglosan sus atributos abajo.", "Sí"),
    ("mision.restricciones.alturaMaximaMetros", "Altura máxima pedida para el envío.", "Integer",
     "Entre 30 y 120 m; en zona urbana nunca más de 120 m aunque se pida otra cosa (RNF-09).", "Sí"),
    ("mision.restricciones.ventanaEntrega", "Franja en la que debe entregarse.", "Ventana(desde:DateTime, hasta:DateTime)",
     "desde < hasta; la llegada estimada de la última etapa debe quedar dentro.", "Sí"),
    ("mision.restricciones.requiereCadenaFrio", "Indica refrigeración obligatoria.", "Boolean",
     "Si es true, solo se usan estaciones con refrigeración (RN-7).", "Sí"),
    ("espacioAereo", "Condiciones actuales del espacio aéreo al planificar.",
     "EstadoEspacioAereo(consultadoEn:DateTime, vientoKmH:Decimal(4,1), vientoDesdeGrados:Integer, zonasRestringidas:ZonaRestringida[0..n])",
     "Lo entregan la API Meteorológica y la Aerocivil en el paso 3; se desglosan sus atributos abajo.", "Sí"),
    ("espacioAereo.vientoKmH", "Velocidad del viento.", "Decimal(4,1)", "Mayor o igual a 0; se usa para el tiempo de cada etapa (hotfix v3.0.2).", "Sí"),
    ("espacioAereo.vientoDesdeGrados", "Dirección desde la que sopla el viento.", "Integer", "Entre 0 y 359.", "Sí"),
    ("espacioAereo.zonasRestringidas[i].codigo", "Zona que la Aerocivil cierra temporalmente.", "String", "Código asignado por la Aerocivil.", "No"),
    ("espacioAereo.zonasRestringidas[i].alturaMaximaMetros", "Techo permitido dentro de la zona.", "Integer",
     "Entre 0 y 120; 0 significa zona prohibida.", "No"),
]
SALIDA_DATOS = [
    ("rutaPlanificada", "Ruta propuesta y, al confirmarla, la ruta en ejecución.",
     "RutaMultiEtapa(id:String, etapas:Etapa[1..n], esperas:EsperaEstacion[0..n], tiempoEstimadoMin:Integer, estado:EstadoRuta, autorizacionAerocivil:String)",
     "Se desglosan sus atributos abajo.", "No (salida)"),
    ("rutaPlanificada.id", "Identificador de la ruta.", "String", "Formato R-<id de la misión>.", "No (salida)"),
    ("rutaPlanificada.etapas[i].numero", "Orden de la etapa.", "Integer", "Empieza en 1 y es consecutivo.", "No (salida)"),
    ("rutaPlanificada.etapas[i].origen", "Punto donde despega la etapa.", "PuntoRuta(tipo:Enum(SEDE, ESTACION), codigo:String)",
     "La etapa 1 sale de la sede origen; las demás, de la estación donde terminó la anterior.", "No (salida)"),
    ("rutaPlanificada.etapas[i].destino", "Punto donde aterriza la etapa.", "PuntoRuta(tipo:Enum(SEDE, ESTACION), codigo:String)",
     "La última etapa termina en la sede destino.", "No (salida)"),
    ("rutaPlanificada.etapas[i].distanciaKm", "Longitud de la etapa.", "Decimal(5,2)", "Máximo 5,00 km con carga (RN-1).", "No (salida)"),
    ("rutaPlanificada.etapas[i].drone", "Drone asignado a la etapa.", "DroneEtapa(id:String, tipo:TipoDroneEtapa, capacidadCargaKg:Decimal(4,1))",
     "tipo Enum(LIGERO, CARGA) según RN-5; creado por la fábrica del tipo (Factory Method, reto 03).", "No (salida)"),
    ("rutaPlanificada.etapas[i].salida", "Hora de despegue de la etapa.", "DateTime", "Llegada de la etapa anterior + espera.", "No (salida)"),
    ("rutaPlanificada.etapas[i].llegadaEstimada", "Hora estimada de aterrizaje.", "DateTime", "Calculada con viento y deriva.", "No (salida)"),
    ("rutaPlanificada.esperas[i]", "Espera del paquete en una estación.", "EsperaEstacion(estacion:String, minutos:Integer)",
     "minutos entre 0 y 30 (RN-2).", "No (salida)"),
    ("rutaPlanificada.tiempoEstimadoMin", "Duración total estimada.", "Integer", "Suma de etapas y esperas.", "No (salida)"),
    ("rutaPlanificada.estado", "Estado de la ruta.", "Enum(PLANIFICADA, CONFIRMADA, EN_CURSO, RECHAZADA)",
     "PLANIFICADA al proponerla, CONFIRMADA en el paso 8, EN_CURSO en el paso 9.", "No (salida)"),
    ("rutaPlanificada.autorizacionAerocivil", "Código de la autorización recibida.", "String", "Obligatorio para pasar a CONFIRMADA (RN-4).", "No (salida)"),
    ("rechazo", "Explicación cuando la ruta no se puede planificar.", "Rechazo(motivo:MotivoRechazoRuta, mensaje:String)",
     "motivo Enum(AEROCIVIL_RECHAZA, SIN_ESTACION_DISPONIBLE, PAQUETE_EXCEDE_RUTA, SEDE_INACTIVA, AEROCIVIL_SIN_RESPUESTA); visible para el coordinador.", "No (salida)"),
]
FLUJO = [
    ("1", "Coordinador de sede", "Abre la misión PENDIENTE en el panel y solicita \"Planificar ruta\".", "---"),
    ("2", "SkyCampus Enterprise", "Valida los datos de entrada: peso, dimensiones, ventana, sedes distintas, activas y en la misma área metropolitana.",
     "Si una sede está inactiva o fuera del área, ejecuta FA-4."),
    ("3", "SkyCampus Enterprise / Aerocivil", "Verifica con la Aerocivil: envía origen, destino, altura máxima y ventana; recibe el código de autorización y las zonas restringidas vigentes.",
     "Si la Aerocivil rechaza, ejecuta FA-1. Si no responde, ejecuta FA-5."),
    ("4", "SkyCampus Enterprise", "Calcula las etapas: divide el trayecto en tramos de máximo 5 km con carga, dentro del radio efectivo de cada sede, evitando zonas restringidas y estimando cada tiempo con viento y deriva.",
     "Si el paquete excede lo que la ruta permite, ejecuta FA-3."),
    ("5", "SkyCampus Enterprise / Estación de carga", "Asigna estaciones de carga: en cada punto intermedio reserva un turno de recarga cuya espera no supere 30 min (refrigerada si hay cadena de frío).",
     "Si no hay estación válida, ejecuta FA-2."),
    ("6", "SkyCampus Enterprise", "Asigna un drone por etapa: según el peso elige el tipo (LIGERO o CARGA) y la fábrica de ese tipo crea el drone de la sede o estación de salida.",
     "Si una estación no tiene drone del tipo, vuelve al paso 5 con la siguiente estación."),
    ("7", "Coordinador de sede", "Revisa la propuesta (etapas, estaciones, esperas, llegada estimada) y la confirma.",
     "Si cancela, la ruta queda RECHAZADA y se liberan todas las reservas (RN-8)."),
    ("8", "SkyCampus Enterprise", "Confirma la ruta: estado CONFIRMADA, reservas definitivas y registro del código de la Aerocivil.", "---"),
    ("9", "SkyCampus Enterprise", "Inicia el vuelo: el drone de la etapa 1 pasa a EN_VUELO, la ruta a EN_CURSO y se publica el evento de inicio de etapa con su llegada estimada.", "---"),
]
ALTERNOS = [
    ("FA-1 (3a)", "SkyCampus Enterprise", "La Aerocivil rechaza la ruta: se registra el motivo devuelto (zona cerrada, altura, ventana) y la ruta queda RECHAZADA con AEROCIVIL_RECHAZA. Se ofrece reprogramar en otra ventana.",
     "No se reservan estaciones ni drones."),
    ("FA-2 (5a)", "SkyCampus Enterprise", "No hay estación de carga disponible: se buscan estaciones alternativas dentro del radio efectivo; si ninguna garantiza espera ≤ 30 min, la ruta queda RECHAZADA con SIN_ESTACION_DISPONIBLE.",
     "Se liberan las reservas parciales hechas en este paso."),
    ("FA-3 (4a)", "SkyCampus Enterprise", "Paquete demasiado pesado para la ruta larga: pesa más de 15000 g (capacidad del tipo CARGA) o la ruta exigiría más de 3 etapas para cumplir RN-1 dentro de la ventana. Queda RECHAZADA con PAQUETE_EXCEDE_RUTA y se sugiere dividir el envío.",
     "No se consulta a las estaciones."),
    ("FA-4 (2a)", "SkyCampus Enterprise", "Sede inactiva o destino fuera del área metropolitana del origen: RECHAZADA con SEDE_INACTIVA.",
     "No se contacta a la Aerocivil."),
    ("FA-5 (3b)", "SkyCampus Enterprise", "La Aerocivil no responde en 5 s: se reintenta una vez; si sigue sin respuesta, RECHAZADA con AEROCIVIL_SIN_RESPUESTA.",
     "Ningún vuelo se planifica sin autorización (RN-4)."),
]
NOTAS = ("SC-15 especifica RF-13, RF-14 y RNF-09 del reto 06 de Infernape. En el código ya existen: ReglasRutaMultiEtapa (RN-1 y RN-2), "
         "ConfiguracionVueloSede y LimitesAerocivil (radio efectivo y 120 m), el orden \"Aerocivil antes de reservar\" del AsignadorMision, "
         "RutaCompuesta/TramoRuta (Composite), FabricaDronesPorEtapa (Factory Method) y CalculadorRutaInterSede (viento y deriva). "
         "Estado objetivo, aún no implementado: la reserva de turnos en estaciones y los estados PLANIFICADA/CONFIRMADA/EN_CURSO de la ruta.")
REGLAS = [
    "Ningún drone vuela más de 5 km con carga sin pasar por una estación de carga (RN-1).",
    "El paquete no permanece más de 30 minutos en una estación entre dos etapas (RN-2).",
    "Ninguna etapa sale del radio efectivo de su sede (el menor entre el del coordinador y el de la Aerocivil) ni supera 120 m de altura en zona urbana (RN-3, RNF-09).",
    "Sin código de autorización de la Aerocivil no se reserva ninguna estación ni drone (RN-4).",
    "El tipo de drone de cada etapa se elige por el peso: LIGERO hasta 2000 g, CARGA hasta 15000 g (RN-5).",
    "Origen y destino deben estar activos, ser distintos y estar en la misma área metropolitana; EAFIT (Medellín) solo recibe rutas dentro de su ciudad (RN-6).",
    "Un paquete con cadena de frío solo usa estaciones con refrigeración (RN-7).",
    "Cancelar o rechazar una ruta libera todas las reservas de estaciones y drones (RN-8).",
]
ABREVIATURAS = [
    ("FA", "Flujo alterno; entre paréntesis, el paso donde se desvía."),
    ("RN", "Regla de negocio de esta ficha."),
    ("CU / RF / RNF", "Caso de uso (reto 10) / requisito funcional y no funcional (reto 06) de Infernape."),
    ("Radio efectivo", "min(radio configurado por el coordinador, radio autorizado por la Aerocivil)."),
]
HISTORIAL = [("Equipo Chimchar (nivel Infernape)", "Pendiente de revisión", "09/10/2026",
              "Creación de la ficha DOSW para SC-15 Planificar ruta multi-etapa inter-sede, con sub-objetos desglosados, 9 pasos, 5 flujos alternos y 8 reglas.")]
ANEXOS = [
    "Prototipo: flujo de planificación de ruta inter-sede con sus estados de error en el dashboard Enterprise (reto 11 de Infernape).",
    "Diagrama C4 nivel 2 (reto 05 de Infernape): el Servicio de Rutas consulta API Meteorológica, Control Aéreo ECI y Aerocivil.",
    "Pruebas que verifican reglas de esta ficha: ReglasRutaMultiEtapaTest, RestriccionesVueloTest, AsignadorMisionFlujosAlternosTest y PatronesRutaTest.",
]


def texto_celda(celda, texto):
    """Deja un solo run con el formato del primero y el texto nuevo."""
    rpr = re.search(r"<w:rPr>.*?</w:rPr>", celda, re.S)
    rpr = rpr.group(0) if rpr else ""
    parrafos = re.findall(r"<w:p[ >].*?</w:p>", celda, re.S)
    primero = parrafos[0]
    ppr = re.search(r"<w:pPr>.*?</w:pPr>", primero, re.S)
    nuevo = f'<w:p>{ppr.group(0) if ppr else ""}<w:r>{rpr}<w:t xml:space="preserve">{escape(texto)}</w:t></w:r></w:p>'
    inicio = celda.index(parrafos[0])
    fin = celda.rindex(parrafos[-1]) + len(parrafos[-1])
    return celda[:inicio] + nuevo + celda[fin:]


def fila_con(fila, valores):
    celdas = re.findall(r"<w:tc>.*?</w:tc>", fila, re.S)
    assert len(celdas) == len(valores), (len(celdas), valores)
    for celda, valor in zip(celdas, valores):
        fila = fila.replace(celda, texto_celda(celda, valor), 1)
    return fila


def llenar(tabla, filas, encabezado=1):
    actuales = re.findall(r"<w:tr[ >].*?</w:tr>", tabla, re.S)
    plantilla = actuales[encabezado] if len(actuales) > encabezado else actuales[-1]
    nuevas = [fila_con(plantilla, list(f)) for f in filas]
    inicio = tabla.index(actuales[encabezado])
    fin = tabla.rindex(actuales[-1]) + len(actuales[-1])
    return tabla[:inicio] + "".join(nuevas) + tabla[fin:]


def main():
    with zipfile.ZipFile(BASE) as z:
        doc = z.read("word/document.xml").decode("utf-8")
        tablas = re.findall(r"<w:tbl>.*?</w:tbl>", doc, re.S)
        nuevas = list(tablas)
        nuevas[1] = llenar(tablas[1], FICHA, 0)
        nuevas[2] = llenar(tablas[2], FUNCIONALIDAD, 0)
        nuevas[3] = llenar(tablas[3], ENTRADA)
        nuevas[4] = llenar(tablas[4], SALIDA_DATOS)
        nuevas[5] = llenar(tablas[5], FLUJO)
        nuevas[6] = llenar(tablas[6], ALTERNOS)
        nuevas[7] = llenar(tablas[7], [("Notas y comentarios:", NOTAS)], 0)
        nuevas[8] = llenar(tablas[8], [(str(i), r) for i, r in enumerate(REGLAS, 1)])
        nuevas[9] = llenar(tablas[9], ABREVIATURAS)
        nuevas[10] = llenar(tablas[10], HISTORIAL)
        for vieja, nueva in zip(tablas, nuevas):
            doc = doc.replace(vieja, nueva, 1)
        anexos = [p for p in re.findall(r"<w:p[ >].*?</w:p>", doc.split("ANEXOS", 1)[1].split("REGLAS DE NEGOCIO", 1)[0], re.S)
                  if re.search(r"<w:t[^>]*>[^<]+</w:t>", p)]
        for parrafo, texto in zip(anexos, ANEXOS):
            doc = doc.replace(parrafo, texto_celda(parrafo, texto), 1)
        tmp = SALIDA.with_suffix(".tmp")
        with zipfile.ZipFile(tmp, "w", zipfile.ZIP_DEFLATED) as salida:
            for item in z.infolist():
                datos = doc.encode("utf-8") if item.filename == "word/document.xml" else z.read(item.filename)
                salida.writestr(item, datos)
    shutil.move(tmp, SALIDA)
    print("generado", SALIDA.name)


if __name__ == "__main__":
    main()
