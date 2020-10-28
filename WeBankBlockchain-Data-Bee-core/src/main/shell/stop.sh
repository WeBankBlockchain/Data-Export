#!/usr/bin/env bash

for i in $(ps -ef|grep supervisord|grep -i bee| awk '{print $2}')
do
    kill -9 $i
done

for j in $(ps -ef|grep -i `pwd`|grep -i webase-collect-bee|grep -v grep| awk '{print $2}')
do
    kill -9 $j
done
echo "Stop succeed!"
