$ErrorActionPreference = 'Stop'
# Simula el reto 02 de Monferno en un repositorio temporal: dos features salen del mismo commit de develop,
# cambian partes diferentes (pero contiguas) de AsignadorMision.java, chocan al integrarse y se resuelven
# conservando ambos cambios. Al final se libera develop en main con el tag v2.0.0.
$repoTemporal = Join-Path $PSScriptRoot ('.gitflow-scratch-' + [guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Path $repoTemporal | Out-Null
$rutaAsignador = Join-Path $repoTemporal 'AsignadorMision.java'
$base = @'
public class AsignadorMision {
    public EstadoMision asignar(Mision mision) {
        Drone drone = estrategia.seleccionar(flota, mision);
        drone.setEstado(EstadoDrone.EN_VUELO);
        return EstadoMision.ASIGNADA;
    }
}
'@
# Juan (feature/asignacion-automatica): cambia SOLO la seleccion (clima + estrategia por prioridad).
$asignacion = @'
public class AsignadorMision {
    public EstadoMision asignar(Mision mision) {
        if (!clima.condicionesAptas(mision.origen(), mision.destino())) return EstadoMision.BLOQUEADA_CLIMA;
        Drone drone = estrategias.get(mision.prioridad()).seleccionar(flota, mision);
        drone.setEstado(EstadoDrone.EN_VUELO);
        return EstadoMision.ASIGNADA;
    }
}
'@
# Maria (feature/alertas-estado): cambia SOLO el cambio de estado (pasa por GestorFlota para notificar).
$alertas = @'
public class AsignadorMision {
    public EstadoMision asignar(Mision mision) {
        Drone drone = estrategia.seleccionar(flota, mision);
        gestorFlota.cambiarEstado(drone, EstadoDrone.EN_VUELO); // notifica a los observadores
        return EstadoMision.ASIGNADA;
    }
}
'@
# Resolucion: se conservan las dos lineas de Juan y la linea de Maria; se borran las marcas.
$resuelto = @'
public class AsignadorMision {
    public EstadoMision asignar(Mision mision) {
        if (!clima.condicionesAptas(mision.origen(), mision.destino())) return EstadoMision.BLOQUEADA_CLIMA;
        Drone drone = estrategias.get(mision.prioridad()).seleccionar(flota, mision);
        gestorFlota.cambiarEstado(drone, EstadoDrone.EN_VUELO); // notifica a los observadores
        return EstadoMision.ASIGNADA;
    }
}
'@
$utf8 = [Text.UTF8Encoding]::new($false)
function Escribir-Fuente([string]$contenido) { [IO.File]::WriteAllText($rutaAsignador, ($contenido -replace "`r`n", "`n") + "`n", $utf8) }
function Invoke-Git([string[]]$argumentos) {
    & git --no-pager -C $repoTemporal @argumentos
    if ($LASTEXITCODE -ne 0) { throw "git $($argumentos -join ' ') termino con codigo $LASTEXITCODE" }
}
function Guardar([string]$nombre, $lineas) {
    [IO.File]::WriteAllText((Join-Path $PSScriptRoot $nombre), (($lineas -join "`n") + "`n"), $utf8)
}
try {
    Invoke-Git @('init','-b','main')
    Invoke-Git @('config','user.name','Simulacion DOSW')
    Invoke-Git @('config','user.email','simulacion@localhost')
    Invoke-Git @('config','commit.gpgsign','false')
    Invoke-Git @('config','core.autocrlf','false')
    Escribir-Fuente $base
    Invoke-Git @('add','AsignadorMision.java')
    Invoke-Git @('commit','-m','feat: asignador de mision base (v1)')
    Invoke-Git @('switch','-c','develop')

    Invoke-Git @('switch','-c','feature/asignacion-automatica')
    Escribir-Fuente $asignacion
    Invoke-Git @('commit','-am','feat: asignacion automatica con clima y estrategia por prioridad')

    Invoke-Git @('switch','develop')
    Invoke-Git @('switch','-c','feature/alertas-estado')
    Escribir-Fuente $alertas
    Invoke-Git @('commit','-am','feat: alertas de cambio de estado via GestorFlota')

    Invoke-Git @('switch','develop')
    Invoke-Git @('merge','--no-ff','feature/asignacion-automatica','-m','merge: integra asignacion automatica')
    & git --no-pager -C $repoTemporal merge --no-ff feature/alertas-estado -m 'merge: integra alertas de estado'
    if ($LASTEXITCODE -eq 0) { throw 'El escenario debia producir un conflicto, pero el merge fue automatico.' }
    $estado = & git -C $repoTemporal status --short
    if (-not ($estado -match 'UU AsignadorMision.java')) { throw 'No se encontro el conflicto esperado en AsignadorMision.java.' }
    Guardar 'evidencia_conflicto.txt' (@('$ git status --short') + $estado + @('', '$ type AsignadorMision.java') + (Get-Content -LiteralPath $rutaAsignador))

    Escribir-Fuente $resuelto
    Invoke-Git @('add','AsignadorMision.java')
    Invoke-Git @('commit','-m','merge: integra asignacion automatica y sistema de alertas')

    Invoke-Git @('switch','main')
    Invoke-Git @('merge','--no-ff','develop','-m','release: SkyCampus v2.0.0')
    Invoke-Git @('tag','-a','v2.0.0','-m','SkyCampus v2: flota autonoma, 3 tipos de drone, alertas')

    $graph = & git --no-pager -C $repoTemporal log --graph --decorate --oneline --all
    Guardar 'evidencia_git_log_graph.txt' $graph
    $diff = & git --no-pager -C $repoTemporal show --format=fuller --stat 'develop'
    $diff += & git --no-pager -C $repoTemporal show --format= 'develop' -- AsignadorMision.java
    Guardar 'evidencia_merge_resuelto.diff' $diff
    Write-Output 'Conflicto provocado y resuelto conservando las dos features; v2.0.0 etiquetada en main.'
    Write-Output ($graph -join "`n")
} finally {
    Remove-Item -LiteralPath $repoTemporal -Recurse -Force -ErrorAction SilentlyContinue
}
