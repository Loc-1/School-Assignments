
/**
 * WebServer Class
 * 
 * Implements a multi-threaded web server
 * supporting non-persistent connections.
 * 
 * CPSC 441, Assingment 3
 * 
 * @author 	Lachlan Moore
 * @version	2021
 *
 */


 /**
  * idk why it has some issues and I have tried to fix it had a pretty hard couple of weeks and I know its no excuse
  * gf broke up with me and have probably only slept like 30 hours total past two weeks so this assignment has been really rough
  */
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;

public class WebServer extends Thread {

    // global logger object, configures in the driver class
    private static final Logger logger = Logger.getLogger("WebServer");

    // global vars
    private int port;
    private ExecutorService pool;

    /**
     * Constructor to initialize the web server
     * 
     * @param port The server port at which the web server listens > 1024
     * 
     */
    public WebServer(int port) {
        this.port = port;
        // setting pool size to 20
        this.pool = Executors.newFixedThreadPool(20);
    }

    /**
     * Main web server method. The web server remains in listening mode and accepts
     * connection requests from clients until the shutdown method is called.
     *
     */
    public void run() {
        ServerSocket server = null;
        try {
            // listen on port number
            server = new ServerSocket(this.port);
            server.setReuseAddress(true);
            // server.setSoTimeout(1000);
            // handle client requests
            while (!Thread.interrupted()) {
                Socket client = server.accept();
                // client connected
                System.out.println("New client connected " + client.getInetAddress() + ":" + client.getPort());
                // create and start worker in pool
                this.pool.execute(new WorkerThread(client));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally { // clean up
            if (server != null) {
                try {
                    server.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Signals the web server to shutdown.
     *
     */
    public void shutdown() {
        this.interrupted();
        // shutdown accepting new threads
        this.pool.shutdown();
        try {
            // wait 5 seconds for active threads to finish
            this.pool.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // cancel all current tasks, technically redundant but just in case
        this.pool.shutdownNow();
    }

}

class WorkerThread extends Thread {
    // global socket for threads
    private Socket socket;

    /**
     * Constructor to initialize the worker threads
     * 
     * @param s The socket the worker will use
     * 
     */
    WorkerThread(Socket s) {
        this.socket = s;
    }

    /**
     * Main worker thread method
     *
     */
    public void run() {
        BufferedReader in = null;
        BufferedOutputStream out = null;
        try {
            // set up the streams
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedOutputStream(new DataOutputStream(socket.getOutputStream()));
            // get the header
            String header = "";
            while (in.ready()) {
                header += in.readLine() + "\n";
            }
            // format header
            System.out.println(header);
            String[] headerParts = new String[4];
            if (header.length() >= 0 && header.length() < 5) {
                headerParts[0] = "";
                headerParts[1] = "";
                headerParts[2] = "";
                headerParts[3] = "";
            } else {
                headerParts = header.split("[\\s+\n]", 4);
            }
            SimpleDateFormat f = new SimpleDateFormat("EE, dd MMM yyyy hh:mm:ss zzz");
            Date date = new Date();

            if (!(headerParts[0].equals("GET")
                    && (headerParts[2].equals("HTTP/1.1") || headerParts[2].equals("HTTP/1.0")))) {
                // incorrect formatted header
                String response = headerParts[2] + " 400 Bad Request\r\nDate: " + f.format(date)
                        + "\r\nServer: Lachlan's Server\r\nConnection: Close\r\n\r\n";
                // write and print
                out.write(response.getBytes("US-ASCII"));
                out.flush();
                System.out.print(response);
            } else {
                FileInputStream file = null;
                File fi = null;
                Path fileN = null;
                BasicFileAttributes attr = null;
                try {
                    // create filestream and other files for header
                    // in try catch to make sure to catch if file doesnt exist
                    file = new FileInputStream(headerParts[1]);
                    fi = new File(headerParts[1]);
                    fileN = Paths.get(headerParts[1]);
                    attr = Files.readAttributes(fileN, BasicFileAttributes.class);
                    // write the okay header
                    String response = headerParts[2] + " 200 OK\r\nDate: " + f.format(date)
                            + "\r\nServer: Lachlan's Server\r\nLast Modified: " + attr.lastModifiedTime()
                            + "\r\nContent Length: " + fi.length() + "\r\nContent Type: "
                            + Files.probeContentType(fileN) + "\r\nConnection: Close\r\n\r\n";
                    // write and print 
                    out.write(response.getBytes("US-ASCII"));
                    out.flush();
                    System.out.print(response);
                    // buffer vars
                    int count = 0;
                    byte[] buffer = new byte[1024];
                    // write file over buffer
                    while ((count = file.read(buffer)) != -1) {
                        out.write(buffer, 0, count);
                        out.flush();
                    }
                    file.close();
                } catch (Exception e) {
                    // Error not found header
                    String response = headerParts[2] + " 404 Not Found\r\nDate: " + f.format(date)
                            + "\r\nServer: Lachlan's Server\r\nConnection: Close\r\n\r\n";
                    // write and print 
                    out.write(response.getBytes("US-ASCII"));
                    out.flush();
                    System.out.print(response);
                    // after printing close up the streams
                    out.close();
                    in.close();
                    socket.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally { // clean up
            try {
                out.close();
                in.close();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
