#!/bin/bash
# Octopus Linux launcher script with bundled optimized JRE (jlink)
# Uses only resources from the current directory

DIR_JAVA="./jre"
"$DIR_JAVA/bin/java" -jar octopus.jar