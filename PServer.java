/* 
Compilation: javac PServer.java
Execution  : java PServer <port_number>

Name       : Michael Bido-Chavez (euid: mb0501)
Due Date   : Oct. 21, 2017
Class      : CSCE 3530

Proxy Server Program - Server
-----------------------------
Description:
The proxy server will wait client connection, and then accept a URL 
string. The server will then forward that string to the host for a 
HTTP response code. If the returned code is 200, cache the web page
if it's not already in the cache, and then forward it to the client. 
Repeat until client types 'bye' to disconnect. Even if the client
disconnects, the server will still run.

This is the Server handler, binds socket and accepts connections
before handing process to each thread.
-----------------------------
*/

import java.io.*;
import java.net.*;

import pkg.*;

public class PServer
{
	private static Socket socket;

	private static int port;
	// getters and setters
	public static int getPort() {
		return port;
	}
	public static void setPort(int p) {
		port = p;
	}
 
	public static void main(String[] args)
	{
		int threadID = 0;

		// check if command line input is valid
		if (args.length > 0) {
			try {
				final int p = Integer.parseInt(args[0]);
				setPort(p);
			} 
			catch (NumberFormatException e) {
				System.err.println("Command Line error: " + args[0] + " must be an integer number. Try again.");
				System.exit(1);
			}
		}
		else {
			System.out.println("Please enter a port number: PServer <port_number>");
			System.exit(2);
		}

		// check if it's a valid port range
		if (getPort() < 1024 || getPort() >= 65535) {
			System.out.println("Port number error:" + port + " must be between 1024 and 65535");
			System.exit(3);
		}

		try {
			// Connecting with the client
			ServerSocket serverSocket = new ServerSocket(getPort());
			System.out.println("Proxy Server started and listening to the port " + getPort());
		
			//Server is always running. This is done using this while(true) loop
			while(true) {
				//Connecting to the client
				socket = serverSocket.accept();

				DataInputStream in = new DataInputStream(socket.getInputStream());
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());

				// start thread
				threadID++;
				PServerThread s = new PServerThread(in, out, threadID);
				s.start();
				// Closing the connection
				// socket.close();
			}
		}
		catch (Exception e) {
			System.out.println("Server socket error!");
		}
	}
}