#!/bin/bash
# A dumb little script to build everything.
# This is useful because now the TA won't have to figure out the classpath
# and whatnot.

javac -cp stanford-parser.jar:htmlparser-1.2.1.jar \
    src/crybaby/Main.java \
    src/crybaby/Main2.java \
    src/crybaby/common/*.java \
    src/crybaby/parser/*.java \
    src/crybaby/jargon/*.java \
    src/crybaby/summarize/*.java \
