#!/bin/bash

# path to JavaFX lib
JAVA_FX_LIB="${JAVA_FX_LIB:-/usr/share/openjfx/lib}"

# Launch the application
java --module-path "$JAVA_FX_LIB" --add-modules javafx.controls,javafx.fxml -jar octopus.jar