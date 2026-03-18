#!/bin/bash
# Octopus Linux launcher script
# PREREQUISITE: User must have a JDK with JavaFX (e.g., Zulu FX) or OpenJFX installed

# Usage:
#   JAVA_HOME=/path/to/jdk ./octopus.sh
#   
#   If JavaFX is installed separately:
#   JAVA_HOME=/path/to/jdk PATH_TO_FX=/path/to/javafx-sdk/lib ./octopus.sh

# Check that JAVA_HOME is defined
if [ -z "$JAVA_HOME" ]; then
    echo "Error: JAVA_HOME environment variable is not set."
    echo "Usage: JAVA_HOME=/path/to/jdk ./octopus.sh"
    exit 1
fi

export PATH="$JAVA_HOME/bin:$PATH"

# Build JavaFX options if PATH_TO_FX is defined
JAVAFX_OPTS=""
if [ -n "$PATH_TO_FX" ]; then
    JAVAFX_OPTS="--module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml,javafx.web,javafx.base,javafx.graphics"
fi

# Launch
"$JAVA_HOME/bin/java" \
	--add-modules java.xml.crypto \
	$JAVAFX_OPTS \
	-jar octopus.jar