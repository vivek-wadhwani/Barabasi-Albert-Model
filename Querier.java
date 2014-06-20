package barabasimodel;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

class Querier {
	
	
	
	
	void query(String filePath) throws NumberFormatException, InterruptedException, IOException{
		
		System.out.println("Please select one of following queries ");
		
		System.out.println("To know routing table press 1");
		System.out.println("To get file containing node information press 2");
		System.out.println("To know farthest node in the network press 3");
		
		Scanner sc = new Scanner(System.in);
		
		switch(sc.nextInt()){
		
		case 1: System.out.println("sending routing info...");
		
			

				File file = new File(filePath);

				BufferedReader br = new BufferedReader(new FileReader(file));
				 String line = null;
				 while ((line = br.readLine()) != null) {
				   System.out.println(line);
				 }
				
	
			
				break;
		case 2:
			System.out.println("sending file containing node info...");
			 file = new File(filePath);

			 br = new BufferedReader(new FileReader(file));
			  line = null;
			 while ((line = br.readLine()) != null) {
			   System.out.println(line);
			 }
			
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