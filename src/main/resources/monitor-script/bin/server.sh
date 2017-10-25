#!/bin/bash
DIR=`cd  $(dirname $0)&& cd .. && pwd`
source ${DIR}/bin/config.sh
# get program pid
function pid(){
 case ${PRO_TYPE} in
     ${PRO_BASH})
         PID=`ps -ef |grep "${BIN}/${EXEC_FILE}"|grep -v grep|awk '{print $2}'`
         ;;
     ${PRO_SPARK})
         PID=`ps -ef |grep "${LIB}/${EXEC_FILE}"|grep "$PARAM"|grep -v grep|awk '{print $2}'`
         ;;
     ${PRO_JAVA})
         PID=`ps -ef |grep "${LIB}/${EXEC_FILE}"|grep "$PARAM"|grep -v grep|awk '{print $2}'`
         ;;
     *)
         echo "${PRO_TYPE} type program is not support get PID!"
         PID=""
         ;;
 esac

 echo "${PID}"
}

# get spark applicationId
function appid(){
  if [ "${PRO_SPARK}" = ${PRO_TYPE} ];then
     APPID=`grep "state:"  ${LOGS}/${LOG_NAME}.log |sed -n "1p"|awk '{print $8}'`
  else
     echo "$PRO_TYPE is not support get applicationId!"
     APPID=""
  fi
  echo ${APPID}
}

function start(){
   PID=`pid`
  if [ -z "${PID}" ]; then
      RUN=""
      case ${PRO_TYPE} in
       ${PRO_BASH})
           RUN="nohup ${EXEC} ${BIN}/${EXEC_FILE} ${PARAM}"
           `${RUN} >> ${LOGS}/${LOG_NAME}.log  2>&1 &`
           ;;
       ${PRO_JAVA})
          if [ -z "$MAIN_CLASS" ];then
          RUN="nohup ${EXEC} -jar ${LIB}/${EXEC_FILE} ${PARAM}"
          `${RUN} >> ${LOGS}/${LOG_NAME}.log  2>> ${LOGS}/${LOG_NAME}_ERR.log &`
          else
          RUN="nohup ${EXEC} -cp ${LIB}/${EXEC_FILE} ${MAIN_CLASS}  ${PARAM}"
          `${RUN} >> ${LOGS}/${LOG_NAME}.log  2>> ${LOGS}/${LOG_NAME}_ERR.log &`
          fi
          ;;
       ${PRO_SPARK})
          RUN="nohup ${EXEC} $MASTER $DRIVER_MEM $NUM_EXE $EX_MEM $SPARK_CONF $CLASS $LIB/$EXEC_FILE $PARAM"
          > ${LOGS}/${LOG_NAME}.log
          `${RUN} >> ${LOGS}/${LOG_NAME}.log  2>> ${LOGS}/${LOG_NAME}_ERR.log &`
          ;;
        *)
          echo "$PRO_TYPE program is not support server.sh start!"
          exit 1
        ;;
      esac
      echo ${RUN} is start ...
      sleep 2
      PID=`pid`
      echo "${EXEC} ${BIN}/${EXEC_FILE} ${PARAM} is running $PID ..."
  else
     echo "${EXEC} ${BIN}/${EXEC_FILE} ${PARAM} is running $PID ..."
     echo "please scan $LOGS/$LOG_NAME.log ..."
  fi
}



function stop(){
  PID=`pid`
  APPID=`appid`
  case ${PRO_TYPE} in
   ${PRO_BASH})
      if [ -n "${PID}"  ]; then
          `kill -9 ${PID}`
          echo "$BIN/$EXEC_FILE is running pid:$PID ,is killed!"
      else
         echo "PID: $PID is not exist!"
      fi
      ;;
    ${PRO_JAVA})
      if [ -n "${PID}"  ]; then
          `kill -9 ${PID}`
          echo "$LIB/$EXEC_FILE is running pid:$PID ,is killed!"
      else
         echo "PID: $PID is not exist!"
      fi
      ;;
    ${PRO_SPARK})
      if [ -n "${APPID}"  ]; then
          `yarn application -kill ${APPID}`
          echo "$LIB/$EXEC_FILE is running appid:$APPID ,is killed!"
       else
         echo "APPID: $APPID is not exist!"
      fi
      ;;
    *)
          echo "$PRO_TYPE program is not support server.sh stop!"
          exit 1
      ;;
    esac
}

function status(){
  PID=`pid`
  APPID=`appid`
  case ${PRO_TYPE} in
   ${PRO_BASH})
      if [ -n "${PID}"  ]; then
         echo "$BIN/$EXEC_FILE,running,$PID"
      else
         if [ "${JOB_TYPE}" = ${JOB_BATCH} ];then
            echo "${BIN}/${EXEC_FILE},succeeded,-100"
         else
            echo "${BIN}/${EXEC_FILE},stopped"
         fi
      fi
      ;;
    ${PRO_JAVA})
      if [ -n "${PID}"  ]; then
         echo "$LIB/$EXEC_FILE,running,$PID"
      else
         if [ "${JOB_TYPE}" = ${JOB_BATCH} ];then
            echo "${BIN}/${EXEC_FILE},succeeded,-100"
         else
            echo "${BIN}/${EXEC_FILE},stopped"
         fi
      fi
      ;;
    ${PRO_SPARK})
      if [ -n "${APPID}"  ]; then
         sta=`yarn application -status ${APPID}|egrep "State :|Final-State :"|awk '{print $3}'|awk BEGIN{RS=EOF}'{gsub(/\n/,":::");print}'`
         if [ -z "$sta" ];then
            sta="stopped"
         fi
         echo "$LIB/$EXEC_FILE,$sta,$APPID"
       else
         echo "$LIB/$EXEC_FILE,Not-Exist,-100"
      fi
      ;;
    *)
          echo "$PRO_TYPE program is not support server.sh status!"
          exit 1
      ;;
    esac




}

case $1 in
  start)  start ;;
  stop)    stop ;;
  status)  status ;;
  restart)  stop && start ;;
  *)       echo "Usage: start|stop|restart|status";;
esac

