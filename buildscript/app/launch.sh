#!/bin/bash
if [ -z $CONFIG_FILE ]; then
  /bin/sh -c "java -Duser.timezone='Asia/Shanghai' -Dfile.encoding=UTF-8 -Djava.security.egd=file:/dev/./urandom -jar /app.jar"
else
  /bin/sh -c "java -Duser.timezone='Asia/Shanghai' -Dfile.encoding=UTF-8 -Djava.security.egd=file:/dev/./urandom -jar /app.jar --spring.config.location=$CONFIG_FILE"
fi