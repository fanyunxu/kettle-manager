FROM tomcat
ADD kettle-manager.war km.war
RUN sh -c 'mv km.war /usr/local/tomcat/webapps/'
#ENTRYPOINT [ "sh", "-c", "/usr/local/tomcat/bin/start.sh" ]
EXPOSE 8080
