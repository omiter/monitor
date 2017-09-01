#!/bin/bash
DIR=`cd  $(dirname $0)&& cd .. && pwd`
source $DIR/bin/config.sh

function pid(){
 PID=`jps -ml |grep $EXEC_FILE|awk '{print $1}'`
 echo $PID 
}

function appid(){
  APPID=`grep "state:"  $DIR/logs/${LOG_NAME}.log |sed -n "1p"|awk '{print $8}'`
  echo $APPID
}

function start(){
  PID=`pid`
  if [ -z "$PID" ];then
    echo "spark-submit --master $MASTER --files $LOG4J --conf "spark.driver.extraJavaOptions=-Dlog4j.configuration=file:$LOG4J"  --executor-memory $EX_MEM --num-executors $NUM_EXE --driver-memory $DRIVER_MEM  --class $CLASS   $DIR/lib/$EXEC_FILE $PARAM  > $DIR/logs/${LOG_NAME}.log 2>$DIR/logs/${LOG_NAME}_ERR.log &"
    spark-submit --master $MASTER --files $LOG4J  --conf "spark.driver.extraJavaOptions=-Dlog4j.configuration=file:$LOG4J"  --executor-memory $EX_MEM --num-executors $NUM_EXE --driver-memory $DRIVER_MEM  --class $CLASS   $DIR/lib/$EXEC_FILE $PARAM  > $DIR/logs/${LOG_NAME}.log 2>$DIR/logs/${LOG_NAME}_ERR.log &
    sleep 1
    PID=`pid`
    echo "$EXEC_FILE is running $PID ... "
  else
    echo "$EXEC_FILE pid is $PID"
    echo "$EXEC_FILE is start failed! log path:$DIR/logs"
  fi
}

function stop(){
  PID=`pid`
  APPID=`appid`
  if [ $PID  ];then
    yarn application -kill $APPID
    echo "yarn application -kill $APPID"
    kill -9 $PID
    echo "$EXEC_FILE is running pid:$PID ,is killed!"
  fi
}

function status(){
   APPID=`appid`
   sta=`yarn application -status $APPID|egrep "State :|Final-State :"|awk '{print $3}'|awk BEGIN{RS=EOF}'{gsub(/\n/,":::");print}'`
   echo "$EXEC_FILE $sta $APPID"
}

case $1 in
  start)  start ;;
  stop)    stop ;;
  status)  status ;;
  restart)  stop && start ;;
  *)       echo "Usage: start|stop|restart|status";;
esac

