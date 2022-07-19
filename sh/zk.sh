#!/bin/bash

case $1 in
"start"){
	for i in hbase hbase1 hbase2
	do
        echo ---------- zookeeper $i 启动 ------------
		ssh $i "/root/module/apache-zookeeper-3.5.9-bin/bin/zkServer.sh start"
	done
};;
"stop"){
	for i in hbase hbase1 hbase2
	do
        echo ---------- zookeeper $i 停止 ------------    
		ssh $i "/root/module/apache-zookeeper-3.5.9-bin/bin/zkServer.sh stop"
	done
};;
"status"){
	for i in hbase hbase1 hbase2
	do
        echo ---------- zookeeper $i 状态 ------------    
		ssh $i "/root/module/apache-zookeeper-3.5.9-bin/bin/zkServer.sh status"
	done
};;
esac

