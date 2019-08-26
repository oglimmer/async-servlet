This simulates a slow backend and how an asynchronous servlet processing make things better
===========================================================================================

what is does
------------

- the client spawns 500 threads and does http requests (connect timeout=5s / read timeouts=60s)
- the server offers two endpoints
  - /sync which processes the http requests synchronously
  - /async which processes the http requests asynchronously
- both endpoints calls the "slow backend server at 9090", those calls take ~5 seconds


how to run
----------

1.) start the slow backend server

$ cd fake-backend; ./gradlew bootRun

2.) start the webserver

$ cd api; ./gradlew bootRun

3.) wait a couple of seconds and then start the client using the asynchronous processing

$ cd client; ./run.sh async 2000

4.) as you see all requests return successfully 

5.) Now using the synchronous processing

$ cd client; ./run.sh sync 2000

.... watch the world burn

p.s. in all fairness it really depends on the some parameters:

* number of concurrent client connections (here 500)
* read timeout of the client (here 15 seconds)
* number of worker threads in the servlet container (here 50)
