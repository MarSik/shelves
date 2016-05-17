# Shelves backend docker container

This is a containerized version of the [Shelves part tracking service](http://www.shelves.cz) API backend. The easiest way to start it up is using Docker compose and some ansible bits.

## Prepare a configuration directory

Fist you need to create a directory (lets remember the path as shelves\_dir\_with\_config) that will contain the file with secret values. This file needs to be called application.properties and I would recommend to set 0700 permissions on the directory.

The template config file is here:

```
google.oauth2.client.id={{ shelves_sec_google_oauth_id }}
google.oauth2.client.secret={{ shelves_sec_google_oauth_secret }}
google.oauth2.donePage=https://{{ shelves_backend_hostname }}/federated/google/done

github.oauth2.client.id={{ shelves_sec_github_oauth_id }}
github.oauth2.client.secret={{ shelves_sec_github_oauth_secret }}

mailgun.key={{ shelves_sec_mailgun_key }}
mailgun.url=https://api.mailgun.net/v2/{{ shelves_sec_mailgun_domain }}/messages

spring.datasource.username={{ service }}
spring.datasource.password={{ shelves_sec_database_password }}

shelves.security.loginPage=https://{{ shelves_frontend_hostname }}/
shelves.security.authPage=https://{{ shelves_frontend_hostname }}/
shelves.security.donePage=https://{{ shelves_frontend_hostname }}/
```

## Start all necessary containers

I use docker compose to orchestrate the full environment. The API server needs MariaDB database, memcached server and possibly a reverse proxy. See the template Docker compose file here:

```
memcached:
  image: memcached:latest
  restart: always

mariadb:
  image: mariadb:10.1
  restart: always
  volumes:
    - "shelves-database-volume:/var/lib/mysql"
  environment:
    MYSQL_USER: "shelves"
    MYSQL_PASSWORD: "{{ shelves_sec_database_password }}"
    MYSQL_DATABASE: "shelves"
    MYSQL_ROOT_PASSWORD: "{{ shelves_sec_database_root_password }}"

backend:
  image: marsik/shelves-backend
  restart: always
  links:
    - "memcached:memcached"
    - "mariadb:mariadb"
  expose:
    - 8080
    # DEBUG PORT - 8000
  volumes:
    - "{{ shelves_dir_with_config }}:/etc/shelves"
    - "shelves-data-volume:/shelves"
  environment:
    VIRTUAL_HOST: "{{ shelves_backend_hostname }}"
    LETSENCRYPT_HOST: "{{ shelves_backend_hostname }}"
    LETSENCRYPT_EMAIL: "{{ shelves_backend_email }}"
    VIRTUAL_PORT: 8080
    MEMCACHED_SERVER: memcached
    MYSQL_SERVER: mariadb
    MYSQL_DATABASE: "shelves"

```

## Expose the service

I recommend using jwilder/nginx-proxy reverse proxy together with JrCs/docker-letsencrypt-nginx-proxy-companion that adds the SSL support. I start the proxy using the following ansible playbook:

```
- name: Create the Lets encrypt certificate directory
  file:
    path: /etc/ssl/certs/letsencrypt
    mode: 0700
    state: directory

- name: Create vhost customization directory
  file:
    path: /etc/nginx/vhost.d
    mode: 0755
    state: directory

- name: Nginx reverse proxy
  docker:
    name: nginx-reverse-proxy
    image: jwilder/nginx-proxy
    net: bridge
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - /etc/ssl/certs/letsencrypt:/etc/nginx/certs:ro
      - /etc/nginx/vhost.d:/etc/nginx/vhost.d:ro
      - /usr/share/nginx/html
      - /var/run/docker.sock:/tmp/docker.sock:ro
    pull: always
    restart_policy: always
    restart_policy_retry: 20
    state: reloaded

- name: Lets encrypt certificate service
  docker:
    name: letsencrypt-nginx-proxy-companion
    image: jrcs/letsencrypt-nginx-proxy-companion
    net: bridge
    volumes:
      - /etc/ssl/certs/letsencrypt:/etc/nginx/certs:rw
      - /etc/nginx/vhost.d:/etc/nginx/vhost.d:rw
      - /var/run/docker.sock:/var/run/docker.sock:ro
    volumes_from:
      - nginx-reverse-proxy
    pull: always
    restart_policy: always
    restart_policy_retry: 20
    state: reloaded
```


