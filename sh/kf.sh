#! /bin/bash

case $1 in
"start"){
    for i in hbase hbase1 hbase2
    do
        echo " --------启动 $i Kafka-------"
        ssh $i "/root/module/kafka_2.12-2.4.1//bin/kafka-server-start.sh -daemon /root/module/kafka_2.12-2.4.1//config/server.properties"
    done
};;
"stop"){
    for i in hbase hbase1 hbase2
    do
        echo " --------停止 $i Kafka-------"
        ssh $i "/root/module/kafka_2.12-2.4.1//bin/kafka-server-stop.sh stop"
    done
};;
esac

