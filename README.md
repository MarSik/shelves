# Shelves

Part and project tracking REST-ful web application.

Distributed under the terms of Affero GPLv3 or later license.

[![Build Status](https://travis-ci.org/MarSik/shelves.svg?branch=master)](https://travis-ci.org/MarSik/shelves)

## Development

### Version bump

Version should be changed using the following maven command:

mvn versions:set -DnewVersion=<new version>

### Requirements

* Java 1.8 SDK (OpenJDK works fine)
* Maven 3
* MySQL
  * create empty elshelves\_unittest database, with root:<no password> access
* Memcached

### Source requirements

Please use the following script to install the special versions of some dependencies:

```
git clone https://github.com/pauloubuntu/identicon.git
pushd identicon
mvn clean install
popd
```

### Build

Just type `mvn clean install` to generate all necessary files.

### Deploy

There are two files that contain all that is needed for deploy:

* web/target/web-1.0-SNAPSHOT.war
* backend/server/target/backend-1.0-SNAPSHOT.war

You can extract the files from web.war and serve them using any simple http
server like nginx. You can find the example configuration in doc/nginx.

The backend.war file is an executable Java archive and the example startup
script is located in doc/deploy. The script is designed to use an exploded
war (unzip the .war file somewhere and set the DEPLOY variable in the run
script accordingly).


