
/**
 * GzipClient Class
 * 
 * CPSC 441
 * Assignment 1
 * Lachlan Moore
 * 30030228
 */

import java.net.Socket;
import java.io.*;
import java.util.logging.*;

public class GzipClient {

	private static final Logger logger = Logger.getLogger("GzipClient"); // global logger
	// initilaize global vars
	private String serverName;
	private int serverPort;
	private int bufferSize;
	private static DataOutputStream dataOutputStream = null;
	private static DataInputStream dataInputStream = null;

	/**
	 * Constructor to initialize the class.
	 * 
	 * @param serverName remote server name
	 * @param serverPort remote server port number
	 * @param bufferSize buffer size used for read/write
	 */
	public GzipClient(String serverName, int serverPort, int bufferSize) {
		// set global vars based on object creation
		this.serverName = serverName;
		this.serverPort = serverPort;
		this.bufferSize = bufferSize;
	}

	/**
	 * Compress the specified file via the remote server.
	 * 
	 * @param inName  name of the input file to be compressed
	 * @param outName name of the output compressed file
	 * @throws IOException
	 */
	public void gzip(String inName, String outName) {
		Socket socket = null;
		SendThread sending = null;
		// try connecting to server (with set params), create input and output streams
		try {
			// open port
			socket = new Socket(this.serverName, this.serverPort);
			// create streams
			dataInputStream = new DataInputStream(socket.getInputStream());
			dataOutputStream = new DataOutputStream(socket.getOutputStream());
			// send file to server by creating a thread to send so program reads while
			// thread writes to server
			sending = new SendThread(inName, socket);
			sending.start();
			// receive file from server
			receiveFile(outName);
		} catch (Exception e) { // catch connteciton error and print to console
			e.printStackTrace();
			System.err.println("Connection error, please try again. Check port and server name"); 
		}
		try {
			// cleaning
			dataInputStream.close();
			dataOutputStream.close();
			socket.close();
			sending.join();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Thread did not join correctly"); 
		}
	}

	/**
	 * Send file to server
	 * 
	 * @param fileName name of file to send
	 */
	private void sendFile(String fileName) {
		try {
			// create file
			File file = null;
			file = new File(fileName);
			// create file stream
			FileInputStream fileInputStream = new FileInputStream(file);

			int count = 0;
			byte[] buffer = new byte[this.bufferSize];

			while ((count = fileInputStream.read(buffer)) != -1) {
				dataOutputStream.write(buffer, 0, count);
				dataOutputStream.flush();
				// print details for assignment
				System.out.println("W " + count);
			}
			// clean up
			fileInputStream.close();
		} catch (Exception e) { // handle errors 
			e.printStackTrace();
			System.err.println("File not found, try again."); 
			return;
		}
	}

	/**
	 * Receive file from server
	 * 
	 * @param fileName name of file to receive
	 */
	private void receiveFile(String fileName) {
		try {
			// create file stream
			FileOutputStream fileOutputStream = new FileOutputStream(fileName);

	 		int count = 0;
			byte[] buffer = new byte[this.bufferSize];

			while ((count = dataInputStream.read(buffer)) != -1) {
				fileOutputStream.write(buffer, 0, count);
				fileOutputStream.flush();
				// print details for the assingment
				System.out.println("R " + count);
			}
			// clean up
			fileOutputStream.close();
		} catch (Exception e) { // handle errors
			e.printStackTrace();
			System.err.println("File not created, try again."); 
			return;
		}
	}

	private class SendThread extends Thread {

		public Socket socket;
		public String fileName;

		/**
		 * Constructor to initialize the class.
		 * 
		 * @param fileName
		 * @param socket
		 */
		public SendThread(String fileName, Socket socket) {
			this.socket = socket;
			this.fileName = fileName;
		}

		/**
		 * Run function for the thread
		 */
		public void run() {
			try {
				// call the sendFile method to handle sending the file
				sendFile(fileName);
				// clean up when done to signal to server no more file to read
				socket.shutdownOutput();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}