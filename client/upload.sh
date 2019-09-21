#!/bin/bash

#mvn compile

java -cp target/classes de.oglimmer.client.post.PostClient "File" "$@"
