#!/bin/bash

param="$4"
ssh "$3"@"$1"<<EOF
  source /etc/profile;
  state_dir="$2/state/";
  filetimestamp=\$(stat -c %Y \$state_dir/monitor${param}.state)
  currtimestamp=\$(date +%s)
  ((t=\$currtimestamp - \$filetimestamp))
  if [ "\$t" -gt 70  ];then
    echo 0
  else
    info=\$(cat \$state_dir/monitor${param}.state)  
    echo \$info
  fi
  exit;
EOF 
