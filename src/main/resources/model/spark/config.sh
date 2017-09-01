#!/bin/bash

export DIR=`cd  $(dirname $0)&& cd .. && pwd`

export LOG4J="log4j.properties"
export EXEC_FILE="big-screen-1.0.jar"
export LOG_NAME="big-screen"
export PARAM=


export MASTER="yarn-client"
export EX_MEM="4G"
export NUM_EXE="4"
export DRIVER_MEM="4G"
export CLASS="com.gome.screen.main.BigScreenStreaming"
