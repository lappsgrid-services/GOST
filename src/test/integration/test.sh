#!/usr/bin/env bash

docker run -d -p 8080:8080 --name tomcat -v target:/var/lib/tomcat7/webapps lappsgrid/tomcat7:1.1.0
sleep 2
lsd src/test/lsd/metadata.lsd
docker rm -f tomcat
