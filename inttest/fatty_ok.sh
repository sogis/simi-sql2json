#!/bin/bash

rm fat_boy/_result.json

java -Dorg.slf4j.simpleLogger.defaultLogLevel=warn -jar ../build/sql2json.jar \
  -c jdbc:postgresql://localhost/postgres \
  -u postgres \
  -p postgres \
  -t $(pwd)/fat_boy/template.json \
  -o $(pwd)/fat_boy/_result.json

jarexit=$?

grep -l §last_element§ fat_boy/_result.json

grepexit=$?

if [ $jarexit -ne 0 -o $grepexit -ne 0 ]; then
  echo fatty_ok test failed
  exit 1
else
  echo fatty_ok test passed
  exit 0
fi


