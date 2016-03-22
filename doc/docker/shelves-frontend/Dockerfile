FROM nginx:alpine

COPY nginx.conf /etc/nginx/nginx.conf
RUN mkdir -p /etc/nginx/conf.d
COPY shelves.conf /etc/nginx/conf.d/

EXPOSE 8080

# Explode the web.war to /web.war/ directory
# TODO enable API endpoint customization using environment variable

# The recommended usage is behind a SSL proxy
