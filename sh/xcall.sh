#! /bin/bash

for i in  hbase hbase1 hbase2
do
  echo --- $i ---
  ssh $i "$*"
done
