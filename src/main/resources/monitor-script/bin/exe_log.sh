t1=`date +%s`
t2=`expr $t1 - 65`
end1=`date -d @$t1 +"%Y-%m-%d %H:%M:%S"`
end2=`date -d @$t1 +"%Y-%m-%dT%H:%M:%S"`
start1=`date -d @$t2 +"%Y-%m-%d %H:%M:%S"`
start2=`date -d @$t2 +"%Y-%m-%dT%H:%M:%S"`
hm=`date +"%H:%M"`
yesterday=`date -d "-1 days" +"%Y-%m-%d"`
# log size limit 10M
size_limit=10485760
#size_limit=20000
ERROR="ERROR|Exception:"
GREPV="mail|INFO"
LOG_NUM=9
ssh $3@$1 << EOF
  source /etc/profile;
  i=0
  r=-1
  function log(){
    ((i++)) 
    ((r++)) 
    local logfile=\$(ls -l \$1 |awk '{print \$9}'|awk -F"." '{print \$1"'\.'"\$2}')
    exsit=\$(ls -l \${logfile}.\${i})
    if [[ -n "\$exsit"  ]];then
       \$(log \$logfile.\${i})
    fi
    if [[ \$i -gt $LOG_NUM  ]];then
      rm -rf   \${logfile}.\${i}
      return
    fi
    if [[ \$r = 0 ]];then
      cat \${logfile} > \${logfile}.\${i}
    else
      mv \${logfile}.\${r}  \${logfile}.\${i}
    fi
    if [[ \$i -eq 1 ]];then
      cp /dev/null  \$logfile
    fi
  }
  datetime=\$(date +"%Y-%m-%d")
  log_name=\$(sed -n '/LOG_NAME/p' $2/bin/config.sh |awk -F "[\"\"]" '{print \$2}');
  log_dir=$2/logs/\${log_name}_ERR.log
  num=\$(cat \$log_dir|wc -l) 
  err1=""
  if [ \$num -gt 0 ];then
    cat \$log_dir >> $2/logs/\${log_name}_ERR_\${datetime}.log
    err1=\$(cat \$log_dir|egrep -v "$GREPV"|egrep  -A 50  "$ERROR" |awk BEGIN{RS=EOF}'{gsub(/\\n/,":::");print}')
    >\$log_dir
  fi
  rownum=\$(cat $2/logs/rownum)
  count=\$(cat $2/logs/\${log_name}.log|wc -l)
  if [[ -z "\$rownum" || \$rownum -gt \$count  ]];then
    rownum=0
    count=0
  fi
  err2=\$(awk -F, 'NR>'\$rownum'' $2/logs/\${log_name}.log |egrep -v "$GREPV"|egrep  -A 50  "$ERROR"|awk BEGIN{RS=EOF}'{gsub(/\\n/,":::");print}')
   \$(echo \$count > $2/logs/rownum)
  
  size=\$(ls -l $2/logs/\${log_name}.log|awk '{print \$5}')
  if [ "\$size"  -gt "$size_limit"  ];then
     n=$2/logs/\${log_name}.log
     \$(log \$n)
     \$(echo 0 > $2/logs/rownum)
  fi

  if [[ -n \$err1 ||-n \$err2  ]];then
    echo \$err1:::\$err2
  else
   echo "0"
  fi
  exit;
EOF

