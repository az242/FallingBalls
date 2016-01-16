package Server;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
import DataTypes.Ball;
import DataTypes.Player;
import DataTypes.Power;
public class Server extends Applet implements MouseListener,ActionListener,MouseMotionListener,KeyListener{
	ArrayChat serverLog;
	ArrayChat userList;
	ArrayList<Player> players;
	ArrayList<Ball> balls;
	ServerConnection interwebs;
	BGenerator BG;
	public Server(){
		addKeyListener(this);
		setFocusable(true);
		addMouseListener(this);
		addMouseMotionListener(this);
		setSize(1000,500);
		balls=new ArrayList<Ball>();
		players = new ArrayList<Player>();
		userList = new ArrayChat(600,0, 750,450,32,false);//used to display stuff about users
		serverLog = new ArrayChat(200,0,600,450,32,true);// magic number 32 for max chat entries displayed
		serverLog.addMessage(new Message("System", "Start Up"));
		Timer myTimer;
		myTimer=new Timer(30, this);
		myTimer.start();
	}
	@Override
	public void destroy(){
		if(comm!=null)
			comm.send(System.currentTimeMillis()+"~ServerErrorDC");
		System.out.println("Exited");
	}
	ServerConnection comm;
	public void startServer(){
		 comm = new ServerConnection(9999);
		Thread servertest=new Thread(comm);
		servertest.start();
	}
	public void startBalls(){
		BG = new BGenerator();
		balls = new ArrayList<Ball>();
	}
	public void reset(){
		BG.stop();
		BG = null;
		String message = System.currentTimeMillis()+"";
		for(int x=0;x<balls.size();x++){
			message = message + "~BD~"+balls.get(x).getID();
		}
		comm.send(message);
		balls = new ArrayList<Ball>();
	}
	public class BGenerator implements ActionListener{
		Timer myTimer;
		public BGenerator(){
			
			myTimer=new Timer(100, this);
			myTimer.start();
			serverLog.addMessage(new Message("Server","Starting ball Generator."));
		}
		public void stop(){
			myTimer.stop();
		}
		public void createBall(){
			//x y r C dy dx
			int x= (int) (Math.random()*1000);
			int radius = (int) (Math.random()*30);
			int y = 0-radius;
			int dy = (int) (Math.random()*500);
			int dx = (int) (Math.random()*500);
			if(Math.random()>.5){
				dx = -dx;
			}
			Color col = new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));
			String id = rn();
			long time = System.currentTimeMillis();
			balls.add(new Ball(id,x,y,radius,col,dy,dx,time));
			comm.send(System.currentTimeMillis()+"~BC~"+id+"~"+x+"~"+y+"~"+radius+"~"+col.getRed()+"~"+col.getGreen()+"~"+col.getBlue()+"~"+dy+"~"+dx+"~"+time);
		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			createBall();
		}
	}
	public class ServerConnection implements Runnable{
		public boolean run;
		DatagramSocket serverSock;
		public ServerConnection(int port){
			serverLog.addMessage(new Message("System", "Attempting to start socket at port "+port));
			
			 try {
				serverSock = new DatagramSocket(port);
				serverLog.addMessage(new Message("System", "Socket Established"));
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//serverLog.addMessage(new Message("ERROR", e.getMessage(),Color.RED));
				run= false;
			}
			 run = true;
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
					String data = new String(receivePacket.getData()).trim();
					//do something with ddata
					//System.out.println(data);
					process(data.split("~"),receivePacket.getAddress(),receivePacket.getPort());
					receiveData = new byte[1024];
				} 
				System.out.println("Something went wrong");
			}catch (Exception ex){
				ex.printStackTrace();
				//serverLog.addMessage(new Message("ERROR", ex.getMessage(),Color.RED));
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
							players.get(x).setCords(test[0],test[1]);
							userList.chat.get(x).setMessage(test[0]+", "+test[1]);
						}
					}
					send(data[0] +"~"+ data[CP]+"~"+ data[CP+1]+"~"+ data[CP+2]+"~"+ data[CP+3]);
					//SEND DATA TO ALL OTHER PLAYERS
					CP = CP+4;
				}else if(data[CP].equals(Power)){
					//name x y power
					send(data[0]+"~" + data[CP]+"~"+ data[CP+1]+"~"+ data[CP+2]+"~"+ data[CP+3]+"~"+data[CP+4]);
					CP = CP+5;
				}else if(data[CP].equals(Connect)){
					//name x y R G B power
					boolean accept=true;
					for(int x=0;x<players.size();x++){
						if(players.get(x).getName().equals(data[CP+1])){
							accept=false;
						}
					}
					if(accept){
						serverLog.addMessage(new Message("System", data[CP+1] + " has connected to server."));
						players.add(new Player(Integer.parseInt(data[CP+2]), Integer.parseInt(data[CP+3]), data[CP+1],getPower(Integer.parseInt(data[CP+7])),new Color(Integer.parseInt(data[CP+4]),Integer.parseInt(data[CP+5]),Integer.parseInt(data[CP+6])),inetAddress,port));
						userList.addMessage(new Message(data[CP+1], data[CP+2] +", "+ data[CP+3],new Color(Integer.parseInt(data[CP+4]),Integer.parseInt(data[CP+5]),Integer.parseInt(data[CP+6])) ));
						send(data[0]+"~"+data[CP]+"~" + data[CP+1]+"~"+ data[CP+2]+"~"+ data[CP+3]+"~"+ data[CP+4]+"~"+data[CP+5]+"~"+data[CP+6]+"~"+data[CP+7]);
						String temp=System.currentTimeMillis()+"";
						for(int x=0;x<players.size()-1;x++){
							temp = temp + "~Succ~"+players.get(x).getName()+"~"+players.get(x).getX()+"~"+players.get(x).getY()+"~"+players.get(x).getColor().getRed()+"~"+players.get(x).getColor().getGreen()+"~"+players.get(x).getColor().getBlue()+"~"+players.get(x).getPower().getIndex();
						}
						if(players.size()>1)
							send(temp,inetAddress ,port); 
					}else{
						serverLog.addMessage(new Message("System","User tried to connect with " + data[CP+1] + ", already exists!"));
						send(System.currentTimeMillis() +"~"+"Failed"+"~"+data[CP+1],inetAddress,port);
						
					}
					//send data to all players
					CP = CP+8;
				}else if(data[CP].equals(Disconnect)){
					//name
					for(int x=0;x<players.size();x++){
						if(players.get(x).getName().equals(data[CP+1])){
							players.remove(x);
							userList.chat.remove(x);
						}
					}
					//System.out.println("test 1");
					send(data[0] +"~"+ data[CP]+"~"+ data[CP+1]);
					//send data to all players
					serverLog.addMessage(new Message("System", data[CP+1] + " has Disconnected from the server."));
					CP = CP+2;
				}else if(data[CP].equals(Chat)){
					//name message R G B
					serverLog.addMessage(new Message(data[CP+1],data[CP+2],new Color(Integer.parseInt(data[CP+3]),Integer.parseInt(data[CP+4]),Integer.parseInt(data[CP+5]))));
					//send data to all other players
					send(data[0]+"~" + data[CP]+"~"+ data[CP+1]+"~"+ data[CP+2]+"~"+ data[CP+3]+"~"+data[CP+4] +"~"+ data[CP+5]);
					CP = CP+6;
				}else if(data[CP].equals(Move)){
					//name keyPress
					for(int x=0;x<players.size();x++){
						if(players.get(x).getName().equals(data[CP+1])){
							switch(data[CP+2]){
							case "WPress":
								players.get(x).startJump();
								break;
							case "APress":
								players.get(x).setDx(-100);
								break;
							case "SPress":
								break;
							case "DPress":
								players.get(x).setDx(100);
								break;
							case "WRelease":
								//nothing
								break;
							case "ARelease":
								if(players.get(x).getDx()==-100)
									players.get(x).setDx(0);
								break;
							case "SRelease":
								break;
							case "DRelease":
								if(players.get(x).getDx()==100)
									players.get(x).setDx(0);
								break;
							}
						}
					}
					CP = CP +2;
				}else{
					CP = CP++;
					//Couldn't find anything!
				}
			}
		}
		public void send(String data, InetAddress ip,int port){
			byte[] sendData = new byte[1024];
			sendData = data.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip,port);
			//System.out.println(sendPacket.getAddress().toString()+": "+sendPacket.getPort());
			try {
				serverSock.send(sendPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//serverLog.addMessage(new Message("ERROR", "Failed to send packet: "+e.getMessage(),Color.RED));
				
			}
		}
		public void send(String data){
			byte[] sendData = new byte[1024];
			sendData= data.getBytes();
			for(int x=0;x<players.size();x++){
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, players.get(x).getIP(),players.get(x).getPort());
//				/System.out.println(sendPacket.getAddress().toString()+": "+sendPacket.getPort());
				try {
					serverSock.send(sendPacket);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					//serverLog.addMessage(new Message("ERROR", "Failed to send packet: "+e.getMessage(),Color.RED));
					
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
		g.drawString("balls: "+balls.size(), 10, 50);
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		repaint();
		double delta= (double)getDelta()/1000;
		String message=""+System.currentTimeMillis();
		//System.out.println(delta);
		for(int x=0;x<players.size();x++){
			players.get(x).setCords((int) (players.get(x).getX()+((double)players.get(x).getDx()*delta)),players.get(x).getY()+players.get(x).getDy());
			if(players.get(x).isJumping()){
				if(players.get(x).getDy()==10){
					players.get(x).setCords(players.get(x).getX(), 450);//Magic number GROUND FLOOR
					players.get(x).stopJump();
				}else{
					players.get(x).setDy(players.get(x).getDy()+1);
				}
			}
			userList.chat.get(x).setMessage(players.get(x).getX()+", "+players.get(x).getY());
			message= message+"~C~"+players.get(x).getName()+"~"+players.get(x).getX()+"~"+players.get(x).getY();
		}
		
		for(int x=0;x<balls.size();x++){
			if((balls.get(x).getX()+balls.get(x).getRadius())<0 || (balls.get(x).getX()-balls.get(x).getRadius())>1000|| (balls.get(x).getY()-balls.get(x).getRadius())>500){
				comm.send(System.currentTimeMillis()+"~BD~"+balls.get(x).getID());
				balls.remove(x);
			}else{
				balls.get(x).update(delta);
				//message = message + "~B~"+balls.get(x).getID()+"~"+balls.get(x).getX()+"~"+balls.get(x).getY();
			}
		}
		
		if(comm!=null && comm.run==true && message.length()>0){
			comm.send(message);//System.out.println("uhhh");
		}
		//send data
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		if(comm==null)
			startServer();
		else
			serverLog.addMessage(new Message("ERROR","Server already active!",Color.RED));
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
		for(int x=0;x<7;x++){
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
	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getKeyCode()==KeyEvent.VK_J){
			startBalls();
		}else if(arg0.getKeyCode()==KeyEvent.VK_K){
			reset();
		}
	}
	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
