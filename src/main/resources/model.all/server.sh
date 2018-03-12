#!/bin/bash
DIR=`cd  $(dirname $0)&& cd .. && pwd`
source ${DIR}/bin/config.sh
source /etc/profile
export EXEC_FILE=${EXEC_FILE//  / }
export EXEC_FILE=${EXEC_FILE//  / }
# get program pid
function pid(){
 P=`echo $PARAM|sed 's/-D//'`
 case ${PRO_TYPE} in
     ${PRO_BASH})
         PID=`ps -ef |grep "${BIN}/${EXEC_FILE}"|grep -v grep|awk '{print $2" "$3}'`
         ;;
     ${PRO_SPARK})
         PID=`ps -ef |grep "${LIB}/${EXEC_FILE}"|grep "$P"|grep -v grep|awk '{print $2" "$3}'`
         ;;
     ${PRO_JAVA})
         PID=`ps -ef |grep "${LIB}/${EXEC_FILE}"|grep "$P"|grep -v grep|awk '{print $2" "$3}'`
         ;;
     *)
         echo "${PRO_TYPE} type program is not support get PID!"
         PID=""
         ;;
 esac
 local pid=($PID)
 if [ "$1" = "parent" ];then
   echo "${pid[1]}"
 else
   echo "${pid[0]}"
 fi
}

# get spark applicationId
function appid(){
  if [ "${PRO_SPARK}" = ${PRO_TYPE} ];then
    APPID=`grep "state:"  ${LOGS}/${LOG_NAME}.log |sed -n "1p"|awk '{print $8}'`
    if [ -z "$APPID" ];then
       APPID=`grep "state:"  ${LOGS}/${LOG_NAME}_ERR.log |sed -n "1p"|awk '{print $8}'`
    fi
  else
     echo "$PRO_TYPE is not support get applicationId!"
     APPID=""
  fi
  if [[ -z "$APPID" && "${PRO_SPARK}" = ${PRO_TYPE}  ]];then
     APPID=`cat $LOGS/appid.aid`
  else
     echo $APPID > $LOGS/appid.aid
  fi
  echo ${APPID}
}

function start_model(){
  local COMMAND="$1"
  local LOGFILE="$2"
  local ERR_LOGFILE="$3"
  if [[ "$BATCH_LOG_CONSOLE" = 0 && "$JOB_TYPE" = $JOB_BATCH ]];then 
     echo "$COMMAND 2>&1 |tee -a $LOGFILE"
     $COMMAND 2>&1 |tee -a $LOGFILE
     echo "exec end..."
     exit
  else
     echo "nohup $COMMAND 1>>$LOGFILE 2>>$ERR_LOGFILE &"
     nohup $COMMAND 1>>$LOGFILE 2>>$ERR_LOGFILE &
  fi
}

function start(){
   PID=`pid`
  if [ -z "${PID}" ]; then
      RUN=""
      case ${PRO_TYPE} in
       ${PRO_BASH})
           RUN="${EXEC} ${BIN}/${EXEC_FILE} ${PARAM}"
           start_model "${RUN}" "${LOGS}/${LOG_NAME}.log" "${LOGS}/${LOG_NAME}.log"
           ;;
       ${PRO_JAVA})
          if [ -z "$MAIN_CLASS" ];then
          RUN="${EXEC} -jar ${LIB}/${EXEC_FILE} ${PARAM}"
          start_model "${RUN}" "${LOGS}/${LOG_NAME}.log" "${LOGS}/${LOG_NAME}_ERR.log"
          else
          RUN="${EXEC} -cp ${LIB}/${EXEC_FILE} ${MAIN_CLASS}  ${PARAM}"
          start_model "${RUN}" "${LOGS}/${LOG_NAME}.log" "${LOGS}/${LOG_NAME}_ERR.log"
          fi
          ;;
       ${PRO_SPARK})
          RUN="${EXEC} $MASTER $DRIVER_MEM $NUM_EXE $EX_MEM $SPARK_CONF $CLASS $LIB/$EXEC_FILE $PARAM"
          > ${LOGS}/${LOG_NAME}.log
          start_model "${RUN}" "${LOGS}/${LOG_NAME}.log" "${LOGS}/${LOG_NAME}.log"
          ;;
        *)
          echo "$PRO_TYPE program is not support server.sh start!"
          exit 1
        ;;
      esac
      sleep 2
      PID=`pid`
      echo "${EXEC} ${BIN}/${EXEC_FILE} ${PARAM} is running $PID ..."
  else
     echo "${EXEC} ${BIN}/${EXEC_FILE} ${PARAM} PID get failed!"
     echo "please scan $LOGS/$LOG_NAME.log ..."
  fi
}

function killparent(){
  echo "kill param: $KILL"
  if [[ "$BATCH_LOG_CONSOLE" = 0 && "$JOB_TYPE" = $JOB_BATCH ]];then
    local PID=`pid parent`
     if [ "$PID" != "1"  ];then
        kill $KILL $PID
     else
        echo "PID=$PID cannot be killed!"
     fi    
     kill $KILL "$1"
  else
    local PID="$1"
    kill $KILL $PID
  fi
}

function stop(){
  PID=`pid`
  APPID=`appid`
  rm -rf $LOGS/appid.aid
  case ${PRO_TYPE} in
   ${PRO_BASH})
      if [ -n "${PID}"  ]; then
         killparent $PID 
         echo "$BIN/$EXEC_FILE is running pid:$PID ,is killed!"
      else
         echo "PID: $PID is not exist!"
      fi
      ;;
    ${PRO_JAVA})
      if [ -n "${PID}"  ]; then
          killparent $PID
          echo "$LIB/$EXEC_FILE is running pid:$PID ,is killed!"
      else
         echo "PID: $PID is not exist!"
      fi
      ;;
    ${PRO_SPARK})
      if [ -n "${APPID}"  ]; then
          killparent $PID
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
  restart)  stop && sleep 2s && start ;;
  *)       echo "Usage: start|stop|restart|status";;
esac

