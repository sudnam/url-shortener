Url Shortener
===

A basic HTTP url shortener which takes as input a long url and returns a short url.
When a GET request is then made on the short url the user will be redirected
to the long url.

There are two HTTP calls that can be made:

**PUT /?url=<long_url>**


Used to create a short url for a long url. The incoming url parameter should contain
the long url that should be converted to a short url. Will return a 204 Created with
the short url in the location field on success.

**GET /<hash>**

Used to be redirected to a long url given its hash. The hash parameter is generated
in the create call and part of the short url returned.

Dependencies
---

- Java 8
- Scala 2.11
- SBT 0.13
- *Optional: Redis*

Implementation
---

The implementation is done using Finagle and currently based on a Redis database
for storage of mappings between short and long urls. The main functionality can
be found in the class UrlShortenerService where all the magic of converting a short
url to a long one resides (to make the service more general the hashing should be
moved out to a separate class extending a trait with a method for the transformation,
but this is left as an exercise to the reader :)).

The database interface was made generic via the Database trait which makes it quite
easy to implement new connections to some kind of storage. Currently the server
supports in memory storage (which isn't very scalable) and Redis. The Redis storage
does not use any sort of clustering or similar functionality at the moment, but with
the current implementation it should be quite easy to add support for that as well to
make the availability of the application much better by not relying on a single
database host.

Running
---

Compiling the code can be done by issuing the following command in the root directory
of the project:

```
sbt compile
```

To run unit tests issue the following command:

```
sbt test
```

To run integration tests (requires an installation of Redis and the command redis-server
to be available in the path) issue the following command:

```
sbt it:test
```

To run the server itself issue the following command:

```
sbt "run-main com.gnifin.url_shortener.Main --help"
```

This will print all the arguments that may be sent as input to the service to configure
it in different ways.

[![Build Status](https://travis-ci.org/sudnam/url-shortener.svg?branch=master)](https://travis-ci.org/sudnam/url-shortener)
