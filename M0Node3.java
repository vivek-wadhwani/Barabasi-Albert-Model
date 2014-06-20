import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.cert.PKIXParameters;
import java.security.cert.PKIXReason;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;

// This is one of m0 nodes
public class M0Node3 {
	
	
	 
	Socket clientsckt = null;
	ServerSocket tcpSocket = null;
	DatagramSocket udpSocket;
	DatagramSocket udpSocketForQuery;
	Properties prop;
	static int nodenumber = 0;
	static int tcpport = 0;
	static int udpport = 0;
	File myFile;
	static int udpportForQuery = 0;
	static String host = null;
	static BufferedReader br = null;
	static ObjectOutputStream oos = null;
	static ObjectInputStream oi = null;
	static PrintWriter pw = null;
	String receiveMessage = "", sendMessage = "";
	static int numberOfConnections;
	static int noOfConnections = 0;
	// ArrayList<Routinginfo> routeinfo = new ArrayList<Routinginfo>(2);
	static String ipaddress;
	String oldContentOfFile = "null";
	ArrayList<String> al;
	String newContentOfFile = null;
	ArrayList arrayList = new ArrayList();
	BufferedInputStream bis;
	BufferedOutputStream bos;
	String connectedNodes;
	String pkgPath = System.getProperty("user.dir") + "\\src\\barabasimodel";

public	static void main(String[] args) throws NumberFormatException, IOException {

		try {
			new M0Node3();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	M0Node3() throws InterruptedException, NumberFormatException, IOException {

		prop = new Properties();
		try {
			// load a properties file
			prop.load(getClass().getResourceAsStream("config3.properties"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		ipaddress = prop.getProperty("ipaddress");
		nodenumber = Integer.parseInt(prop.getProperty("nodenumber"));
		tcpport = Integer.parseInt(prop.getProperty("tcpport"));
		udpport = Integer.parseInt(prop.getProperty("udpport"));
		udpportForQuery = Integer.parseInt(prop.getProperty("udpportforquery"));

		new Thread(new Runnable() {
			public void run() {
				createTCPServer(tcpport);

			}
		}).start();

		new Thread(new Runnable() {
			public void run() {
				try {
					createUDPServer(udpSocket, udpport);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}).start();

		new Thread(new Runnable() {
			public void run() {
				try {
					createUDPServer(udpSocketForQuery, udpportForQuery);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}).start();

		new Thread(new Runnable() {
			public void run() {
				try {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					createClient();
				} catch (NumberFormatException e) {

					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void createTCPServer(int port) {

		try {
			tcpSocket = new ServerSocket(port);
			System.out.println("M0Node Server" + " "
					+ " is listening on tcp port " + tcpSocket.getLocalPort());

			while (true) {
				// Listen for a TCP connection request.
				final Socket connection = tcpSocket.accept();
				arrayList.add(connection);
				br = null;
				receiveMessage = null;
				sendMessage = null;
				pw = null;

				// get i/o streams
				br = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
				pw = new PrintWriter(connection.getOutputStream(), true);

				// receive
				if ((receiveMessage = br.readLine()) != null) {
					System.out.println(receiveMessage);
				}

				// send

				String ipAddress = connection.getInetAddress().toString();

				sendMessage = "[" + ipAddress.substring(1, ipAddress.length())
						+ ":" + port + "]" + "--" + receiveMessage;
				String firstNode = "["
						+ ipAddress.substring(1, ipAddress.length()) + ":"
						+ port + "]";
				String secondNode = "--" + receiveMessage;

				firstNode = firstNode + "\r\n";

				secondNode = secondNode + "\r\n";

				connectedNodes = firstNode + secondNode;
				
				byte[] writeBytes = new byte[1024];

				myFile = new File(pkgPath + "\\RoutingInformation"+prop.getProperty("nodenumber")+".txt");

				bos = new BufferedOutputStream(new FileOutputStream(myFile,
						true));

				writeBytes = connectedNodes.getBytes();

				bos.write(writeBytes);

				bos.flush();

				//myFile = new File(pkgPath + "\\RoutingInformation"+prop.getProperty("nodenumber")+".txt");
				bis = new BufferedInputStream(new FileInputStream(myFile));

				byte[] readBytes=new byte[1024];

				// readBytes="".getBytes();

				for(int i=0;i<readBytes.length;i++){
					
					readBytes[i]=0;
				}
				
				bis.read(readBytes, 0, readBytes.length);

				

				findNodes(readBytes);

			}

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public void createUDPServer(DatagramSocket udpsckt, int port)
			throws IOException {

		try {
			udpsckt = new DatagramSocket(port);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		System.out.println("NodeServer" + " " + " is listening on udp port "
				+ udpsckt.getLocalPort());
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];
		InetAddress IPAddress = InetAddress.getByName("localhost");

		while (true) {

			try {
				DatagramPacket receivePacket = new DatagramPacket(receiveData,
						receiveData.length);
				udpsckt.receive(receivePacket);
				String getRequest = new String(receivePacket.getData(), 0,
						receivePacket.getLength());

				System.out.println("@UDPSERVER: received request : "
						+ getRequest);

				if (port == Integer.parseInt(prop.getProperty("udpport"))) {
					int maxSize = Integer.parseInt(prop
							.getProperty("networkmaxsize"));
					int currSize = Integer.parseInt(prop
							.getProperty("networkcurrsize"));

					if (maxSize - currSize > 0) {

						System.out.println((maxSize - currSize)
								+ " more node can connect");

						BufferedReader buff = new BufferedReader(
								new FileReader(pkgPath+"\\RoutingInformation"+prop.getProperty("nodenumber")+".txt"));

						StringBuilder sb = new StringBuilder();
						String line = buff.readLine();
						while (line != null) {
							sb.append(line);
							sb.append("\r\n");
							line = buff.readLine();
						}
						String send = sb.toString();

						sendData = send.getBytes();

						DatagramPacket sendPacket = new DatagramPacket(
								sendData, sendData.length,
								receivePacket.getAddress(),
								receivePacket.getPort());

						udpsckt.send(sendPacket);

					} else {

						System.out.println((maxSize - currSize)
								+ " node can connect");
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void createClient() throws NumberFormatException, IOException,
			InterruptedException {

		System.out
				.println("NodeClient is ready to connect...Enter server node  port : ");

		// nullify
		br = null;
		receiveMessage = null;
		sendMessage = null;
		pw = null;

		// get i/o streams and socket
		br = new BufferedReader(new InputStreamReader(System.in));
		tcpport = Integer.parseInt(br.readLine());

		clientsckt = new Socket(host, tcpport);

		pw = new PrintWriter(clientsckt.getOutputStream(), true);

		// send
		sendMessage = "[" + prop.getProperty("ipaddress") + ":"
				+ String.valueOf(tcpSocket.getLocalPort()) + "]";
		pw.println(sendMessage);
		// System.out.println(sendMessage);

		// receive

		File myFile = new File(pkgPath + "\\RoutingInformation"
				+ prop.getProperty("nodenumber") + ".txt");

		byte[] receiveBytes = new byte[1024];

		FileOutputStream fos = new FileOutputStream(myFile);

		BufferedOutputStream bos = new BufferedOutputStream(fos);
		
		ObjectInputStream ois=new ObjectInputStream(clientsckt.getInputStream());



			System.out.println("receiving started.....");
			int bytesRead = ois.read(receiveBytes, 0, receiveBytes.length);
			bos.write(receiveBytes, 0, bytesRead);
			bos.flush();
			System.out.println("receiving complete.....");

		

	}

	public HashMap<String, Integer> findProbabability() {

		double degree[] = new double[2];
		double probability[] = new double[2];

		double totaldegree = 0;

		for (int i = 0; i < degree.length; i++) {
			probability[i] = degree[i] / totaldegree;
		}

		Random rand = new Random();
		HashMap ip_port = new HashMap<String, Integer>();
		double p = rand.nextInt(10) / 10;
		double cumulativeProbability = 0.0;

		for (int i = 0; i < probability.length; i++) {

			cumulativeProbability += probability[i];

			if (p <= cumulativeProbability && probability[i] != 0) {

				System.out.println(p);

				/*
				 * ip_port.put(routeinfo.get(i).getIPAddress(), routeinfo.get(i)
				 * .getTcpport());
				 */

				break;
			}

		}

		return ip_port;

	}

	public void findNodes(byte[] writeBytes) throws IOException {

		
		
		
		al = new ArrayList<String>();
		BufferedReader buffer;
		try {
			buffer = new BufferedReader(new FileReader(pkgPath + "\\RoutingInformation"+prop.getProperty("nodenumber")+".txt"));
			StringBuilder sb1 = new StringBuilder();
			String lines = buffer.readLine();
			while (lines != null) {
				if (lines.contains("[")) {
					String ipAdd = lines.substring(lines.indexOf("[") + 1,
							lines.indexOf(":"));
					al.add(ipAdd);
				}
				if (lines.contains("]")) {
					String port = lines.substring(lines.indexOf(":") + 1,
							lines.indexOf("]"));
					al.add(port);
				}
				// sb1.append(lines);
				// sb1.append("\r\n");
				lines = buffer.readLine();
			}

			// read.reset();

			buffer.close();

			
			
			System.out.println(al);
			
			
			// TO BROADCAST THE DEGREE TO ALL THE NODES
			broadcast(writeBytes);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void broadcast(byte[] writeBytes) {


		for (int i = 0; i < arrayList.size(); i++) {
			Socket connections = (Socket) arrayList.get(i);
			
			try {

				
				oos = new ObjectOutputStream(connections.getOutputStream());
				
				System.out.println("sending started.....");

				oos.write(writeBytes, 0, writeBytes.length);

				oos.flush();

				System.out.println("sending complete.....");

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

}
