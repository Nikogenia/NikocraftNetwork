#!/bin/bash

echo "--------------------"
echo "Java Version"
echo "--------------------"

java --version

echo "--------------------"
echo "Start nnmaster.jar ..."
echo "--------------------"

exec java -jar nnmaster.jar
