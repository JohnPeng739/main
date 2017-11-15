#!/bin/bash

JAVA_HOME=/opt/jdk

CLASSPATH=/opt/retl/conf/.
for jarFile in /opt/retl/deps-restful/*.jar; do
  CLASSPATH=$CLASSPATH:$jarFile
done

echo "JAVA_HOME: $JAVA_HOME"
echo "CLASSPATH: $CLASSPATH"

$JAVA_HOME/bin/java -cp $CLASSPATH com.ds.retl.RETLRestApplication >> /opt/retl/logs/startup.log 2>&1 &

echo "Start DS RETL Restful Application Service successfully."
