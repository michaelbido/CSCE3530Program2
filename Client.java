/* 
Compilation: javac Client.java
Execution  : java Client <port_number>

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

This is the Client who makes a request by entering a URL, and, if
the response code from the proxy server is 200, then receive the 
web page from the proxy server
-----------------------------
*/

import java.io.*;
import java.net.*;

public class Client
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

	public static void main(String args[])
	{
		// check if command line input is valid
		if (args.length > 0) {
			try {
                final int p = Integer.parseInt(args[0]);
                setPort(p);
			} 
			catch (NumberFormatException e) {
				System.err.println("Command Line error: " + args[0] + " must be an integer number. Try again");
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
        
		try
		{
			// Connecting with the server
			// String host = "localhost"; // Both in the same machine
            String host = "129.120.151.94"; //IP address of server, cse01.cse.unt.edu
            
            System.out.println("Connecting to " + host + ":" + getPort());
	
			InetAddress address = InetAddress.getByName(host);
			socket = new Socket(address, port);

			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			int length;

			boolean endServerConnection = false;
			
			// Sending the message (URL) to the server
			while (!endServerConnection) {
				// Read input
				System.out.println("------------------ To exit, type 'bye' ------------------");
				System.out.print("Enter a URL to request, ( include http:// or https:// ): ");
				String sendMessage = br.readLine();
				// System.out.print("To exit, type 'bye'. Enter a URL to request: http://");				
				// String sendMessage = "http://" + br.readLine();
				
				// Convert and send input to server
				byte[] smessage = new byte[256];
				smessage = sendMessage.getBytes();
				out.writeInt(smessage.length);
				out.write(smessage);
				out.flush();			
				System.out.println("Message sent to the server: " + sendMessage);
				
				// End the loop if the user prints bye
				if (sendMessage.equals("bye")) {
					endServerConnection = true;
				}

				// Receiving the message (the response code) from the server
				length = in.readInt();
				byte[] rmessage = new byte[256];
				in.readFully(rmessage, 0, length);
				String recvMessage = new String(rmessage);
				recvMessage = recvMessage.substring(0, recvMessage.indexOf('\0'));				
				
                // Print message from server, the status code received
				System.out.println("Server - Result of " + sendMessage + " is: " + recvMessage);

				// if the returned status code is 200, then read data from server
				// and store into file with local. at the front, just to differentiate between
				// objects on proxy and ojects that run from the client
				if (recvMessage.compareTo("200") == 0) {
					System.out.println("Reponse code = 200. Downloading Web Page...");
					// create a filename with local. at front, based on requested url
					File fileName = new File("local." + sendMessage.split("://",2)[1] + ".html");
					FileOutputStream fileStream = new FileOutputStream(fileName);	
					BufferedOutputStream fileStreamOut = new BufferedOutputStream(fileStream);
					// make buffer large enough for file transfer
					// up to 4 Mb, should be large enough
					byte[] fileBuffer = new byte[4194303];
					int bufferAmount = in.readInt();
					// store to file
					// while ((bufferAmount = in.read(fileBuffer)) > 0 ) {
					in.readFully(fileBuffer, 0, bufferAmount);
					fileStream.write(fileBuffer, 0, bufferAmount);
					//}
					
					fileStream.close();
					fileStreamOut.close();
				}
			}
			// Closing the connection
			socket.close();

			System.out.println("Closing connection. Ending Program.");
		}
		catch (Exception e)
		{
			System.out.println("Client socket error:" + e);
		}
	}
}
