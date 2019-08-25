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

0.) start the slow backend server

$ cd fake-backend; ./gradlew bootRun

1.) start the webserver

$ mvn jetty:run

2.) wait a couple of seconds and then start the client using the asynchronous processing

$ ./run.sh async 2000

3.) as you see all requests return successfully 

4.) stop the client (ctrl-c) and start the client again, now using the synchronous processing

$ ./run.sh sync 2000

5.) watch the world burn
