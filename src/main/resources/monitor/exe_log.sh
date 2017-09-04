#!/usr/bin/env bash


ssh root@$1 << EOF
  source /etc/profile;
  datetime=\$(date +"%Y-%m-%d")
  log_name=\$(sed -n '/LOG_NAME/p' $2/bin/config.sh |awk -F "[\"\"]" '{print \$2}');
  log_dir=$2/logs/\${log_name}_ERR.log
  num=\$(cat \$log_dir|wc -l) 
  if [ \$num -gt 0 ];then
    cat \$log_dir >> $2/logs/\${log_name}_ERR_\${datetime}.log
    info=\$(awk BEGIN{RS=EOF}'{gsub(/\\n/,":::");print}' \$log_dir)
    > \$log_dir
    echo \$info
  else
   echo "0"
  fi
  exit;
EOF

