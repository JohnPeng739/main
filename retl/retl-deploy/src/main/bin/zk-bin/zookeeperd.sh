#!/usr/bin/env bash

cd /opt/zookeeper

case $1 in
    start)
        bin/zkServer.sh start
        ;;
    stop)
        bin/zkServer.sh stop
        ;;
    retart)
        bin/zkServer.sh restart
        ;;
    *)
        echo "Usage: $0 {start | stop | restart}" >&2
esac
