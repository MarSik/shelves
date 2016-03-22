#!/bin/sh

export DIR_CONFIG="$HOME/etc"
export DEPLOY="$HOME/deploy/server.war"
export LOG_FILE="$HOME/log/shelves.log"

umask 0077
mkdir -m 0700 -p $DIR_DATA

JAVA_OPTS="-Xms128m -Xmx3072m -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=127.0.0.1:8000,suspend=n"
MAINCLASS=$(grep "Main-Class: " "$DEPLOY/META-INF/MANIFEST.MF" | cut -d ' ' -f2 | sed -e 's/[[:space:]]//g')

java $JAVA_OPTS -cp "$DEPLOY" -Dlogging.config=etc/logback.xml -Ddebug -Dserver.port=8080 -Dspring.config.location=$DIR_CONFIG/ $MAINCLASS &
PID=$!

trap "kill -$PID" SIGINT SIGTERM EXIT
trap "kill 0" SIGINT SIGTERM EXIT

wait $PID

