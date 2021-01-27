# Executes the jar with a bad config and asserts that the output is written and the java exit value is <> 0

java -jar ../build/sql2json.jar \
  -c jdbc:postgresql://localhost/postgres \
  -u postgres \
  -p postgres \
  -l info \
  -t $(pwd)/fat_boy/template.json \
  -o $(pwd)/fat_boy/_result.json

jarexit=$?

grep -l §last_element§ fat_boy/_result.json

grepexit=$?

if [ $jarexit -ne 0 -o $grepexit -ne 0 ]; then
  echo fat_boy test failed
  exit 1
else
  echo fat_boy test passed
  exit 0
fi


