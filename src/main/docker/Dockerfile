FROM java:8
VOLUME /tmp
ADD kettle-manager-0.0.1.jar app.jar
RUN sh -c 'touch /app.jar'
VOLUME  /app/config
ENV JAVA_OPTS=""
ENV PROFILE="prod"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar -Dspring.profiles.active=prod -Dspring.config.location=/app/config " ]
