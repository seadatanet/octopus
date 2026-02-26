@echo off
REM Script de lancement d'Octopus avec JRE embarquée
REM Ce script doit fonctionner uniquement avec les ressources du répertoire courant (JRE embarquée et octopus.jar)

REM Change vers le répertoire du script
cd /d "%~dp0%"

REM Définit les variables pour la JRE embarquée
set DIR_JAVA=.\jre
set PATH=%DIR_JAVA%\bin;%PATH%
set JAVA_HOME=%DIR_JAVA%

REM Lance Octopus avec la JRE embarquée et les modules nécessaires
%DIR_JAVA%\bin\java.exe --add-modules javafx.controls,java.xml.crypto,javafx.base,javafx.graphics,javafx.fxml,javafx.web,jdk.xml.dom,java.xml -jar "octopus.jar"

REM Pause pour voir les éventuels messages d'erreur
pause