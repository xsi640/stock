#!/bin/bash

export DOCKER_HOST=tcp://${docker_host}

docker stop ${app_name} || true
docker rm -f ${app_name} || true
docker rmi -f ${docker_image} || true

docker run --name='${app_name}' -d \
--restart=always \
-v /etc/localtime:/etc/localtime \
-e ACTIVE=${active} \
--network net-local \
--network-alias ${app_name} \
${docker_image}