#!/usr/bin/env bash

ssh $3@$1 << EOF
  source /etc/profile;
  
  exec_file=\$(sed -n '/EXEC_FILE/p' $2/bin/config.sh |awk -F "[\"\"]" '{print \$2}');
  info=\$($2/bin/server.sh status|grep "\$exec_file"|sed -n '1p');
  echo \$info;
  if [ -z "\$info"  ];then
    echo "UNDEFINED"
  fi
  exit;
EOF

