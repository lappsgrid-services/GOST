
# Build the deployable war file.
war:
	awk '{printf("%d\n", $$1+1)}' build > build.tmp && mv build.tmp build
	cp build src/main/resources
	mvn package

clean:
	mvn clean
	rm src/main/docker/*.war

# Goals for building and deploying the Docker image.
docker:
	src/main/docker/build.sh

tag:
	src/main/docker/build.sh tag

push:
	src/main/docker/build.sh push

# Sends a POST to Portainer which causes it to pull the latest Docker image.
update:
	src/main/docker/build.sh update

# Connecting to the Docker image.
start:
	src/main/docker/build.sh start

stop:
	src/main/docker/build.sh stop

login:
	src/main/docker/build.sh login

# Goals to run various test scripts.
execute:
	lsd src/test/lsd/execute.lsd 

lookup:
	lsd src/test/lsd/execute.lsd lookup

ne:
	lsd src/test/lsd/execute.lsd ne

metadata:
	lsd src/test/lsd/metadata.lsd

