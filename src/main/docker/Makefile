SHELL=/bin/bash

VERSION=$(shell cat ../../../VERSION)

WAR=GostService\#$(VERSION).war
TARGET=../../../$(WAR)

IMAGE=gost

docker: copy
	docker build --build-arg VERSION=$(VERSION) -t lappsgrid/$(IMAGE) .

start:
	docker run --dns 8.8.8.8 -d -p 8080:8080 --name gost lappsgrid/$(IMAGE)

stop:
	docker rm -f gost

test:
	@echo "Hello $(WAR)"

copy:
	if [ $(TARGET) -nt $(WAR) ] ; then \
		echo "target is newer." \
		#cp $(TARGET) . \
	else \
		echo "no need to copy war." \
	fi

tag:
	docker tag lappsgrid/gost
	
update:
	curl -X POST http://129.114.17.83:9000/api/webhooks/780d7c20-dad9-4259-ae55-a2c1d485f421
