$ErrorActionPreference = 'Stop'
$repoTemporal = Join-Path $PSScriptRoot ('.gitflow-scratch-' + [guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Path $repoTemporal | Out-Null
$rutaAsignador = Join-Path $repoTemporal 'AsignadorMision.java'
$base = @'
class AsignadorMision {
    String asignar() {
        return "pendiente";
    }
    // PUNTO_DE_EXTENSION
}
'@
$asignacion = @'
class AsignadorMision {
    String asignar() {
        return validarClima() ? "asignada" : "bloqueada por clima";
    }
    private boolean validarClima() { return true; }
    // PUNTO_DE_EXTENSION
}
'@
$alertas = @'
class AsignadorMision {
    String asignar() {
        return notificarCambioEstado("EN_VUELO");
    }
    private String notificarCambioEstado(String estado) { System.out.println(estado); return "asignada"; }
    // PUNTO_DE_EXTENSION
}
'@
$resuelto = @'
class AsignadorMision {
    String asignar() {
        String estado = validarClima() ? "EN_VUELO" : "BLOQUEADA_POR_CLIMA";
        notificarCambioEstado(estado);
        return estado;
    }
    private boolean validarClima() { return true; }
    private void notificarCambioEstado(String estado) { System.out.println(estado); }
}
'@
function Escribir-Fuente([string]$contenido) { [IO.File]::WriteAllText($rutaAsignador, $contenido, [Text.UTF8Encoding]::new($false)) }
function Invoke-Git([string[]]$argumentos) {
    & git --no-pager -C $repoTemporal @argumentos
    if ($LASTEXITCODE -ne 0) { throw "git $($argumentos -join ' ') terminó con código $LASTEXITCODE" }
}
try {
    Invoke-Git @('init','-b','develop')
    Invoke-Git @('config','user.name','Simulación DOSW')
    Invoke-Git @('config','user.email','simulacion@localhost')
    Invoke-Git @('config','commit.gpgsign','false')
    Invoke-Git @('config','core.pager','cat')
    Escribir-Fuente $base
    Invoke-Git @('add','AsignadorMision.java')
    Invoke-Git @('commit','-m','base: asignador de misión')
    Invoke-Git @('switch','-c','feature/asignacion-automatica')
    Escribir-Fuente $asignacion
    Invoke-Git @('add','AsignadorMision.java')
    Invoke-Git @('commit','-m','feat: validar clima antes de asignar')
    Invoke-Git @('switch','develop')
    Invoke-Git @('switch','-c','feature/alertas-estado')
    Escribir-Fuente $alertas
    Invoke-Git @('add','AsignadorMision.java')
    Invoke-Git @('commit','-m','feat: notificar cambio de estado')
    Invoke-Git @('switch','develop')
    Invoke-Git @('merge','--no-ff','feature/asignacion-automatica','-m','merge: asignación automática')
    & git --no-pager -C $repoTemporal merge --no-ff feature/alertas-estado -m 'merge: alertas de estado'
    if ($LASTEXITCODE -eq 0) { throw 'El escenario debía producir un conflicto, pero el merge fue automático.' }
    $estadoConflicto = & git -C $repoTemporal status --short
    if (-not ($estadoConflicto -match 'UU AsignadorMision.java')) { throw 'No se encontró el conflicto esperado en AsignadorMision.java.' }
    Escribir-Fuente $resuelto
    Invoke-Git @('add','AsignadorMision.java')
    Invoke-Git @('commit','-m','merge: conservar validación y alertas')
    $carpetaEvidencia = $PSScriptRoot
    $graph = & git --no-pager -C $repoTemporal log --graph --decorate --oneline --all
    [IO.File]::WriteAllText((Join-Path $carpetaEvidencia 'evidencia_git_log_graph.txt'), (($graph -join [Environment]::NewLine) + [Environment]::NewLine), [Text.UTF8Encoding]::new($false))
    $diff = & git --no-pager -C $repoTemporal show --format=fuller --stat HEAD; $diff += & git --no-pager -C $repoTemporal show --format= --find-renames HEAD -- AsignadorMision.java
    [IO.File]::WriteAllText((Join-Path $carpetaEvidencia 'evidencia_merge_resuelto.diff'), (($diff -join [Environment]::NewLine) + [Environment]::NewLine), [Text.UTF8Encoding]::new($false))
    Write-Output 'Conflicto provocado y resuelto conservando las dos features.'
    Write-Output ($graph -join [Environment]::NewLine)
} finally {
    Remove-Item -LiteralPath $repoTemporal -Recurse -Force -ErrorAction SilentlyContinue
}
