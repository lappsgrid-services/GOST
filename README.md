# Gene Ontology Semantic Tagger

A LAPPS Grid SOAP wrapper for the REST service provided by [Lancaster University](http://ucrel.lancs.ac.uk).  GOST performs entity identification and semantic markup with detailed semantic categories in biomedical texts, using a set of over 200 semantic labels from http://ucrel.lancs.ac.uk/usas/semtags.txt as well as entites and processes from the [Gene Ontology](http://geneontology.org).

## TL ; DR

```
$> make clean war docker tag push
```

## Prerequisites

To build this project you will need:

1. Maven 3.x (or later)
1. Docker

The Makefile assumes Linux or a MacOS based system.

## Goals

The following *goals* are available when running Make.

* **war**<br/>
Generates the WAR file. This is the default goal.
* **clean**<br/>
Remove build artifacts.
* **docker**<br/>
Builds the Docker image.
* **tag**<br/>
Tags the latest Docker images
* **push**<br/>
Pushes the latest Docker image to docker.lappsgrid.org
* **update**<br/>
Sends a *POST* message to the Docker swarm manager (Portainer) to tell it to pull the latest Docker image.

The following *goals* are used to start and stop the Docker container on your local machine.

* **start**<br/>
Starts the Docker container.  The GOST service will be available on http://localhost:8080/GostService/1.0.0-SNAPSHOT/services/GostService
* **stop**<br/>
Stops the running Docker container.
* **login**<br/>
Connect to a *bash* shell inside a running Docker container.



