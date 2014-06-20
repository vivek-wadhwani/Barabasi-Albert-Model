/*
NewNode.java
*
*
*
*
*/

package barabasimodel;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;


/* This is one of new nodes. The purpose of NewNode class is to create a new node in the network and connect it to one of the M0Node. 
It starts multiple threads for TCP, UDP, UDP QUery Connection. It creates a new client UDP thread and send a connection requestion to m0 nodes.
If in the reply of it recieves can connect, it creates a TCP thread and establishes a TCP Connection to the m0 node. It does that in findprobability() method.
It also broadcasts a file over the network which contains information i.e. IP address and TCP Port no. of all the node connected in the network.

*/

public class NewNode {

	static int port = 0;
	static String joinRequest;				//Variable for JoinRequest
	static String ipaddress;
	static String joinReply;
	static String pkgPath = System.getProperty("user.dir")			
			+ "\\src\\barabasimodel";					//Directory path where node info file is stored
	static Socket clientsckt = null;
	static ServerSocket tcpSocket = null;
	DatagramSocket udpSocket;
	DatagramSocket udpSocketForQuery;
	static Properties prop;
	static int nodenumber = 0;					
	static int tcpport = 0;						//variable to store TCP Port No.
	static int udpport = 0;						//variable to store UDP Port No.
	static File myFile;
	static int udpportForQuery = 0;				//variable to store UDPQuery Port No.
	static String host = null;
	BufferedReader br = null;
	ObjectOutputStream oos = null;
	ObjectInputStream oi = null;
	PrintWriter pw = null;
	String receiveMessage = "";
	String sendMessage = "";
	BufferedInputStream bis;
	BufferedOutputStream bos;
	String connectedNodes;
	FileOutputStream fos;
	ObjectInputStream ois;
	ArrayList al;
	static DatagramSocket clientSocket;
	Set<String> set;
	ArrayList arrayListforSet;
	String[][] degreeInformation;
	ArrayList goingToConnectNodesAddress;
	String[] goingToConnecthost = new String[2];
	String[] goingToConnecttcpport= new String [2];
	String connectHost1="";
	String connectHost2="";
	int tcpPort1=0;
	int tcpPort2=0;
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {


//Reading file from disk, if file doen't exists creating that file and storing it.

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter ip  for join request : ");
		ipaddress = br.readLine();
		InetAddress ip = InetAddress.getByName(ipaddress);
		byte[] bytes = ip.getAddress();
		for (byte b : bytes) {
			System.out.println(b & 0xFF);
		}
		InetAddress IPAddress = InetAddress.getByAddress(bytes);
		System.out.println("Enter port for join request : ");
		udpport = Integer.parseInt(br.readLine());
		 clientSocket = new DatagramSocket(0);

		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];

		System.out.println("Enter join request command : ");

		joinRequest = br.readLine();
		
		//Send UDP message
		sendData = joinRequest.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData,
				sendData.length, IPAddress, udpport);

		clientSocket.send(sendPacket);
		
		
		//Receive UDP REPLY
		DatagramPacket receivePacket = new DatagramPacket(receiveData,
				receiveData.length);
		clientSocket.receive(receivePacket);
		joinReply = new String(receivePacket.getData());
		joinReply.substring(joinReply.indexOf("["),
				joinReply.lastIndexOf("]") - 1);
		System.out.println(joinReply);
		
		File file = new File(pkgPath+"\\UpdatedRoutingInformation.txt");
		// if file doesnt exists, then create it
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		FileWriter fw;
		try {
			fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(joinReply);
			bw.close();
			fw.close();
			
			NewNode node1 = new NewNode();
			node1.findNodes();
			
		}
		
		
		
		catch(Exception e){
			e.printStackTrace();
		
		}
		
		clientSocket.close();
		
	}
		
		
		public void findNodes() throws IOException {

			
			
			
			al = new ArrayList<String>();
			BufferedReader buffer;
			try {
				buffer = new BufferedReader(new FileReader(pkgPath + "\\UpdatedRoutingInformation.txt"));
				StringBuilder sb1 = new StringBuilder();
				String lines = buffer.readLine();
				String nodeAddress="";
				String ipAddress="";
				String portNumber ="";
				while (lines != null ) {
					
					if(lines.contains("["))
					{
					
					if (lines.contains("[")) {
						 ipAddress = lines.substring(lines.indexOf("[") + 1,
								lines.indexOf(":"));
						//al.add(ipAdd);
					}
					if (lines.contains("]")) {
						 portNumber = lines.substring(lines.indexOf(":") + 1,
								lines.indexOf("]"));
						//al.add(port);
					}
					nodeAddress=ipAddress+":"+portNumber;
					String nodes= String.valueOf(al.add(nodeAddress));
					System.out.println(nodes);
					// sb1.append(lines);
					// sb1.append("\r\n");
					lines = buffer.readLine();
				}
					else {
						
						break;
					}
				
			}

				// read.reset();

				buffer.close();
				
				
				System.out.println(al);

				/*for (int i = 0; i < al.size(); i++) {
					
				}*/
				
				
				int end = al.size();
				 set = new HashSet<String>();

				for(int i = 0; i < end; i++){
				set.add(String.valueOf(al.get(i).toString()));
				}
				arrayListforSet = new ArrayList();

				Iterator it = set.iterator();
				while(it.hasNext()) {
					arrayListforSet.add(it.next());
				 // System.out.println(it.next());
				
				
				}
				
				findDegree();
				
				// TO BROADCAST THE DEGREE TO ALL THE NODES
				

			}
			catch(Exception e1){
				e1.printStackTrace();
				
				
			}
		/*if (joinReply != "REJECT") {

			NewNode newnode = new NewNode();
			newnode.createTCPConnection(ipaddress,tcpport);
	
			newnode.createTCPConnection(ipaddress, tcpport);
		}else{
			
			System.out.println("m0 nodes have been made.you can not connect");
		}*/

		

	}
		
		
		/**
		 * Calculates the degree of each node and stores it in Arraylist
		 * 
		 */
		public void findDegree(){
			int degree=0;
		 degreeInformation= new String[arrayListforSet.size()][2];
			for (int i = 0; i < arrayListforSet.size(); i++) {
				
				degree=0;
				for (int j = 0; j < al.size(); j++) {
					if(arrayListforSet.get(i).toString().equals(al.get(j).toString()))
					{
						degree++;
						
					}
					
				}
				
				String nodeWithPort = arrayListforSet.get(i).toString();
				degreeInformation[i][0]= nodeWithPort;
				degreeInformation[i][1]=String.valueOf(degree);
				
				
				
			}
			
			try {
				findProbability();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		
			
		}
		
		
		/**
		 * Finds probability of each nodes and receives the node information to which the node has to connect
		 */
		public void findProbability(){
			
			//double degree[] = new double[2];
			double probability[] = new double[(degreeInformation.length)];

			double totaldegree = 0;

			for (int i = 0; i < (degreeInformation.length); i++) {
				totaldegree=totaldegree+Integer.parseInt(degreeInformation[i][1]);
			}
			for (int i = 0; i < degreeInformation.length; i++) {
				probability[i] = Integer.parseInt(degreeInformation[i][1]) / totaldegree;
			}
			
			
 
			Random rand = new Random();
			//HashMap ip_port = new HashMap<String, Integer>();
			double p = rand.nextInt(10) / 10;
			int countNoOfNodes =0;
			System.out.println(p);
			double cumulativeProbability = 0.0;
			goingToConnectNodesAddress = new ArrayList();

			for (int i = 0; i < probability.length; i++) {

				cumulativeProbability += probability[i];

				if (p <= cumulativeProbability && probability[i] != 0) {

					System.out.println("probabalitly: "+p);
					
					goingToConnectNodesAddress.add(degreeInformation[i][0]);
					System.out.println("Going to connect nodes: "+goingToConnectNodesAddress.get(i));
					countNoOfNodes++;
					
					if(countNoOfNodes ==2)
					{
					break;
					}
				}

			}

			//Separating port number and ipaddress
			
			for (int i = 0; i < goingToConnectNodesAddress.size(); i++) {
				String separateIpAndPort = goingToConnectNodesAddress.get(i).toString();
				 goingToConnecthost[i] = separateIpAndPort.substring(0,
						 separateIpAndPort.indexOf(":"));
				 goingToConnecttcpport[i] = separateIpAndPort.substring(separateIpAndPort.indexOf(":")+1,separateIpAndPort.length());
				
			}
			
			
			
				 connectHost1 = goingToConnecthost[0];
				 connectHost2= goingToConnecthost[1];
				 tcpPort1 = Integer.parseInt(goingToConnecttcpport[0]);
				 tcpPort2 = Integer.parseInt(goingToConnecttcpport[1]);
				 
				 
				 
				 try {
					createTCPConnection(connectHost1, tcpPort1);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 try {
					createTCPConnection(connectHost2, tcpPort2);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
		}
		
		
		
		

//method for TCP connection

	/**
	 * @param host
	 * @param tcpport
	 * @throws IOException
	 */
		
		
	
	void createTCPConnection(String host,int tcpport) throws IOException {
		// TODO Auto-generated method stub

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
		/*sendMessage = "[" + prop.getProperty("ipaddress") + ":"
				+ String.valueOf(tcpSocket.getLocalPort()) + "]";*/
		sendMessage = "CONNECTED!!!";
		pw.println(sendMessage);
		System.out.println("TCP connection esatblished ");	// System.out.println(sendMessage);

		// receive

		myFile = new File(pkgPath + "\\RoutingInformation"
				+ prop.getProperty("nodenumber") + ".txt");

		byte[] receiveBytes = new byte[1024];

		fos = new FileOutputStream(myFile);

		bos = new BufferedOutputStream(fos);

		ois = new ObjectInputStream(clientsckt.getInputStream());

		System.out.println("receiving  started.....");
		int bytesRead = ois.read(receiveBytes, 0, receiveBytes.length);
		bos.write(receiveBytes, 0, bytesRead);
		bos.flush();
		System.out.println("receiving complete.....");
		
		
		/*if ((receiveMessage = br.readLine()) != null) {
			System.out
			.println("@NodeServer -> data received from client :  "
					+ receiveMessage);
}*/

	}
	
	//Class for Queries performed to the node
	/**
	 * @author Vivek
	 *
	 */
	class Querier {
					
		
		
		
			void query(){
				
				System.out.println("Please select one of following queries ");
				
				System.out.println("To know routing table press 1");
				System.out.println("To get file containing node information press 2");
				System.out.println("To know farthest node in the network press 3");
				
				Scanner sc = new Scanner(System.in);
				
				switch(sc.nextInt()){
				
				case 1: System.out.println("sending routing info...");
					
						break;
				case 2:
					System.out.println("sending file containing node info...");
					
					
						break;
				case 3:
					
					
					System.out.println("sending farthest node...");
						break;
						
				default:
					System.out.println("Incorrect choice...exiting");
					
							System.exit(1);
				}
				
				
				
				
				
			}
		
	}

}
