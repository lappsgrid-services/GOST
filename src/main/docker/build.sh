#!/usr/bin/env bash

# See http://stackoverflow.com/questions/59895/getting-the-source-directory-of-a-bash-script-from-within
SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
  DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
  SOURCE="$(readlink "$SOURCE")"
  [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
done
DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"

cd $DIR

VERSION=$(cat ../../../VERSION)
BUILD=$(cat ../../../build)

if [ "$VERSION" = "" ] ; then
	VERSION="1.0.0"
	echo "Using default version $VERSION"
fi

WAR="GostService#$VERSION.war"
TARGET="../../../target/$WAR"

REPO=docker.lappsgrid.org
ORG=lappsgrid
IMAGE=gost

function build() {
	if [ ! -e $WAR ] || [ $TARGET -nt $WAR ] ; then
		echo "Getting newest war file."
		cp -p $TARGET .
	fi
	echo "Building the Docker image"
	docker build --build-arg VERSION=$VERSION -t $ORG/$IMAGE .
}

if [ $# -eq 0 ] ; then
	build
	exit 0
fi

case $1 in
	docker)
		build
		;;
	start)
		docker run --dns 8.8.8.8 -d -p 8080:8080 --name $IMAGE $ORG/$IMAGE
		;;
	stop)
		docker rm -f $IMAGE
		;;
	login)
	    docker exec -it $IMAGE bash
	    ;;
	tag)
		docker tag $ORG/$IMAGE $REPO/$ORG/$IMAGE
		;;
	push)
		docker push $REPO/$ORG/$IMAGE
		;;
	update)
		curl -X POST http://129.114.17.83:9000/api/webhooks/780d7c20-dad9-4259-ae55-a2c1d485f421
		;;
	*)
		echo "Invalid command $1"
		exit 1
		;;
esac
