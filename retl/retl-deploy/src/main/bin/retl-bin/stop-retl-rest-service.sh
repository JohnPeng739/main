#!/bin/bash

app_name="[c]om.ds.retl.RETLRestApplication"
pid=`ps -ef | grep $app_name | awk '{print $2}'`
if [ "${pid}"x != ""x ]; then
  kill -9 $pid
  echo "Stop the DS RETL Restful Application Service successfully."
else
  echo "The process: $app_name not found."
fi
