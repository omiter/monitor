#!/bin/bash
DIR=`cd  $(dirname $0)&& cd .. && pwd`
source $DIR/bin/config.sh

function pid(){
 PID=`jps -ml |grep $EXEC_FILE|awk '{print $1}'`
 echo $PID 
}

function start(){
  PID=`pid`
  if [ -z "$PID" ];then
    echo "java -jar $EXEC_FILE $PARAM  > $DIR/logs/${LOG_NAME}.log 2>&1 &"
    java -jar $DIR/lib/$EXEC_FILE $PARAM  > $DIR/logs/${LOG_NAME}.log 2>$DIR/logs/${LOG_NAME}_ERR.log &
    sleep 1
    PID=`jps -ml |grep $EXEC_FILE|awk '{print $1}'`
    echo "$EXEC_FILE is running $PID ... "
  else
    echo "$EXEC_FILE pid is $PID"
    echo "$EXEC_FILE is start failed! log path:$DIR/logs"
  fi
}

function stop(){
  PID=`pid`
  kill -9 $PID
  echo "$EXEC_FILE is running pid:$PID ,is killed!"
}

function status(){
  PID=`pid`
  if [ -n "$PID"  ];then
    echo "$EXEC_FILE running $PID "
  else
    echo "$EXEC_FILE stopped"
  fi
}


case $1 in
  start)  start ;;
  stop)    stop ;;
  status)  status ;;
  restart)  stop && start ;;
  *)       echo "Usage: start|stop|restart|status";;
esac
