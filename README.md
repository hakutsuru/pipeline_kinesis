AWS Kinesis Testing
========

[Kinesis](http://aws.amazon.com/kinesis/) is a message log similar to [Apache Kafka](http://kafka.apache.org) and [NSQ](https://github.com/bitly/nsq).

The rational for testing Kinesis is the product is operationally simpler than Kafka.
In terms of capacity planning, Kinesis has understandable limits, and there seems
to be simple steps to adjust the number of stream shards.

There are two concerns:

- Reliability: Though unlikely, any issue with Kinesis would lead to analytic data
loss. Anticipated counter arguments -- Kinesis failure would be associated with
"bigger problems" and the service is inexpensive.

- Complexity: There is the danger of increased complexity due to the limitations
of the service. Event messages larger than 50,000 characters would require segment
management in producers and consumers.

Here is the [Kinesis API Reference](http://docs.aws.amazon.com/kinesis/latest/APIReference/Welcome.html).

The tests provided use [amazonica](https://github.com/mcohen01/amazonica).

Test Preliminaries & Requirements
---------------------------------

Instructions for testing with Vagrant are provided. If you wish to test elsewhere,
the requirements are Java 7 and Leiningen.

Clojure was chosen for testing, as I work with a team that uses the language.

These are my first Clojure programs, and must have obvious flaws. But the aim
was to test Kinesis, and show how this could be done simply using Clojure.

Log into AWS Console, and create a Kinesis stream named "kinesis-api-test"
in Oregon (us-west-2). Of course, you may alter these via environment
variables, or changing the testing source code.

The consumer sleep duration should not be set less than 200 milliseconds
as shards are restricted to five read operations per second.

Testing will require two terminal sessions, one for the producer, and another
for the consumer. I describe the simple case of using two windows, if you 
are using tmux, then you should feel confident about skipping needless
steps in these instructions.

Testing with Vagrant
--------------------

First, launch and provision the data-pipeline environment:

    $ cd pipeline_kinesis/vagrant/data-pipeline
    $ vagrant up

Copy the kinesis_test directory to the data-pipeline directory.

Consumer Testing
----------------

Via terminal window one...

    $ vagrant ssh
    $ export AWS_ACCESS_KEY_ID=[...]
    $ export AWS_SECRET_KEY=[...]
    $ cd /vagrant/kinesis_test/consumer-testing/
    $ lein run

You should see output similar to this (note that it can take up to 10s after
messages are published to the log for them to become available):

```
vagrant@node1:/vagrant/kinesis_test/consumer-testing$ lein run
Retrieving [...]
Jul 20, 2014 2:05:55 AM com.amazonaws.AmazonWebServiceClient setRegion
INFO: {kinesis, us-west-2} was not found in region metadata, trying to construct an endpoint using the standard pattern for this region: 'kinesis.us-west-2.amazonaws.com'.

hey now, let us consume!
streams available: [kinesis-api-test]

{"response":{"status":200,"headers":{"link":"</search?q=hee&group-by=kind&page=1>"},"body":{"list":[],"track":[],"release":[],"mix":[],"genre":[],"account":[],"best-match":null}},"ip":"127.0.0.1","user-agent":"","method":"get","events":"","duration":"69ms","http-server":"org.eclipse.jetty.server.HttpInput@3b702644","function-times":{},"id":"2014-07-20-api-usw1a-001-00000000","action":"Request","time-unix":1405822088,"uri":"/search","user":"marlboro"}

{"response":{"status":200,"headers":{"link":"</search?q=hee&group-by=kind&page=1>"},"body":{"list":[],"track":[],"release":[],"mix":[],"genre":[],"account":[],"best-match":null}},"ip":"127.0.0.1","user-agent":"","method":"get","events":"","duration":"69ms","http-server":"org.eclipse.jetty.server.HttpInput@3b702644","function-times":{},"id":"2014-07-20-api-usw1a-001-00000001","action":"Request","time-unix":1405822088,"uri":"/search","user":"marlboro"}

[...]
```

Producer Testing
----------------

Via terminal window two...

    $ vagrant ssh pipeline
    $ export AWS_ACCESS_KEY_ID=[...]
    $ export AWS_SECRET_KEY=[...]
    $ cd /vagrant/kinesis_test/producer-testing/
    $ lein run

You should see output similar to this:

```
vagrant@node1:/vagrant/kinesis_test/producer-testing$ lein run
Retrieving [...]
Jul 20, 2014 2:08:09 AM com.amazonaws.AmazonWebServiceClient setRegion
INFO: {kinesis, us-west-2} was not found in region metadata, trying to construct an endpoint using the standard pattern for this region: 'kinesis.us-west-2.amazonaws.com'.

hey now, we must produce!
streams available: [kinesis-api-test]

{"response":{"status":200,"headers":{"link":"</search?q=hee&group-by=kind&page=1>"},"body":{"list":[],"track":[],"release":[],"mix":[],"genre":[],"account":[],"best-match":null}},"ip":"127.0.0.1","user-agent":"","method":"get","events":"","duration":"69ms","http-server":"org.eclipse.jetty.server.HttpInput@3b702644","function-times":{},"id":"2014-07-20-api-usw1a-001-00000000","action":"Request","time-unix":1405822088,"uri":"/search","user":"marlboro"}
==> event <2014-07-20-api-usw1a-001-00000000> published to aws kinesis

{"response":{"status":200,"headers":{"link":"</search?q=hee&group-by=kind&page=1>"},"body":{"list":[],"track":[],"release":[],"mix":[],"genre":[],"account":[],"best-match":null}},"ip":"127.0.0.1","user-agent":"","method":"get","events":"","duration":"69ms","http-server":"org.eclipse.jetty.server.HttpInput@3b702644","function-times":{},"id":"2014-07-20-api-usw1a-001-00000001","action":"Request","time-unix":1405822088,"uri":"/search","user":"marlboro"}
==> event <2014-07-20-api-usw1a-001-00000001> published to aws kinesis

[...]
```

References
----------

* [Upstream Documentation](http://aws.amazon.com/kinesis/)

Acknowledgments
---------------

The basic project structure follows that devised by my teammate @elasticdog (author
of transcrypt). Though over-engineered for this simple test-demo, I chose to keep
the structure, as it emphasizes the technologies involved.
