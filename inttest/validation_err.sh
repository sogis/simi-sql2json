#!/bin/bash

java -Dorg.slf4j.simpleLogger.defaultLogLevel=warn -jar ../build/sql2json.jar \
  -c jdbc:postgresql://localhost/postgres \
  -u postgres \
  -p postgres \
  -t $(pwd)/validation_error/content.json \
  -o $(pwd)/validation_error/_result.json \
  -s $(pwd)/validation_error/schema.json

jarexit=$?

if [ $jarexit -ne 0 ]; then
  echo validation_error test passed
  exit 0
else
  echo validation_error test failed
  exit 1
fi


