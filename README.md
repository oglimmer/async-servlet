This simulates a slow backend and how an asynchronous servlet processing make things better
===========================================================================================

what is does
------------

- the client spawns 500 threads and does http requests (connect timeout=1s / read timeouts=8s)
- the server offers two endpoints
  - /dataSync which processes the http requests synchronously
  - /dataAsync which processes the http requests asynchronously
- both endpoints assume that every 5th call is very slow and thus takes ~5 seconds, while all the other calls return immediately


how to run
----------

1.) start the webserver

$ mvn jetty:run

2.) wait a couple of seconds and then start the client using the asynchronous processing

$ ./run.sh dataAsync

3.) as you see all requests return successfully 

4.) stop the client (ctrl-c) and start the client again, now using the synchronous processing

$ ./run.sh dataSync

5.) watch the world burn
