# Frontend container for Shelves

This is a containerized version of the [Shelves part tracking service](http://www.shelves.cz) frontend. The easiest way to start it up is using Docker compose and some ansible bits.

The recommended usage is behind a SSL proxy (like properly configured nginx).

## Example docker compose file for starting the frontend

```
frontend:
  restart: always
  image: marsik/shelves-frontend
  environment:
    API_SERVER: "https://{{ shelves_backend_hostname }}"
    API_BASE: ""
    API_NAMESPACE: "v1"
    VIRTUAL_HOST: "{{ shelves_frontend_hostname }}"
    VIRTUAL_PORT: 80
    LETSENCRYPT_HOST: "{{ shelves_frontend_hostname }}"
    LETSENCRYPT_EMAIL: "{{ shelves_frontend_email }}"
  expose:
    - 80

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
