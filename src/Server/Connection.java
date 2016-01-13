package Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import DataTypes.Player;
import DataTypes.Power;

public class Connection extends Player implements Runnable{
	BufferedReader reader;
	Socket sock;
	PrintWriter client;
	boolean DC = false;
	public Connection(Socket clientSocket, PrintWriter user) {
		// new inputStreamReader and then add it to a BufferedReader
		client = user;
		try {
			sock = clientSocket;
			InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
			reader = new BufferedReader(isReader);
		} // end try
		catch (Exception ex) {
			System.out.println("error beginning StreamReader");
		} // end catch

	} // end ClientHandler()
	public void run() {
		String message;
		String[] data;
		String connect = "Connect";
		String disconnect = "Disconnect";
		String cords= "C";
		String power= "P";
		String movement="M";
		try {
			while ((message = reader.readLine()) != null) {

				//System.out.println("Received: " + message);
				
				data = message.split("`");
				/*
				for (String token:data) {
					System.out.println(token);
				}*/
				if (data[0].equals(connect)) {
					//"Connect`"+name+"`"+power+"`"+ID
					Player(500,500,data[1],getPower(stn(data[2])),data[3]);
				}else if (data[0].equals(disconnect)) {
					DC = true;
				}else if(data[0].equals(movement)){
					
					//tellEveryone(cords+"`"+data[1]+"`"+data[2]+"`"+data[3]);
				}else if(data[0].equals(power)){
					//data 4 is name
					//check if can use power
					
					//data3 = powerValue data 1,2= x,y cords
				}else{
					System.out.println("No Conditions were met.");
				}
			} // end while
		}catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("lost a connection");
		} // end catch
	} // end run()
	public Power getPower(int x){
		switch(x){
		case 0:
			return Power.Wall;
		case 1:
			return Power.Invincibility;
		case 2:
			return Power.Wormhole;
		default:
			return null;
		}
	}
	public int stn(String input){//string to number
		return Integer.parseInt(input);
	}
}
