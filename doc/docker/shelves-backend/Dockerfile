FROM java:openjdk-8-jre-alpine

# Data and logging directory
RUN mkdir -p /shelves/log
RUN mkdir -p /shelves/data
VOLUME /shelves

# Configuration directory
# - create a volume or directory
# - populate it using the shelves-configuration container
# - mount the result to /etc/shelves
RUN mkdir -p /etc/shelves
VOLUME /etc/shelves

# Main app port
EXPOSE 8080

# Debug port
EXPOSE 8000

ADD run.sh /bin/run.sh

ENV JAVA_MIN_MEM 128m
ENV JAVA_MAX_MEM 3096m

ENTRYPOINT ["/bin/run.sh"]

ENV VERSION latest
ENV RELEASE_API https://api.github.com/repos/MarSik/shelves/releases/$VERSION

# Add and explode the server.war to /server.war directory
RUN apk add --no-cache --virtual .build-deps curl \
 && curl -L -O https://github.com/stedolan/jq/releases/download/jq-1.5/jq-linux32 \
 && chmod a+rx jq-linux32 \
 && mkdir -p /server.war \
 && curl -L -s $RELEASE_API \
    | ./jq-linux32 --raw-output '.assets[] | .browser_download_url | select(test("server"))' \
    | xargs curl -L -o server-dl.war \
 && unzip -d /server.war server-dl.war \
 && apk --no-cache del .build-deps \
 && rm -f jq-linux32 server-dl.war

# In case you want to use a locally built file, comment the above block
# and add the file here:
# ADD server-1.0-SNAPSHOT.war /
# RUN mkdir -p /server.war && unzip -d /server.war server-*.war

