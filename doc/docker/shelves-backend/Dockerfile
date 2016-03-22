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

# ADD and explode the server.war to /server.war directory
ENTRYPOINT ["/bin/run.sh"]

