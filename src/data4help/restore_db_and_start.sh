#!/usr/bin/env bash

$(mongorestore --host mongodb:27017 --db data4help --drop ./dbbackup)

java_start="java -jar target/data4help-jar-with-dependencies.jar"

eval ${java_start}