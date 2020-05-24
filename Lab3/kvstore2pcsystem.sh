#!/bin/sh

JAR_PATH=./kvstore2pcsystem.jar
 
#    $1  config_path
 
java -jar -Dconfig_path=$1 $JAR_PATH 
 
exit $?
