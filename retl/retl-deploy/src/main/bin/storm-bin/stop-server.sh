#!/usr/bin/env bash

cd /opt/storm

echo "*************************************************************"
echo "Stop storm services..."

SVRS=`cat conf/services.conf`
for SVR in $SVRS
do
  echo "stopping storm service: $SVR..."
  SVRN=
  case $SVR in
    nimbus)
      SVRN="daemon.nimbus"
      ;;
    ui)
      SVRN="ui.core"
      ;;
    logviewer)
      SVRN="daemon.logviewer"
      ;;
    supervisor)
      SVRN="daemon.supervisor"
      ;;
    *)
      echo "Unsupported service: $SVR"
      continue
      ;;
  esac
  kill -9 `ps -ef | grep $SVRN | awk '{print $2}'`
done
echo "Stop storm services successfully."
