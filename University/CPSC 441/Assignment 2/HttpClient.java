
/**
 * HttpClient Class
 * 
 * 
 * CPSC 441
 * Assignment 2
 * Lachlan Moore, 30030228
 * 
 */

import java.net.Socket;
import java.util.logging.*;
import java.io.*;

public class HttpClient {

    private static final Logger logger = Logger.getLogger("HttpClient"); // global logger

    /**
     * Default no-arg constructor
     */
    public HttpClient() {
        // nothing to do!
    }

    /**
     * Downloads the object specified by the parameter url.
	 *
     * @param url	URL of the object to be downloaded. It is a fully qualified URL.
     */
	public void get(String url) {
        // vars to store info
        String hostName = "";
        String pathName = "";
        int socketNum = 80;
        String[] urlParts;
        // split input string
        if (url.contains(":")) {
            urlParts = url.split("[:/]", 3);
            hostName = urlParts[0];
            socketNum = Integer.parseInt(urlParts[1]);
            pathName = "/"+urlParts[2];
        } else {
            urlParts = url.split("/", 2);
            hostName = urlParts[0];
            pathName = "/"+urlParts[1];
        }
        // split pathName to get object name for file creation later
        String[] pathParts = pathName.split("/");
        String fileName = pathParts[pathParts.length-1];
        // create socket
        Socket socket = null;
        // try opening a connection to the socket
        try {
            // Open socket, output and input
            socket = new Socket(hostName, socketNum);
            PrintStream out = new PrintStream(socket.getOutputStream());
            BufferedInputStream in = new BufferedInputStream(new DataInputStream(socket.getInputStream()));
            // GET request header and print GET header
            out.print("GET " + pathName + " HTTP/1.1\r\n");
            System.out.print("GET " + pathName + " HTTP/1.1\r\n");
            out.print("Host: " + hostName + "\r\n");
            System.out.print("Host: " + hostName + "\r\n");
            out.print("Connection: close\r\n");
            System.out.print("Connection: close\r\n");
            out.print("\r\n");
            System.out.print("\r\n");
            out.flush(); // flush afterwards
            // print out POST header
            String header = "";
            String temp = "";
            while(true) {
                temp = getLine(in);
                if (temp.equals("\r\n")) {
                    break;
                }
                header = header + temp;
            }
            System.out.println(header);
            // if connection was OK
            if (header.contains("200 OK")) {
                // create file to save
                FileOutputStream file = new FileOutputStream(fileName);
                // grab body data
                byte buffer[] = new byte[1024];
                int count;
                while((count = in.read(buffer)) != -1){
                    file.write(buffer, 0, count);
                    file.flush();
                }
                file.close();
            }
            // clean up
            in.close();
            out.close();
            socket.close();
        } catch (Exception e) { // catch errors 
            e.printStackTrace();
        }

    }

    /**
     * turns stream of bytes into readable line of characters.
	 *
     * @param in	Input stream to grab lines.
     * @return      Line of converted data
     */
    private String getLine (BufferedInputStream in) throws IOException {
        String string = "";
        int i;
        while ((i = in.read()) != -1) {
            char c = (char) i;
            if (c == '\n') {
                string = string + c;
                return string;
            }
            string = string + c;
        }
        return string;
    }

}
