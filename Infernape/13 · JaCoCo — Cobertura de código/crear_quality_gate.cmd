@echo off
rem Crea el Quality Gate "SkyCampus Enterprise" con los 6 indicadores del reto 13 y lo asigna al proyecto.
rem Requiere SonarQube en http://localhost:9000 y la variable SONAR_TOKEN (nunca escribir el token aqui).
setlocal
if "%SONAR_TOKEN%"=="" (echo Defina primero: set SONAR_TOKEN=su_token & exit /b 1)
set S=http://localhost:9000
set G=SkyCampus%%20Enterprise
curl -s -u %SONAR_TOKEN%: -X POST "%S%/api/qualitygates/create?name=%G%"
echo.
call :cond coverage LT 85 || exit /b 1
call :cond branch_coverage LT 75 || exit /b 1
rem Bugs y vulnerabilidades: claves del modo estandar; si el servidor usa el modo MQR se usan las de calidad de software
call :cond bugs GT 0 || call :cond software_quality_reliability_issues GT 0 || exit /b 1
call :cond vulnerabilities GT 0 || call :cond software_quality_security_issues GT 0 || exit /b 1
rem Deuda tecnica en minutos
call :cond sqale_index GT 15 || call :cond software_quality_maintainability_remediation_effort GT 15 || exit /b 1
call :cond duplicated_lines_density GT 3 || exit /b 1
curl -s -f -u %SONAR_TOKEN%: -X POST "%S%/api/qualitygates/select?gateName=%G%&projectKey=skycampus-enterprise" >nul && echo Gate asignado a skycampus-enterprise
exit /b 0

:cond
curl -s -f -u %SONAR_TOKEN%: -X POST "%S%/api/qualitygates/create_condition?gateName=%G%&metric=%1&op=%2&error=%3" >nul && (echo Condicion %1 %2 %3 creada) || (echo No se pudo crear %1 & exit /b 1)
exit /b 0
