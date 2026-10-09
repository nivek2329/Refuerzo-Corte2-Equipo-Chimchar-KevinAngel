# Pruebas de mutacion manual del reto 12 (Monferno).
# Para cada mutante: cambia una linea del codigo de produccion, corre "mvn test" y guarda la salida,
# y SIEMPRE restaura el archivo original (bloque finally). Si una prueba falla, el mutante "muere":
# eso demuestra que la prueba detecta el error. Uso, desde la raiz del repo:
#   powershell -NoProfile -ExecutionPolicy Bypass -File ..\_mutantes\mutantes_12.ps1
$ErrorActionPreference = 'Stop'
$repo = (Get-Location).Path
$v2 = Join-Path $repo 'Monferno\skycampus-v2'
$salida = Join-Path (Split-Path -Parent $repo) '_historial_tdd'
$asig = 'src\main\java\edu\eci\skycampus\asignacion'

$mutantes = @(
    @{ n = 1; nombre = 'desempate_invertido'; archivo = "$asig\AsignacionMasRapido.java";
       antes = '.thenComparingInt(Drone::bateria)'; despues = '.thenComparingInt(drone -> -drone.bateria())' },
    @{ n = 2; nombre = 'limite_peso_mas_uno'; archivo = "$asig\AsignadorMision.java";
       antes = 'paquete.pesoGramos() > CAPACIDAD_MAXIMA_FLOTA_GRAMOS';
       despues = 'paquete.pesoGramos() > CAPACIDAD_MAXIMA_FLOTA_GRAMOS + 1' },
    @{ n = 3; nombre = 'bajo_usa_urgente'; archivo = "$asig\AsignadorMision.java";
       antes = 'prioridad == Prioridad.URGENTE ?'; despues = 'prioridad != Prioridad.NORMAL ?' }
)

foreach ($m in $mutantes) {
    $ruta = Join-Path $v2 $m.archivo
    $original = [IO.File]::ReadAllText($ruta)
    if (-not $original.Contains($m.antes)) { throw "No se encontro el texto a mutar en $($m.archivo)" }
    $log = Join-Path $salida ("monferno_12_mutante_{0}_{1}.txt" -f $m.n, $m.nombre)
    $cabecera = "MUTANTE $($m.n) ($($m.nombre)) en $($m.archivo)`r`n  antes:   $($m.antes)`r`n  despues: $($m.despues)`r`n`r`n"
    try {
        [IO.File]::WriteAllText($ruta, $original.Replace($m.antes, $m.despues))
        [IO.File]::WriteAllText($log, $cabecera)
        cmd /c "mvn -f `"$v2\pom.xml`" clean test >> `"$log`" 2>&1"
        Write-Host ("Mutante {0} ({1}): mvn termino con codigo {2} (1 = alguna prueba fallo = mutante muerto)" -f $m.n, $m.nombre, $LASTEXITCODE)
    } finally {
        [IO.File]::WriteAllText($ruta, $original)
    }
}
Write-Host 'Codigo original restaurado. git status no deberia mostrar cambios en src/main.'
