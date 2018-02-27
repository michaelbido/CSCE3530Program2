/* 
Compilation: javac pkg/*.java;
Part of the import package.

Name       : Michael Bido-Chavez (euid: mb0501)
Due Date   : Oct. 21, 2017
Class      : CSCE 3530

Proxy Server Program - Server Thread
-----------------------------
Description:
The proxy server will wait client connection, and then accept a URL 
string. The server will then forward that string to the host for a 
HTTP response code. If the returned code is 200, cache the web page
if it's not already in the cache, and then forward it to the client. 
Repeat until client types 'bye' to disconnect. Even if the client
disconnects, the server will still run.

Thread handles all of the processing, uses additional methods
from ForwardCacheResponse.java (import pkg.ForwardCacheResponse)
-----------------------------
*/

package pkg;

import java.io.*;
import java.net.*;
// import java.util.*;

import pkg.ForwardCacheResponse;

public class PServerThread extends Thread
{
    // variables
    int length;
    int threadID;
    boolean endClientConnection;
    boolean isCached;    
	DataInputStream in;
	DataOutputStream out;
    BufferedReader br;	
    int statusCode;
    // thread
	private Thread t;
	// private String threadName;
    // constructor, receives input/output data stream from socket
    public PServerThread(DataInputStream dataIn, DataOutputStream dataOut, int threadNum) {
        try {
            in = dataIn;
            out = dataOut;
            endClientConnection = false;
            isCached = false;
			threadID = threadNum;
			br = new BufferedReader(new InputStreamReader(System.in));
        }   
        catch(Exception e) {
            System.out.println("There was a problem with creating a thread");
        }     
    }
    // run the thread
    public void run() {
        System.out.println("Connection established. Running thread " + threadID + ".");
        try {
            while(!endClientConnection) {
                System.out.println("Awaiting client request...");                
				// Reading the message (URL) from the client
				length = in.readInt();
				byte[] rmessage = new byte[256];
				in.readFully(rmessage, 0, length);
				String recvMessage = new String(rmessage);
                // Display message from client
				System.out.println("Message received from a client: " + recvMessage);				
				recvMessage = recvMessage.substring(0, recvMessage.indexOf('\0'));
                // Class object contains methods for getting web page and creating files
                ForwardCacheResponse test = new ForwardCacheResponse(recvMessage);

                String sendMessage;			

				// if the client wishes to end connection, send goodbye message and end it for good				
                if (recvMessage.equals("bye")) {
                    // end loop
                    endClientConnection = true;
                    statusCode = 0;
                    // send outgoing message
                    sendMessage = "Goodbye, I'll miss you!";
                    System.out.println("Client said bye, saying bye back. Closing client connection...");
                }
                else {
                    // Reteive HTTP response code, display appropriate responses
                    statusCode = test.getUrlResponseCode();

                    if (statusCode == 0) {
                        sendMessage = "Invalid URL, DNS address could not be found.";
                        System.out.println("Based on input " + recvMessage + ", the DNS address could not be found.");                        
                        
                    }
                    else {
                        System.out.println("Based on input " + recvMessage + ", response code is " + statusCode);                        
                        sendMessage = Integer.toString(statusCode);
                    }
				}
				// send message back
				byte[] smessage = new byte[256];
				smessage = sendMessage.getBytes();
				out.writeInt(smessage.length);
				out.write(smessage);
                out.flush();
                // if status code is 200, send the web page to client
                if (statusCode == 200) {
                    // check if cached, if so skip, otherwise get web page
                    isCached = test.checkIfCached();  
                    if (!isCached) {
                        test.getWebData();
                    }
                    // Makes files stream into byte stream to be sent to client
                    System.out.print("Sending web page to Client... ");
                    File fileName = new File(recvMessage.split("://",2)[1] + ".html");
                    BufferedInputStream fileStream = new BufferedInputStream(new FileInputStream(fileName));
                    // large enough buffer to transfer file
                    int bufferSize = (int) fileName.length();                    
                    byte[] fileBuffer = new byte[bufferSize];
                    fileStream.read(fileBuffer);
                    // transfer bytes
                    out.writeInt(fileBuffer.length);
                    out.write(fileBuffer);
                    out.flush();
                    //}

                    fileStream.close();
                    System.out.println("web page sent!");
                }
            }
        }
        catch(Exception e) {
            System.out.println("There was a problem with running a thread: " + e);            
        }
        System.out.println("Ending thread number " + threadID);
	}
	
	// start thread
    public void start () {
        if (t == null) {
            t = new Thread (new PServerThread(in, out, threadID));
            t.start ();
        }
	}
}