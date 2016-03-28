#!/bin/sh

DIR_CONFIG="/etc/shelves"
DEPLOY="/server.war"
DIR_LOG="/shelves/log"
LOG_FILE="$DIR_LOG/shelves.log"
DIR_DATA="/shelves/data"

umask 0077
mkdir -m 0700 -p $DIR_LOG
mkdir -m 0700 -p $DIR_DATA

umask 0077
mkdir -m 0700 -p $DIR_DATA

JAVA_OPTS="-Xms$JAVA_MIN_MEM -Xmx$JAVA_MAX_MEM -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=0.0.0.0:8000,suspend=n"
MAINCLASS=$(grep "Main-Class: " "$DEPLOY/META-INF/MANIFEST.MF" | cut -d ' ' -f2 | sed -e 's/[[:space:]]//g')

exec java $JAVA_OPTS -cp "$DEPLOY" \
-Dlogging.config=$DIR_CONFIG/logback.xml \
-Ddebug \
-Dserver.port=8080 \
-Dspring.config.location=$DIR_CONFIG/ \
-Dstorage.path=$DIR_DATA \
"-Dshelves.memcached.server=$MEMCACHED_SERVER" \
"-Dspring.datasource.url=jdbc:mysql://$MYSQL_SERVER/$MYSQL_DATABASE?useUnicode=yes&characterEncoding=UTF-8" \
$MAINCLASS "$@"

