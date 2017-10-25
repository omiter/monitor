#!/bin/bash
DIR=$(cd `dirname $0` && cd .. && pwd)
split="~~~"
    echo `date +"%Y-%m-%d %H:%M:%S"`" collect application info..."
    sh $DIR/bin/monitor.sh $DIR/conf/list.conf 2>/dev/null|awk BEGIN{RS=EOF}'{gsub(/\n/,"'$split'");print}' >$DIR/state/monitor
    cat $DIR/state/monitor > $DIR/state/monitor.state
    echo `date +"%Y-%m-%d %H:%M:%S"`" collect application Log info..."
    sh $DIR/bin/monitor_log.sh $DIR/conf/list.conf 2>/dev/null|awk BEGIN{RS=EOF}'{gsub(/\n/,"'$split'");print}' >$DIR/state/monitor_log
    cat $DIR/state/monitor_log > $DIR/state/monitor_log.state
