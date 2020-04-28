#!/bin/sh
#HttpServer.sh

JAR_PATH=./HttpServer.jar
 
#    $1  port
#    $2  number-thread
 
java -jar -Dport=$1 -Dnumber-thread=$2 $JAR_PATH 
 
exit $?
