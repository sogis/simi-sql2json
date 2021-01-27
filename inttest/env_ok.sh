#!/bin/bash
# Executes the jar with env variables (instead of commandline args)

export SqlTrafo_Templatepath=$(pwd)/good_conf/template.json
export SqlTrafo_Outputpath=$(pwd)/good_conf/_result.json
export SqlTrafo_DbConnection=jdbc:postgresql://localhost/postgres
export SqlTrafo_DbUser=postgres
export SqlTrafo_DbPassword=postgres

java -Dorg.slf4j.simpleLogger.defaultLogLevel=warn -jar ../build/sql2json.jar

jarexit=$?

grep -l §last_element§ good_conf/_result.json

grepexit=$?

if [ $jarexit -ne 0 -o $grepexit -ne 0 ]; then
  echo env_ok test failed
  exit 1
else
  echo env_ok test passed
  exit 0
fi


