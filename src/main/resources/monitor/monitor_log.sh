#!/bin/bash
DIR=`cd  $(dirname $0)&& cd .. && pwd`

FILE=$1



while read LINE
do
  ARR=($LINE)
  info=`sh $DIR/bin/exe_log.sh ${ARR[0]} ${ARR[1]}`
  echo ${ARR[0]},${ARR[3]},${ARR[2]},$info
done < $FILE


