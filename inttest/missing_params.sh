#!/bin/bash
# Executes the jar with missing params and asserts that the exit value is <> 0

java -Dorg.slf4j.simpleLogger.defaultLogLevel=warn -jar ../build/sql2json.jar \
  -u postgres \
  -p postgres

if [ $? -ne 0 ]; then
  echo missing_params test passed
  exit 0
else
  echo missing_params test failed
  exit 1
fi


