This directory contains the ansible script to deploy the Shelves service using
docker.

It consists of two main roles you can apply to your hosts:

shelves-backend
shelves-frontend

The scripts require Ansible 2.0 and Docker 1.9

You can customize all aspects of the services using environment variables.

There are also two helper roles:
- mariadb role if you want to use Dockerized mariadb database.
- docker role if you need to make sure docker is installed and up
- prometheus role to install time series database and grafana viewer
- memcached role to install the memory caching store

