#!/bin/bash

echo "----------------------------------------"
echo "Java Version"
echo "----------------------------------------"

java --version

echo "----------------------------------------"
echo "Start waterfall.jar ..."
echo "----------------------------------------"

exec java -Xms512M -Xmx512M -XX:+UseG1GC -XX:G1HeapRegionSize=4M -XX:+UnlockExperimentalVMOptions \
    -XX:+ParallelRefProcEnabled -XX:+AlwaysPreTouch -XX:MaxInlineLevel=15 -jar waterfall.jar --nogui
