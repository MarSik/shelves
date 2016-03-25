FROM marsik/config-volume-creator

ENV MARIADB_SERVER=localhost
ENV MARIADB_DATABASE=elshelves
ENV MARIADB_USERNAME=root
ENV MARIADB_PASSWORD=""

ENV MAILGUN_API_KEY=""
ENV MAILGUN_API_DOMAIN=""

ENV GOOGLE_OAUTH_ID=""
ENV GOOGLE_OAUTH_SECRET=""

ENV GITHUB_OAUTH_ID=""
ENV GITHUB_OAUTH_SECRET=""

# The url of the main Shelves web application
# connected to this backend
ENV WEB_APP_URL="http://a.shelves.cz/"


ADD *.xml /sources/
ADD *.properties /sources/

