#!/bin/bash
# Executes the jar with a bad config and asserts that the output is written and the java exit value is <> 0

rm bad_conf/.result.json

java -Dorg.slf4j.simpleLogger.defaultLogLevel=warn -jar ../build/sql2json.jar \
  -c jdbc:postgresql://localhost/postgres \
  -u postgres \
  -p postgres \
  -t $(pwd)/bad_conf/template.json \
  -o $(pwd)/bad_conf/.result.json

jarexit=$?

grep -l §last_element§ bad_conf/.result.json

grepexit=$?

if [ $jarexit -eq 0 -o $grepexit -ne 0 ]; then # Test fails if sql2json.jar exits with ok value
  echo exit_err test failed
  exit 1
else
  echo exit_err test passed
  exit 0
fi


