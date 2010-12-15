#!/bin/bash
#A script which runs the partial pipeline, with no webscraping

java -Xmx1500m -cp ./stanford-parser.jar;./src crybaby.Main2 $1