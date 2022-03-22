#!/bin/bash

rm fat_complete_json_no_sql_tags/.result.json

java -Dorg.slf4j.simpleLogger.defaultLogLevel=warn -jar ../build/sql2json.jar \
  -c jdbc:postgresql://localhost/postgres \
  -u postgres \
  -p postgres \
  -t $(pwd)/fat_complete_json_no_sql_tags/template.json \
  -o $(pwd)/fat_complete_json_no_sql_tags/.result.json

jarexit=$?

grep -l §last_element§ fat_complete_json_no_sql_tags/.result.json

grepexit=$?

if [ $jarexit -ne 0 -o $grepexit -ne 0 ]; then
  echo json_no_sql_tags test failed
  exit 1
else
  echo json_no_sql_tags test passed
  exit 0
fi


