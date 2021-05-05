#!/bin/bash

java -Dorg.slf4j.simpleLogger.defaultLogLevel=warn -jar ../build/sql2json.jar \
  -c jdbc:postgresql://localhost/postgres \
  -u postgres \
  -p postgres \
  -t $(pwd)/fat_template_only/template.json \
  -o $(pwd)/fat_template_only/_result.json

jarexit=$?

grep -l §last_element§ fat_template_only/_result.json

grepexit=$?

if [ $jarexit -ne 0 -o $grepexit -ne 0 ]; then
  echo fat_boy test failed
  exit 1
else
  echo fat_template_only test passed
  exit 0
fi


