package Server;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import javax.swing.Timer;


import Chat.ArrayChat;
import Chat.Message;
import DataTypes.Player;
import DataTypes.Power;
public class Server extends Applet implements MouseListener,ActionListener,MouseMotionListener{
	ArrayChat serverLog;
	ArrayChat userList;
	ArrayList<Player> players = new ArrayList<Player>();
	ServerConnection interwebs;
	public Server(){
		setFocusable(true);
		addMouseListener(this);
		addMouseMotionListener(this);
		setSize(1000,500);
		userList = new ArrayChat(600,0, 700,450,32,false);//used to display stuff about users
		serverLog = new ArrayChat(200,0,600,450,32,true);// magic number 32 for max chat entries displayed
		serverLog.addMessage(new Message("System", "Start Up"));
		Timer myTimer;
		myTimer=new Timer(50, this);
		myTimer.start();
	}
	public void startServer(){
		Thread servertest=new Thread(new ServerConnection(9999));
		servertest.start();
	}
	public class ServerConnection implements Runnable{
		boolean run;
		DatagramSocket serverSock;
		public ServerConnection(int port){
			serverLog.addMessage(new Message("System", "Attempting to start socket at port "+port));
			run = true;
			 try {
				serverSock = new DatagramSocket(port);
				serverLog.addMessage(new Message("System", "Socket Established"));
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				serverLog.addMessage(new Message("ERROR", e.getMessage(),Color.RED));
				run= false;
			}
		}
		@Override
		public void run() {
			if(run)
				serverLog.addMessage(new Message("Server","Starting to accept packets."));
			else
				return;
			try {
				byte[] receiveData = new byte[1024];
				while (run) {
					DatagramPacket receivePacket = new DatagramPacket(receiveData,receiveData.length);
					serverSock.receive(receivePacket);
					String data = new String(receivePacket.getData());
					//do something with ddata
					process(data.split(" "),receivePacket.getAddress(),receivePacket.getPort());
				} 
			}catch (Exception ex){
				serverLog.addMessage(new Message("ERROR", ex.getMessage(),Color.RED));
			}
		}
		final String Connect = "Connect";
		final String Disconnect = "Disconnect";
		final String Cords = "C";
		final String Chat = "CH";
		final String Move = "M";
		final String Power = "P";
		public void process(String data[], InetAddress inetAddress,int port){
			//slot 0 is time
			int CP=1;
			while(CP<data.length-1){
				if(data[CP].equals(Cords)){
					//name x y
					for(int x=0;x<players.size();x++){
						if(players.get(x).getName().equals(data[CP+1]) && Long.parseLong(data[0])-players.get(x).lastTimeSeen>0){
							int[] test = {Integer.parseInt(data[CP+1]),Integer.parseInt(data[CP+2])};
							players.get(x).setCords(test);
							userList.chat.get(x).setMessage(test[0]+", "+test[1]);
						}
					}
					send(data[0] + data[CP]+ data[CP+1]+ data[CP+2]+ data[CP+3]);
					//SEND DATA TO ALL OTHER PLAYERS
					CP = CP+4;
				}else if(data[CP].equals(Power)){
					//name x y power
					send(data[0] + data[CP]+ data[CP+1]+ data[CP+2]+ data[CP+3]+data[CP+4]);
					CP = CP+5;
				}else if(data[CP].equals(Connect)){
					//name x y R G B power
					serverLog.addMessage(new Message("System", data[CP+1] + " has connected to server."));
					players.add(new Player(Integer.parseInt(data[CP+2]), Integer.parseInt(data[CP+3]), data[CP+1],getPower(Integer.parseInt(data[CP+7])),inetAddress,port));
					userList.addMessage(new Message(data[CP+1], data[CP+2] +", "+ data[CP+3],new Color(Integer.parseInt(data[CP+4]),Integer.parseInt(data[CP+5]),Integer.parseInt(data[CP+6])) ));
					//send data to all players
					send(data[0]+data[CP] + data[CP+1]+ data[CP+2]+ data[CP+3]+ data[CP+4]+data[CP+5]+data[CP+6]+data[CP+7]);
					CP = CP+8;
				}else if(data[CP].equals(Disconnect)){
					//name
					for(int x=0;x<players.size();x++){
						if(players.get(x).getName().equals(data[CP+1])){
							players.remove(x);
							userList.chat.remove(x);
						}
					}
					send(data[0] + data[CP]+ data[CP+1]);
					//send data to all players
					serverLog.addMessage(new Message("System", data[CP+1] + " has Disconnected from the server."));
					CP = CP+2;
				}else if(data[CP].equals(Chat)){
					//name message R G B
					serverLog.addMessage(new Message(data[CP+1],data[CP+2],new Color(Integer.parseInt(data[CP+3]),Integer.parseInt(data[CP+4]),Integer.parseInt(data[CP+5]))));
					//send data to all other players
					send(data[0] + data[CP]+ data[CP+1]+ data[CP+2]+ data[CP+3]+data[CP+4] + data[CP+5]);
					CP = CP+6;
				}
			}
		}
		
		public void send(String data){
			byte[] sendData = new byte[1024];
			for(int x=0;x<players.size();x++){
				sendData= data.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, players.get(x).getIP(),players.get(x).getPort());
				try {
					serverSock.send(sendPacket);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					serverLog.addMessage(new Message("ERROR", e.getMessage(),Color.RED));
					
				}
			}
		}
	}
	private Image dbImage; 
	private Graphics dbg; 
	@Override
	public void update(Graphics g){
		//code for double buffering. gets a image of the canvas and displays it, then it is cleared and paint is called
		if (dbImage == null) {
			dbImage = createImage (this.getSize().width, this.getSize().height); 
			dbg = dbImage.getGraphics (); 
		} 
		// clear screen in background 
		dbg.setColor (getBackground ()); 
		dbg.fillRect (0, 0, this.getSize().width, this.getSize().height); 

		// draw elements in background 
		dbg.setColor (getForeground()); 
		paint (dbg); 

		// draw image on the screen 
		try{
			g.drawImage (dbImage, 0, 0, this); 
		}catch(Exception ex){
			System.out.println("error painting");
		}
		
	}
	
	public void paint(Graphics g){
		setSize(1000,500);
		serverLog.draw(g);
		userList.draw(g);
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		startServer();
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public long getTime(){
		return System.currentTimeMillis();
	}
	long lastTime = getTime();
	public int getDelta(){
		long time=getTime();
		int delta=(int)(time-lastTime);
		lastTime=time;
		return (Integer) delta;
	}
	public int stn(String input){//string to number
		return Integer.parseInt(input);
	}
	public static String rn(){
		String hex="";
		for(int x=0;x<15;x++){
			if(Math.random()>.5){
				double temp=(Math.floor(Math.random()*57)+65);
				char test=(char) temp;
				while(temp>=91 && temp<=96){
					temp=(Math.floor(Math.random()*57)+65);
				}
				test=(char) temp;
				hex=hex+test;
			}else{
				hex=hex+(int)(Math.random()*10);
			}
		}
		return hex;
	}
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
	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		if(serverLog.contains(arg0.getX(), arg0.getY())){
			serverLog.reset();
		}
	}
}
