@echo off
REM ========================================
REM Octopus Windows non-standalone launcher
REM Alternative launcher for custom Java/JavaFX configurations
REM ========================================
REM
REM This script allows you to specify custom JAVA_HOME and PATH_TO_FX
REM for cases where JavaFX is installed separately from the JDK.
REM
REM For standard installations with JDK+JavaFX integrated (Zulu FX, Liberica Full),
REM you can simply double-click octopus.exe instead.
REM
REM ========================================

REM ========================================
REM CONFIGURATION
REM Modify these paths according to your Java installation
REM ========================================

REM SCENARIO 1: JDK with integrated JavaFX (Zulu FX, Liberica Full)
REM Simply set JAVA_HOME, do NOT set PATH_TO_FX
set JAVA_HOME=C:\Program Files\Java\zulu-11-fx

REM SCENARIO 2: JDK without JavaFX + separate JavaFX SDK
REM Set both JAVA_HOME and PATH_TO_FX
REM set JAVA_HOME=C:\Program Files\Java\jdk-11
REM set PATH_TO_FX=C:\Program Files\javafx-sdk-17.0.8\lib

REM ========================================
REM DO NOT MODIFY BELOW THIS LINE
REM ========================================

echo ========================================
echo Octopus Launcher
echo ========================================
echo JAVA_HOME=%JAVA_HOME%
echo PATH_TO_FX=%PATH_TO_FX%
echo ========================================
echo.

REM Check that JAVA_HOME is defined
if not defined JAVA_HOME (
    echo Error: JAVA_HOME is not set.
    echo Please edit this script and set JAVA_HOME to your JDK installation directory.
    pause
    exit /b 1
)

REM Check that java.exe exists
if not exist "%JAVA_HOME%\bin\java.exe" (
    echo Error: java.exe not found in %JAVA_HOME%\bin
    echo Please verify your JAVA_HOME configuration.
    pause
    exit /b 1
)

REM Build Java command
set JAVA_CMD=%JAVA_HOME%\bin\java.exe

REM Build the launch command based on PATH_TO_FX presence
if defined PATH_TO_FX (
    echo Using separate JavaFX from: %PATH_TO_FX%
    "%JAVA_CMD%" --module-path "%PATH_TO_FX%" --add-modules javafx.controls,javafx.fxml,javafx.web,javafx.base,javafx.graphics --add-modules java.xml.crypto -jar octopus.jar
) else (
    echo Using JavaFX from JDK (integrated)
    REM Note: java.xml.crypto is required for HTTPS/SSL connections
    "%JAVA_CMD%" --add-modules javafx.controls,javafx.fxml,javafx.web,javafx.base,javafx.graphics --add-modules java.xml.crypto -jar octopus.jar
)
