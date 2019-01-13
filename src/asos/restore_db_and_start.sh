#!/usr/bin/env bash

$(mongorestore --host mongodb:27017 --db automatedsos --drop ./dbbackup)

java_start="java -jar target/asos-jar-with-dependencies.jar"

eval ${java_start}
