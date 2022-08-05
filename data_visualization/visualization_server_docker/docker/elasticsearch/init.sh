#!/bin/bash
#nohup  /usr/local/kibana-7.11.1-linux-x86_64/bin/kibana --allow-root  &
echo "test" >/home/elasticsearch/test.txt
tail -f /home/elasticsearch/test.txt
