#!/usr/bin/env bash

# constant set
export JOB_REAL="real-time"
export JOB_BATCH="batch"

export PRO_JAVA="java"
export PRO_SPARK="spark"
export PRO_BASH="bash"

#DIR set
export DIR=`cd  $(dirname $0)&& cd .. && pwd`
export BIN=${DIR}/bin
export CONF=${DIR}/conf
export LIB=${DIR}/lib
export LOGS=${DIR}/logs


#job type (cat constant.sh)
export JOB_TYPE=$JOB_REAL
export PRO_TYPE=$PRO_BASH

# program set
export EXEC="sh"
export EXEC_FILE="cron.sh"
export LOG_NAME="cron"
# start with "-" param append on $EXEC_FILE 
export PARAM=""


#java param
export MAIN_CLASS=""

#spark param
export MASTER="--master yarn-client"
export EX_MEM="--executor-memory 4G"
export NUM_EXE="--num-executors 4"
export DRIVER_MEM="--driver-memory 4G"
export SPARK_CONF=""
export CLASS=""
