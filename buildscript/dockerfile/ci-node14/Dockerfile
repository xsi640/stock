FROM node:14
ENV LANG C.UTF-8
COPY sources.list /etc/apt/sources
RUN apt-get update
ENV DOCKERVERSION=18.03.1-ce
RUN curl -fsSLO https://download.docker.com/linux/static/stable/x86_64/docker-${DOCKERVERSION}.tgz \
    && tar xzvf docker-${DOCKERVERSION}.tgz --strip 1 \
                 -C /usr/local/bin docker/docker \
    && rm docker-${DOCKERVERSION}.tgz
RUN npm install -g nrm \
    && nrm add localnpm  http://172.16.11.231:8081/nexus/repository/npm-group/ \
    && nrm use localnpm