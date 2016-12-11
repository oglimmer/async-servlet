#!/bin/bash

if [ ! -f target/classes/de/oglimmer/client/Client.class ]; then
	mvn compile
fi

java -cp target/classes/ de.oglimmer.client.Client "$@"

