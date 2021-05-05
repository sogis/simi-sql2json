#!/bin/bash

java -jar ../build/sql2json.jar -v

jarexit=$?

if [ $jarexit -ne 0 ]; then
  echo print_version test failed
  exit 1
else
  echo print_version test passed
  exit 0
fi