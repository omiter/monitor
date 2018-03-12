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
# 0:离线任务日志同时打印到控制台和文件，且不后台运行（用于azkaban调度） 
# 1:离线任务日志只打印到文件，后台运行
export BATCH_LOG_CONSOLE=1

export KILL=""

#job type (cat constant.sh)
export JOB_TYPE=
export PRO_TYPE=

# program set
export EXEC=""
export EXEC_FILE=""
export LOG_NAME=""
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
