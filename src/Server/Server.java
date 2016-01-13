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
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import javax.swing.Timer;


import Chat.ArrayChat;
import Chat.Message;
import DataTypes.Player;
public class Server extends Applet implements MouseListener,ActionListener,MouseMotionListener{
	ArrayChat serverLog;
	
	ArrayList<Player> players = new ArrayList<Player>();
	ServerConnection interwebs;
	public Server(){
		setFocusable(true);
		addMouseListener(this);
		addMouseMotionListener(this);
		setSize(1000,500);
		serverLog = new ArrayChat(200,0,600,450,32,true);
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
				byte[] receiveData = new byte[10240];
				byte[] sendData = new byte[10240];
				while (run) {
					DatagramPacket receivePacket = new DatagramPacket(receiveData,receiveData.length);
					serverSock.receive(receivePacket);
					String data = new String(receivePacket.getData());
					//do something with ddata
					process(data.split(" "));
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
		public void process(String data[]){
			//slot 0 is time
			int currentPosition=1;
			while(currentPosition<data.length-1){
				if(data[currentPosition].equals(Cords)){
					//name x y time
					currentPosition = currentPosition+4;
				}else if(data[currentPosition].equals(Power)){
					//name x y power time
					currentPosition = currentPosition+5;
				}else if(data[currentPosition].equals(Connect)){
					//name time
					serverLog.addMessage(new Message("System", data[currentPosition+1] + " has connected to server."));
					currentPosition = currentPosition+2;
				}else if(data[currentPosition].equals(Disconnect)){
					//name
					serverLog.addMessage(new Message("System", data[currentPosition+1] + " has Disconnected from the server."));
					currentPosition = currentPosition+1;
				}else if(data[currentPosition].equals(Chat)){
					//name message time
					currentPosition = currentPosition+3;
				}
			}
			//keys pressed
			
			
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
