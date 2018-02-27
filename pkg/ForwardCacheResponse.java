/* 
Compilation: javac pkg/*.java;
Part of the import package.

Name       : Michael Bido-Chavez (euid: mb0501)
Due Date   : Oct. 21, 2017
Class      : CSCE 3530

Proxy Server Program - Forward Cache Response
-----------------------------
Description:
The proxy server will wait client connection, and then accept a URL 
string. The server will then forward that string to the host for a 
HTTP response code. If the returned code is 200, cache the web page
if it's not already in the cache, and then forward it to the client. 
Repeat until client types 'bye' to disconnect. Even if the client
disconnects, the server will still run.

This contains class object, with internal methods to forward
the response from client to the requested web host. Upon receiving 
the HTTP response code, if 200, internal methods request and cache 
web page before sending the web page to the client.
-----------------------------
*/

package pkg;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ForwardCacheResponse {

    // variables
    private static boolean webRequestValid;
    private static boolean webDownloadSuccessful;
    private static int httpResponse;
    private String myURL;
    private URL url;
    private HttpURLConnection conn;
    // getters/setters
    int getHttpResponse() {
        return httpResponse;
    }
    void setHttpResponse(int code) {
        httpResponse = code;
    }
    // constructor
    public ForwardCacheResponse(String u) {
        webRequestValid = false;
        webDownloadSuccessful = false;
        myURL = u;
        httpResponse = 0; // if DNS could not find domain
    }
    // gets the HTTP response code from URL
    public int getUrlResponseCode()  {
        try {
            url = new URL(myURL);
            conn = (HttpURLConnection)url.openConnection();
            
            conn.setRequestMethod("GET");
            conn.connect();
            
            setHttpResponse(conn.getResponseCode());
            if (getHttpResponse() == 200) {
                webRequestValid = true;
            }
        }
        catch (IOException e) {
            System.err.println("Invalid URL '" + myURL + "' from Client, DNS address could not be found.");
        }
        
        return getHttpResponse();
    }
    // retreives web page from host
    public void getWebData() {
        // htmlString contains the contents from the read buffer
        String htmlString;
        // newFileName is the url without the http:// + .html
        String newFileName; 
        // System.out.println("url split file name: " +  newFileName);
        // variable for input stream reader
        InputStream objStream = null;
        BufferedReader readData; 
        BufferedWriter writeData;

        try {
            newFileName = myURL.split("://",2)[1] + ".html";
            objStream = url.openStream();
            // reads bytes and converts to characters w/ InputStreamReader
            readData = new BufferedReader(new InputStreamReader(objStream));
            writeData = new BufferedWriter(new FileWriter(newFileName));
            // puts the data into a file
            while ((htmlString = readData.readLine()) != null) {
                // System.out.println(htmlString);
                writeData.write(htmlString);
            }
            writeData.close();
        }
        catch (IOException e) {
            System.err.println("There was a problem with getting/writing the web data");
        }
    }
    // checks list.txt if the webpage is cached
    public boolean checkIfCached() {
        String fileName = "list.txt";
        boolean doesFileExist = false;
        try {
            File textFile = new File(fileName);
            doesFileExist = textFile.exists();
            System.out.print("Does list.txt exist? " + doesFileExist + " -- ");

            // if the file does not exist, create it and insert URL.
            if (!doesFileExist) {
                // FileWriter listWriter = new FileWriter(fileName);
                PrintWriter printer = new PrintWriter(new FileWriter(fileName));
                printer.print(myURL.split("://",2)[1]);
                printer.close();
            }
            // else update the List file with method
            else {
                doesFileExist = updateListFile(fileName);
            }
            // print out status
            System.out.println("Is " + myURL + " in cache? " + doesFileExist);            
        }
        catch (Exception e) {
            System.err.println("There was a problem with checking the cache.");
        }

        return doesFileExist;
    }
    // update list.txt to contain, at most, the 5 most recent web requests
    // list.txt stores the oldest items in cache at the top of the file
    public boolean updateListFile(String fileName) {
        // arrayList to store the strings from the list.txt
        ArrayList<String> list = new ArrayList<String>();
        boolean doesFileExist = false;

        try {
            // format for most recent URL, appends .html for handling files
            String htmlFileName = myURL.split("://",2)[1] + ".html";            

            File textFile = new File(fileName);
            BufferedReader br = new BufferedReader(new FileReader(textFile));
            String nextLine = null;
            // from each URL in list.txt, append .html for handling files
            while ((nextLine = br.readLine()) != null) {
                list.add(nextLine + ".html");
                // System.out.println("line from listfile = " + nextLine);
            }
            // if the cache is full, check if old files need to be removed
            if (list.size() == 5) {
                // if the list file does not contain the new URL, replace oldest cache item
                if (!list.contains(htmlFileName)) {
                    File toBeDeleted = new File(list.get(0));
                    // deletes the oldest item
                    if (toBeDeleted.delete()) {
                        System.out.println(list.get(0) + " was deleted from the cache, only 5 most recent allowed.");
                    }
                    else {
                        System.out.println(htmlFileName + " could not be deleted.");
                    }
                    list.remove(0);
                }
                // if the list file does contain the new URL, just update list file
                else {
                    doesFileExist = true;
                    list.remove(list.indexOf(htmlFileName));
                }
            }
            // if the list file does contain the new URL, just update list file
            else {
                if (list.contains(htmlFileName)) {
                    doesFileExist = true;
                    list.remove(list.indexOf(htmlFileName));
                }
            }
            // update the the ArrayList with the new URL and close
            list.add(htmlFileName);
            br.close();

            // overwrite old list.txt
            // FileWriter updateFile = new FileWriter(textFile, false);
            PrintWriter printer = new PrintWriter(new FileWriter(textFile, false));
            for (int i = 0; i < list.size(); i++) {
                printer.println(list.get(i).split(".html",2)[0]);
            }
            printer.close();
        }
        catch (Exception e) {
            System.err.println("There was a problem with updating the cache.");
        }
        return doesFileExist;
    }
}