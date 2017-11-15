#!/usr/bin/env bash

cd /opt/storm

echo "*****************************************"
echo "Start storm services..."

SVRS=`cat conf/services.conf`
for SVR in $SVRS
do
  echo "starting storm service: $SVR..."
  bin/storm $SVR > logs/$SVR-startup.log 2>&1 &
done
echo "Start storm services successfully."
