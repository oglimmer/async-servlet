#!/bin/bash

mvn compile

java -cp target/classes/ de.oglimmer.client.Client "$@"

