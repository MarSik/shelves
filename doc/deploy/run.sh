#!/bin/sh

export DIR_CONFIG="$HOME/etc"
export DIR_DATA="$HOME/data"
export DIR_STATIC="$HOME/static"
export DEPLOY="$HOME/deploy/backend.war"

umask 0077
mkdir -m 0700 -p $DIR_DATA

JAVA_OPTS="-Xms128m -Xmx3072m -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=127.0.0.1:8000,suspend=n"
MAINCLASS=$(grep "Main-Class: " "$DEPLOY/META-INF/MANIFEST.MF" | cut -d ' ' -f2 | sed -e 's/[[:space:]]//g')

java $JAVA_OPTS -cp "$DEPLOY" -Dlogging.config=etc/logback.xml -Ddebug -Dserver.port=8080 -Dorg.marsik.elshelves.services.backend.static="$DIR_STATIC" -Dorg.marsik.elshelves.services.backend.config="$DIR_CONFIG" -Dorg.marsik.elshelves.services.backend.data="$DIR_DATA" $MAINCLASS &
PID=$!

trap "kill -$PID" SIGINT SIGTERM EXIT
trap "kill 0" SIGINT SIGTERM EXIT

wait $PID

