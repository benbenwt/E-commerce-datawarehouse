#!/bin/bash
case $1 in
"start"){
  for i in hbase hbase1
  do 
    echo "--- start $i flume ---"
    ssh $i "nohup /root/module/flume/bin/flume-ng agent --conf-file /root/module/flume/conf/file-flume-kafka.conf --name a1 -Dflume.root.logger=INFO,LOGFILE > /root/module/flume/log1.txt 2>&1 &"
  done
};;
"stop"){
  for i in hbase hbase1
  do
    echo " --------停止 $i 采集flume-------"
                ssh $i "ps -ef | grep file-flume-kafka | grep -v grep |awk  '{print \$2}' | xargs kill -9 "
  done
}
esac
