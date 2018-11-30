# Proxy Server Project
## A project for further understanding of proxy servers, written in java

Name       : Michael Bido-Chavez
Class      : CSCE 3530


## Files

Client.java
PServer.java
pkg + (a directory)
    |-- PServerThread.java
    |-- ForwardCacheResponse.java
compile.sh
README.txt

## Description

The proxy server will wait for a client connection, and then accept a URL 
string. The server will then forward that string to the host for a 
HTTP response code. If the returned code is 200, cache the web page
if it's not already in the cache, and then forward it to the client. 
Repeat until client types 'bye' to disconnect. Even if the client
disconnects, the server will still run.

The Client enters a URL to send the the proxy server, and, if
the response code is 200, then receive the web page from the proxy
server. 

The Proxy Server accepts a URL, forwards it to the host, receives 
a HTTP response code, and, if 200, cache the web page (up to the 
5 most recent) and then forward it to the client.

## How to Compile

Place all files within the same directory in the cse01.cse.unt.edu 
machine, and then enter that directory. Then, type the following:

javac pkg/*.java; 
javac PServer.java
javac Client.java

Alternatively, you can run attached 'compile.sh' file.

## How to Execute

Run the server before running the client. Type the following 
lines to execute each program.

java PServer <port_number>
java Client <port_number>

For the port number, enter 13004 (it's what I used to test as port 80 was in use while testing).
For the client, enter any URL, just be sure to include the http:// or https://
For the client, type ‘bye’ to disconnect and end the process.
For the server, press Control+C to stop the server and end the process.

## Useful Sources for This Program

https://www.tutorialspoint.com/java/java_arraylist_class.htm
https://www.tutorialspoint.com/java/java_files_io.htm
https://www.tutorialspoint.com/java/java_networking.htm
https://docs.oracle.com/javase/tutorial/networking/urls/
https://docs.oracle.com/javase/tutorial/essential/io/index.html
https://docs.oracle.com/javase/tutorial/networking/sockets/index.html
