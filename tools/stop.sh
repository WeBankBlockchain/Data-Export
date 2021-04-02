#!/bin/bash
ps -ef|grep Data-Export |grep -v grep| awk '{print $2}'|xargs kill -9