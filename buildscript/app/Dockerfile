FROM openjdk:8
VOLUME /tmp
ENV LANG C.UTF-8
ADD app.jar /app.jar
ADD launch.sh /launch.sh
ENV ACTIVE prod
EXPOSE 9000
ENTRYPOINT ["sh", "/launch.sh"]