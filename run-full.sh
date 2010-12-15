#!/bin/bash
#A script which runs the full pipeline

java -Xmx4000m -cp stanford-parser.jar:htmlparser-1.2.1.jar:src \
    crybaby.Main $@
