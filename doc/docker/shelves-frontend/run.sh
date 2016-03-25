#!/bin/sh
cat >$(ls -1 /web.war/config*.js) <<EOF
// Use this file as a template when the app needs to be pointed to
// a different server, for example when dockerizing the app
window.SHELVES_API_SERVER="${API_SERVER}";
window.SHELVES_API_BASE="${API_BASE}";
window.SHELVES_API_NAMESPACE="${API_NAMESPACE}";
EOF
echo "Configuration done, starting server."
exec nginx -g "daemon off;" "$@"

