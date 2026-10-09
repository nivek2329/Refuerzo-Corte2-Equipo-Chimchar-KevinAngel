# Pruebas de mutacion manual del reto 12 (Monferno).
# Para cada mutante: cambia una linea del codigo de produccion, corre "mvn test" y guarda la salida,
# y SIEMPRE restaura el archivo original (bloque finally). Si una prueba falla, el mutante "muere":
# eso demuestra que la prueba detecta el error. Uso, desde la raiz del repo:
#   powershell -NoProfile -ExecutionPolicy Bypass -File ..\_mutantes\mutantes_12_politica.ps1
# Ciclo 4: mutantes sobre la politica de produccion (PoliticaAsignacion).
$ErrorActionPreference = 'Stop'
$repo = (Get-Location).Path
$v2 = Join-Path $repo 'Monferno\skycampus-v2'
$salida = Join-Path (Split-Path -Parent $repo) '_historial_tdd'
$asig = 'src\main\java\edu\eci\skycampus\asignacion'

$mutantes = @(
    @{ n = 4; nombre = 'politica_urgente_mayor_bateria'; archivo = "$asig\PoliticaAsignacion.java";
       antes = 'Prioridad.URGENTE, new AsignacionMasRapido()'; despues = 'Prioridad.URGENTE, new AsignacionMayorBateria()' },
    @{ n = 5; nombre = 'politica_bajo_mas_rapido'; archivo = "$asig\PoliticaAsignacion.java";
       antes = 'Prioridad.BAJO, new AsignacionMayorBateria()'; despues = 'Prioridad.BAJO, new AsignacionMasRapido()' }
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
