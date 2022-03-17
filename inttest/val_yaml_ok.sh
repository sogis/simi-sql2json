#!/bin/bash

java -Dorg.slf4j.simpleLogger.defaultLogLevel=warn -jar ../build/sql2json.jar \
  -c jdbc:postgresql://localhost/postgres \
  -u postgres \
  -p postgres \
  -t $(pwd)/validation_ok/content.json \
  -o $(pwd)/validation_ok/_result.yaml \
  -s $(pwd)/validation_ok/schema.json

jarexit=$?

if [ $jarexit -ne 0 ]; then
  echo validation_ok test failed
  exit 1
else
  echo validation_ok test passed
  exit 0
fi


