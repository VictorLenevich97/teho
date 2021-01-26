#!/bin/sh
nohup docker-entrypoint.sh postgres &

while true
do
sleep 5
java -jar teho-1.0-RELEASE.jar
done
