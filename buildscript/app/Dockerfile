FROM openjdk:8
VOLUME /tmp
ENV LANG C.UTF-8
ADD app.jar /app.jar
ADD driver /driver
ADD launch.sh /launch.sh
EXPOSE 9000
RUN chmod -R 777 /driver
ENTRYPOINT ["sh", "/launch.sh"]