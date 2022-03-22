#!/bin/bash

rm good_conf/_result.json

java -Dorg.slf4j.simpleLogger.defaultLogLevel=warn -jar ../build/sql2json.jar \
  -c jdbc:postgresql://localhost/postgres \
  -u postgres \
  -p postgres \
  -t $(pwd)/good_conf/template.json \
  -o $(pwd)/good_conf/_result.json

jarexit=$?

grep -l §last_element§ good_conf/_result.json

grepexit=$?

if [ $jarexit -ne 0 -o $grepexit -ne 0 ]; then
  echo exit_ok test failed
  exit 1
else
  echo exit_ok test passed
  exit 0
fi


