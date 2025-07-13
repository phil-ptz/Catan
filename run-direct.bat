@echo off
echo Starting Catan game directly...

REM Set JAVA_HOME
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.7.6-hotspot"
set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"

REM Ensure project is compiled
echo Compiling project...
call mvnw.cmd clean compile

REM Get JavaFX modules path
set "JAVAFX_PATH=C:\Users\%USERNAME%\.m2\repository\org\openjfx"

REM Run with module path
echo Running application...
"%JAVA_EXE%" ^
    --module-path "target\classes;%JAVAFX_PATH%\javafx-controls\21\javafx-controls-21.jar;%JAVAFX_PATH%\javafx-fxml\21\javafx-fxml-21.jar;%JAVAFX_PATH%\javafx-graphics\21\javafx-graphics-21.jar;%JAVAFX_PATH%\javafx-base\21\javafx-base-21.jar" ^
    --add-modules javafx.controls,javafx.fxml ^
    --module de.philx.catan/de.philx.catan.MainApplication

pause
