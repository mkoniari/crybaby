#!/bin/bash

# Runs the jargon extractor test

java -Xmx1500M -cp stanford-parser.jar:src \
    crybaby.jargon.JargonExtractor $1
