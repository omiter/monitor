#!/bin/bash
DIR=`cd  $(dirname $0)&& cd .. && pwd`

FILE=$1



while read LINE
do
  ARR=($LINE)
  if [[ "${ARR[0]}" = "#"  ]];then
    continue
  fi
  info=`sh $DIR/bin/exe.sh ${ARR[0]} ${ARR[1]} ${ARR[4]} |grep -v login`
  echo ${ARR[0]}===${ARR[3]}===${ARR[2]}===$info
done < $FILE


