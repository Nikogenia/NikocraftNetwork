#!/bin/bash

echo "----------------------------------------"
echo "Java Version"
echo "----------------------------------------"

java --version

echo "----------------------------------------"
echo "Start nnmaster.jar ..."
echo "----------------------------------------"

# Debug mode
#exec java -jar nnmaster-*-jar-with-dependencies.jar --debug

# Normal mode
exec java -jar nnmaster-*-jar-with-dependencies.jar
