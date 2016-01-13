package Client;

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
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.Timer;

import Chat.Message;
import DataTypes.Player;
import DataTypes.Power;
import Server.Server.ServerConnection;

public class Client extends Applet implements MouseListener, ActionListener,KeyListener{
	ArrayList<Player> players;
	public Client(){
		setFocusable(true);
		addMouseListener(this);
		addKeyListener(this);
		players = new ArrayList<Player>();
		Timer myTimer;
		myTimer=new Timer(50, this);
		myTimer.start();
		startConnection();
		//System.out.println("Test");
		String test = System.currentTimeMillis() + "~Connect"+"~indeed"+"~50" + "~50~255~255~0~1";
		//name x y R G B power
		comm.send(test);
		//name message R G B
		test = System.currentTimeMillis() + "~CH~indeed~my message~255~255~0";
		comm.send(test);
		//System.out.println("Test test");
	}
	ClientConnection comm;
	public void startConnection(){
		comm=new ClientConnection("127.0.0.1");
		Thread servertest=new Thread(comm);
		servertest.start();
	}
	@Override
	public void destroy(){
		comm.send(System.currentTimeMillis()+"~Disconnect~indeed");
	}
	public class ClientConnection implements Runnable{
		boolean run;
		DatagramSocket clientSocket;
		InetAddress IPAddress;
		public ClientConnection(String IP){
			try {
				clientSocket = new DatagramSocket();
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				IPAddress = InetAddress.getByName(IP);
				//System.out.println(IPAddress.toString());
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				byte[] receiveData = new byte[1024];
				while(run){
					DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
					clientSocket.receive(receivePacket);
					String message = new String(receivePacket.getData()).trim();
					//System.out.println(message);
					process(message.split("~"));
					receiveData= new byte[1024];
				}
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
		final String Connect = "Connect";
		final String Disconnect = "Disconnect";
		final String Cords = "C";
		final String Chat = "CH";
		final String Move = "M";
		final String Power = "P";
		public void process(String[] data){
			int CP=1;
			while(CP<data.length-1){
				if(data[CP].equals(Cords)){
					//name x y
					for(int x=0;x<players.size();x++){
						if(players.get(x).getName().equals(data[CP+1]) && Long.parseLong(data[0])-players.get(x).lastTimeSeen>0){
							int[] test = {Integer.parseInt(data[CP+1]),Integer.parseInt(data[CP+2])};
							players.get(x).setCords(test);
						}
					}
					CP = CP+4;
				}else if(data[CP].equals(Power)){
					//name x y power
					CP = CP+5;
				}else if(data[CP].equals(Connect)){
					//name x y R G B power
					players.add(new Player(Integer.parseInt(data[CP+2]), Integer.parseInt(data[CP+3]), data[CP+1],getPower(Integer.parseInt(data[CP+7]))));
					//send data to all players
					CP = CP+8;
				}else if(data[CP].equals(Disconnect)){
					//name
					for(int x=0;x<players.size();x++){
						if(players.get(x).getName().equals(data[CP+1])){
							players.remove(x);
						}
					}
					//send data to all players
					CP = CP+2;
				}else if(data[CP].equals(Chat)){
					//name message R G B
					
					CP = CP+6;
				}
			}
		}
		public void send(String packet){//packet is usually x,y
			try {
				byte[] sendData = new byte[1024];
				sendData = packet.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9999);
				clientSocket.send(sendPacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void paint(Graphics g){
		setSize(200,200);
		for(int x=0;x<players.size();x++){
			g.drawRect(players.get(x).getCords()[0]-5, players.get(x).getCords()[1], 10, 25);
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
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
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

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getKeyCode()){
		case KeyEvent.VK_W:
			break;
		case KeyEvent.VK_A:
			break;
		case KeyEvent.VK_S:
			break;
		case KeyEvent.VK_D:
			break;
			
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
}
