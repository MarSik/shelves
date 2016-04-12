#!/bin/sh
sed -i -e "s/ENV VERSION .*/ENV VERSION $2/g" doc/docker/*/Dockerfile

