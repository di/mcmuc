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
    
## Sending encrypted messages
Two sample public and private keys have provided to test the sending and receiving of encrypted messages:
cacert.pem (the public key) and cakey.p8c (the private key)

To register the public/private keypair on the receiving end, issue the following command:
add-key @<room-name> public='cacert.pem' private='cakey.p8c'

Any other client may now send an encrypted message to that client with the following command:
message key='cakey.p8c' <user-name>@<room-name> <message>
