FROM lappsgrid/tomcat7:1.2.3

ARG VERSION

RUN mkdir -p /var/log/gost && chown tomcat7:tomcat7 /var/log/gost
COPY GostService#$VERSION.war /var/lib/tomcat7/webapps
