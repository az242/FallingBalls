package Client;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import Chat.ArrayChat;
import Chat.Message;
import DataTypes.Player;
import DataTypes.Power;
import Server.Server.ServerConnection;

public class Client extends Applet implements MouseListener, ActionListener,KeyListener,MouseMotionListener{
	boolean typing;
	ArrayList<Player> players;
	ArrayChat chatBox;
	String username="test";
	boolean connected= false;
	private Font font;
	public Client(){
		addMouseMotionListener(this);
		chatBox = new ArrayChat(700,0,1000,250,20,true);
		setFocusable(true);
		addMouseListener(this);
		addKeyListener(this);
		typing=false;
		players = new ArrayList<Player>();
		font = new Font("Arial", Font.PLAIN, 12);
		Timer myTimer;
		myTimer=new Timer(30, this);
		myTimer.start();
	}
	ClientConnection comm;
	public void startConnection(){
		comm=new ClientConnection("47.20.145.40");
		Thread servertest=new Thread(comm);
		servertest.start();
		String test = System.currentTimeMillis() + "~Connect"+"~"+username+"~500~450~255~0~0~1";
		//name x y R G B power
		comm.send(test);
		//name message R G B
		connected=true;
	}
	@Override
	public void destroy(){
		if(comm!=null)
			comm.send(System.currentTimeMillis()+"~Disconnect~"+username);
	}
	public class ClientConnection implements Runnable{
		boolean run;
		DatagramSocket clientSocket;
		InetAddress IPAddress;
		public ClientConnection(String IP){
			run=true;
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
		final String Failure = "Failed";
		final String Cords = "C";
		final String Chat = "CH";
		final String Move = "M";
		final String Power = "P";
		final String Success = "Succ";
		public void process(String[] data){
			int CP=1;
			while(CP<data.length-1){
				if(data[CP].equals(Cords)){
					//name x y
					for(int x=0;x<players.size();x++){
						if(players.get(x).getName().equals(data[CP+1]) && Long.parseLong(data[0])-players.get(x).lastTimeSeen>0){
							int[] test = {Integer.parseInt(data[CP+2]),Integer.parseInt(data[CP+3])};
							players.get(x).setCords(test[0],test[1]);
							players.get(x).lastTimeSeen=Long.parseLong(data[0]);
						}
					}
					CP = CP+4;
				}else if(data[CP].equals(Power)){
					//name x y power
					CP = CP+5;
				}else if(data[CP].equals(Connect)){
					//name x y R G B power
					players.add(new Player(Integer.parseInt(data[CP+2]), Integer.parseInt(data[CP+3]), data[CP+1],getPower(Integer.parseInt(data[CP+7])),new Color(Integer.parseInt(data[CP+4]),Integer.parseInt(data[CP+5]),Integer.parseInt(data[CP+6]))));
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
					chatBox.addMessage(new Message(data[CP+1],data[CP+2],new Color(Integer.parseInt(data[CP+3]), Integer.parseInt(data[CP+4]),Integer.parseInt(data[CP+5]))));
					CP = CP+6;
				}else if(data[CP].equals(Failure)){
					JOptionPane.showConfirmDialog(null, "Can't join! There is already a user with that name!");
				}else if(data[CP].equals(Success)){
					//name x y R G B power
					players.add(new Player(Integer.parseInt(data[CP+2]), Integer.parseInt(data[CP+3]), data[CP+1],getPower(Integer.parseInt(data[CP+7])),new Color(Integer.parseInt(data[CP+4]),Integer.parseInt(data[CP+5]),Integer.parseInt(data[CP+6]))));
					CP=CP+8;
				}
			
			}
		}
		public void send(String packet){
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
		setSize(1000,500);
		g.setFont(font);
		g.setColor(Color.BLACK);
		g.drawLine(0,450,1000,450);
		for(int x=0;x<players.size();x++){
			g.setColor(players.get(x).getColor());
			FontMetrics fm   = g.getFontMetrics(font);
			java.awt.geom.Rectangle2D rect = fm.getStringBounds(players.get(x).getName(), g);
			g.drawString(players.get(x).getName(), (int) (players.get(x).getX()-(rect.getWidth()/2)), players.get(x).getY()-25);

			g.drawRect(players.get(x).getCords()[0]-5, players.get(x).getCords()[1]-25, 10, 25);
		}
		chatBox.draw(g);
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
		if(!connected){
			username = JOptionPane.showInputDialog("Username?");
			startConnection();
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getKeyCode()==KeyEvent.VK_ENTER){
			if(typing){
				typing = false;
				comm.send(System.currentTimeMillis()+"~CH~"+players.get(0).getName()+"~"+Typed+"~"+players.get(0).getColor().getRed()+"~"+players.get(0).getColor().getGreen()+"~"+players.get(0).getColor().getBlue());
				//name message R G B
				Typed = "";
			}else{
				typing = true;
			}
		}
		if(typing && arg0.getKeyCode()!=KeyEvent.VK_ENTER){
			Typed = Typed+arg0.getKeyChar();
		}
		if(!typing){
			switch(arg0.getKeyCode()){
			case KeyEvent.VK_W:
				comm.send(System.currentTimeMillis() + "~M~"+username+"~WPress");
				break;
			case KeyEvent.VK_A:
				comm.send(System.currentTimeMillis() + "~M~"+username+"~APress");
				break;
			case KeyEvent.VK_S:
				comm.send(System.currentTimeMillis() + "~M~"+username+"~SPress");
				break;
			case KeyEvent.VK_D:
				comm.send(System.currentTimeMillis() + "~M~"+username+"~DPress");
				break;
				
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getKeyCode()){
		case KeyEvent.VK_W:
			comm.send(System.currentTimeMillis() + "~M~"+username+"~WRelease");
			break;
		case KeyEvent.VK_A:
			comm.send(System.currentTimeMillis() + "~M~"+username+"~ARelease");
			break;
		case KeyEvent.VK_S:
			comm.send(System.currentTimeMillis() + "~M~"+username+"~SRelease");
			break;
		case KeyEvent.VK_D:
			comm.send(System.currentTimeMillis() + "~M~"+username+"~DRelease");
			break;
			
		}
	}
	String Typed="";
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
	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		if(chatBox.contains(e.getX(), e.getY())){
			chatBox.reset();
		}
	}
}
