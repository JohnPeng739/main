#!/usr/bin/env bash

STORM_HOME="/opt/storm"
STORM_CMD="${STORM_HOME}/bin/storm"

RETL_HOME="${STORM_HOME}/retl"
RETL_JAR="${RETL_HOME}/retl-platform-3.0.0.jar"
RETL_DEPS="${RETL_HOME}/deps"
MAIN_CLASS="com.ds.retl.ETLTopologyBuilder"
#CONFIG_FILE="${RETL_HOME}/config/etl-topology-config-sample2.json"
CONFIG_FILE=$1

if [ "${CONFIG_FILE}" == "" ]; then
  echo "!!!!!!!! no config file"
  echo "Usage: submitTopology.sh config_file"
  exit -1
fi

# add --jars
JARS=`ls ${RETL_HOME}/deps | grep "..*\.jar$"`
DEP_JARS=
for JAR in ${JARS}
do
  DEP_JARS="${DEP_JARS},${RETL_DEPS}/${JAR}"
done
DEP_JARS=`echo ${DEP_JARS:1}`

echo "###################################################"
echo "CMD:     ${STORM_CMD}"
echo "JAR:     ${RETL_JAR}"
echo "MAIN:    ${MAIN_CLASS}"
echo "CONFIG:  ${CONFIG_FILE}"
echo "DEPS:    ${DEP_JARS}"
echo "###################################################"

$STORM_CMD jar $RETL_JAR $MAIN_CLASS $CONFIG_FILE --jars $DEP_JARS 
exit 0
