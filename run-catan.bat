@echo off
echo Setting up Java environment...

REM Try to find Java automatically
for /f "tokens=*" %%i in ('where java 2^>nul') do (
    set "JAVA_PATH=%%i"
    goto :found
)

echo Java not found in PATH!
pause
exit /b 1

:found
REM Extract JAVA_HOME from java.exe path
for %%i in ("%JAVA_PATH%") do set "JAVA_HOME=%%~dpi"
set "JAVA_HOME=%JAVA_HOME:~0,-5%"

echo Using JAVA_HOME: %JAVA_HOME%

REM Run Maven with JavaFX
echo Starting Catan game...
call mvnw.cmd clean javafx:run

pause
