# Multicast Multi-User Chat Protocol

## About
Multicast Multi-User Chat (McMUC) is a distributed, non-reliable application-
layer protocol that uses UDP at the transport layer and provides multi- user
chat room creation, discovery, and the exchange of user presence information and
messaging without a central server.

## Running
Build the package:

    $ cd mcmuc
    $ ant

Run the `.jar`:

    $ cd dist
    $ java -jar mcmuc.jar
