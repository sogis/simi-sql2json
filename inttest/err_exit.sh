# Executes the jar with a bad config and asserts that the output is written and the java exit value is <> 0

java -jar ../build/sql2json.jar \
  -c jdbc:postgresql://localhost/postgres \
  -u postgres \
  -p postgres \
  -l info \
  -t $(pwd)/bad_conf/template.json \
  -o $(pwd)/bad_conf/.result.json


