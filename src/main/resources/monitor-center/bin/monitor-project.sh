#/bin/bash
DIR=$(cd `dirname $0` && cd .. && pwd)
param=$2
if [[  $param = "log"  ]];then
  param="_log"
else
  param=""
fi

while read LINE
do
  arr=($LINE)
  info=`sh  $DIR/bin/exec.sh ${arr[0]} ${arr[1]} ${arr[3]} $param |grep -v login`
  if [  "$info" == 0  ];then
     echo 0###${arr[0]}###${arr[1]}###${arr[2]}###${arr[3]}
  else
     echo 1###$info
  fi
done < $1
