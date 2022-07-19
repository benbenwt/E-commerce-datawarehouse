#! /bin/bash
case $1 in
"start"){
        for i in hbase2
        do
                echo " --------启动 $i 消费flume-------"
                ssh $i "nohup /root/module/flume/bin/flume-ng agent --conf-file /root/module/flume/conf/kafka-flume-hdfs.conf --name a1 -Dflume.root.logger=INFO,LOGFILE >/root/module/flume/log2.txt   2>&1 &"
        done
};;
"stop"){
        for i in hbase2
        do
                echo " --------停止 $i 消费flume-------"
                ssh $i "ps -ef | grep kafka-flume-hdfs | grep -v grep |awk '{print \$2}' | xargs  kill -9"
        done

};;
esac
