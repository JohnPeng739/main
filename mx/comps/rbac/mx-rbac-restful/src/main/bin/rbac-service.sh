#!/bin/bash

JAVA_HOME="/opt/jdk"
RBAC_HOME="/opt/rbac-service"
APP="org.mx.comps.rbac.RBACRestApplication"

CLASSPATH=$RBAC_HOME/conf/.
for jarFile in $RBAC_HOME/libs/*.jar; do
  CLASSPATH=$CLASSPATH:$jarFile
done

case "$1" in
    start)
        echo "JAVA_HOME: $JAVA_HOME"
        echo "CLASSPATH: $CLASSPATH"
        cd $RBAC_HOME
        $JAVA_HOME/bin/java -cp $CLASSPATH $APP -type jpa >> $RBAC_HOME/logs/start.log 2>&1 &
        ;;
    stop)
        pid=`ps -ef | grep rbac.RBACRestApplication | awk '{print $2}'`
        kill -9 $pid
        ;;
    *)
        echo $"Usage: $0 {start|stop}"
        exit 2
esac

exit 0