
/**
 * GoBackFtp Class
 * 
 * GoBackFtp implements a basic FTP application based on UDP data transmission.
 * It implements a Go-Back-N protocol. The window size is an input parameter.
 * 
 * Assignment 5
 * 
 * @author 	Lachlan Moore
 * @version	2021
 *
 */

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.*;

public class GoBackFtp {
	// global logger
	private static final Logger logger = Logger.getLogger("GoBackFtp");

	public static int windowSize, rtotimer;
	public static Timer timer;

	/**
	 * Constructor to initialize the program
	 * 
	 * @param windowSize Size of the window for Go-Back_N in units of segments
	 * @param rtoTimer   The time-out interval for the retransmission timer
	 */
	public GoBackFtp(int windowSize, int rtoTimer) {
		this.rtotimer = rtoTimer;
		this.windowSize = windowSize;
	}

	/**
	 * Send the specified file to the specified remote server
	 * 
	 * @param serverName Name of the remote server
	 * @param serverPort Port number of the remote server
	 * @param fileName   Name of the file to be trasferred to the rmeote server
	 * @throws FtpException If unrecoverable errors happen during file transfer
	 */
	public void send(String serverName, int serverPort, String fileName) throws FtpException {
		int port = 0;
		int seqnum = 0;
		Socket socket = null;
		DataInputStream in = null;
		DataOutputStream out = null;
		File file = null;

		// Complete TCP handshake (copy from assignment 4)
		try {
			// open TCP sockets and streams
			socket = new Socket(serverName, serverPort);
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
			// open file
			file = new File(fileName);
			// send info
			out.writeUTF(fileName);
			out.writeLong(file.length());
			out.writeInt(45555);
			out.flush();
			// get info
			port = in.readInt();
			seqnum = in.readInt();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				// clean up
				socket.close();
				out.close();
				in.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		// Go back N
		DatagramSocket uSock = null;
		try {
			// create timer
			this.timer = new Timer(true);
			// create port
			uSock = new DatagramSocket(45555);
			// create threads
			Send send = new Send(uSock, file, seqnum, serverName, port);
			Receiver receiver = new Receiver(uSock, seqnum, send);
			// start threads
			send.start();
			receiver.start();
			// join threads
			send.join();
			receiver.join();
			// close port
			timer.cancel();
			uSock.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	// Helper method to start the timer task
	public synchronized void startTimer () {
		// TODO make these work
		// No idea how to make new tasks each time between threads without making it global :(
	} 
	// Help method to stop the timer task
	public synchronized void stopTimer () {
		// TODO make these work
	} 
} // end of class

/**
 * Sender thread
 */
class Send extends Thread {

	public DatagramSocket socket;
	public File file;
	public int seqnum, port;
	public String serverName;
	public ConcurrentLinkedQueue<DatagramPacket> queList = new ConcurrentLinkedQueue<DatagramPacket>();
	public MyTimerTask task;

	/**
	 * Constructor to initialize the class
	 * 
	 * @param socket pass in socket
	 * @param file pass in file for file stream
	 * @param seqnum pass in seqnum to know first num to start at
	 * @param serverName pass in servername to know what to send to
	 * @param port pass in port
	 */
	public Send(DatagramSocket socket, File file, int seqnum, String serverName, int port) {
		this.socket = socket;
		this.file = file;
		this.port = port;
		this.seqnum = seqnum;
		this.serverName = serverName;
	}

	/**
	 * Run function for the thread
	 */
	public void run() {
		try {
			// open file stream
			FileInputStream fStream = new FileInputStream(this.file);
			int count = 0;
			byte[] buffer = new byte[FtpSegment.MAX_PAYLOAD_SIZE];

			while ((count = fStream.read(buffer)) != -1) {
				// build segment
				FtpSegment seg = new FtpSegment(this.seqnum, buffer);
				DatagramPacket pck = FtpSegment.makePacket(seg, InetAddress.getByName(this.serverName), this.port);
				// busy loop while que is full
				while (queList.size() >= GoBackFtp.windowSize) {
					// do nothing
				}
				// send and add to que
				socket.send(pck);
				queList.add(pck);
				System.out.println("send " + this.seqnum);
				this.seqnum++;
				if (pck == queList.peek()) {
					// start timer TODO fix this as proper timer
					task = new MyTimerTask(this.queList);
					task.uSock = this.socket;
					GoBackFtp.timer.schedule(task, GoBackFtp.rtotimer);
				}
			}
			// close stream when done
			fStream.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}
}

/**
 * Receiver thread
 */
class Receiver extends Thread {

	public DatagramSocket socket;
	public int seqnum;
	public Send send;
	public MyTimerTask task;

	/**
	 * Constructor to initialize the class
	 */
	public Receiver(DatagramSocket socket, int seqnum, Send send) {
		this.socket = socket;
		this.seqnum = seqnum;
		this.send = send;
	}

	/**
	 * Run function for the thread
	 */
	public void run() {
		try {
			int nextExpected = seqnum + 1;
			while (send.isAlive() || !this.send.queList.isEmpty()) {
				byte[] buffer = new byte[FtpSegment.MAX_PAYLOAD_SIZE];
				// recieve ack
				DatagramPacket ack = new DatagramPacket(buffer, buffer.length);
				socket.receive(ack);
				FtpSegment rec = new FtpSegment(ack);
				// if correct seq num
				if (rec.getSeqNum() == nextExpected) {
					// stop timer
					// this.send.task.cancel();
					// canceling timer doesnt send stuff
					// need to fix with sycn blocks but no idea how to
					System.out.println("ack " + nextExpected);
					nextExpected++;
					// remove item at the front of the list as it is ACK'd
					this.send.queList.remove();
					// start timer if trans que not empty
					if (!this.send.queList.isEmpty()) {
						// start timer TODO fix this as proper timer
						task = new MyTimerTask(this.send.queList);
						task.uSock = this.socket;
						GoBackFtp.timer.schedule(task, GoBackFtp.rtotimer);
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}
}

class MyTimerTask extends TimerTask {
	// global var to store packet to send
	ConcurrentLinkedQueue<DatagramPacket> queListCopy;
	DatagramSocket uSock;

	public MyTimerTask (ConcurrentLinkedQueue<DatagramPacket> queListCopy) {
		this.queListCopy = queListCopy;
	}

	@Override
	public void run() {
		try {
			// signal a timeout has happened
			System.out.println("Timeout");
			ArrayList<DatagramPacket> list = new ArrayList<>(queListCopy);
			for (int i = 0; i < list.size(); i++) {
				FtpSegment sent = new FtpSegment(list.get(i));
				// re send packets
				uSock.send(list.get(i));
				System.out.println("retx " + sent.getSeqNum());
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}