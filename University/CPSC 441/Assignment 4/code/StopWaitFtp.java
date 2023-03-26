/**
 * Assignment 4
 * Lachlan Moore
 * CPSC 441
 */

import java.io.*;
import java.net.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.*;

public class StopWaitFtp {
	
	private static final Logger logger = Logger.getLogger("StopWaitFtp"); // global logger	
	private int timeout;

	/**
	 * Constructor to initialize the program 
	 * 
	 * @param timeout		The time-out interval for the retransmission timer
	 */
	public StopWaitFtp(int timeout) {
		this.timeout = timeout;
	}


	/**
	 * Send the specified file to the specified remote server
	 * 
	 * @param serverName	Name of the remote server
	 * @param serverPort	Port number of the remote server
	 * @param fileName		Name of the file to be trasferred to the rmeote server
	 * @throws FtpException If anything goes wrong while sending the file
	 */
	public void send(String serverName, int serverPort, String fileName) throws FtpException {
		int port = 0;
		int seqnum = 0;
		Socket socket = null;
		DataInputStream in = null;
		DataOutputStream out = null;
		File file = null;

		// Complete TCP handshake
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
			//TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				// clean up
				socket.close();
				out.close();
				in.close();
			} catch (Exception e) {
				//TODO: handle exception
				e.printStackTrace();
			}
		}

		// Stop and Wait function
		DatagramSocket uSock = null;
		try {
			uSock = new DatagramSocket(45555);
			// open file stream
			FileInputStream fileStream = new FileInputStream(file);
			int count = 0;
			byte[] buffer = new byte[FtpSegment.MAX_PAYLOAD_SIZE]; // set to max size of FtpSegment
			byte[] receive = new byte[FtpSegment.MAX_PAYLOAD_SIZE];
			// create timer
			Timer timer = new Timer(true);
			// while there is still file to read
			while((count = fileStream.read(buffer)) != -1) {
				// build segment
				FtpSegment seg = new FtpSegment(seqnum, buffer);
				DatagramPacket pck = FtpSegment.makePacket(seg, InetAddress.getByName(serverName), port);
				// pass in vars to store in timer
				MyTimerTask timerTask = new MyTimerTask();
				timerTask.reSend = pck;
				timerTask.seqnum = seqnum;
				timerTask.uSock = uSock;
				// send segment
				uSock.send(pck);
				System.out.println("send " + seqnum);
				// start timer
				timer.scheduleAtFixedRate(timerTask, this.timeout, this.timeout);
				// receive ack
				DatagramPacket ack = new DatagramPacket(receive, receive.length);
				uSock.receive(ack);
				// end timer
				timerTask.cancel();
				seqnum++;
				System.out.println("ack " + (seqnum));
			}
			// close after finishing
			fileStream.close();
			timer.cancel();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			uSock.close();
		}
	}

} // end of class

class MyTimerTask extends TimerTask {
	// global var to store packet to send
	DatagramPacket reSend;
	int seqnum;
	DatagramSocket uSock;

	@Override
	public void run() {
		System.out.println("Timeout");
		try {
			uSock.send(reSend);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("retx " + this.seqnum);
	}

}