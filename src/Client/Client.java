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
import DataTypes.Ball;
import DataTypes.Player;
import DataTypes.Power;
import Server.Server.ServerConnection;

public class Client extends Applet implements MouseListener, ActionListener,KeyListener,MouseMotionListener{
	boolean typing;
	ArrayList<Player> players;
	ArrayList<Ball> balls;
	ArrayChat chatBox;
	String username="test";
	boolean skill=true;
	double time=0;
	boolean connected= false;
	boolean Dead;
	private Font font;
	int mX;
	int mY;
	public Client(){
		addMouseMotionListener(this);
		chatBox = new ArrayChat(700,0,1000,250,20,true);
		setFocusable(true);
		addMouseListener(this);
		addKeyListener(this);
		typing=false;
		Dead = false;
		balls= new ArrayList<Ball>();
		players = new ArrayList<Player>();
		font = new Font("Arial", Font.PLAIN, 12);
		mX = 0;
		mY= 0;
		Timer myTimer;
		myTimer=new Timer(30, this);
		myTimer.start();
	}
	ClientConnection comm;
	public void startConnection(){
		comm=new ClientConnection("127.0.0.1");
		Thread servertest=new Thread(comm);
		servertest.start();
		String test = System.currentTimeMillis() + "~Connect"+"~"+username+"~500~450~255~0~100~1";
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
		final static String Connect = "Connect";
		final static String Disconnect = "Disconnect";
		final static String Failure = "Failed";
		final static String Cords = "C";
		final static String Chat = "CH";
		final static String Move = "M";
		final static String Power = "P";
		final static String Success = "Succ";
		final static String BallCreate = "BC";
		final static String BallDestroy = "BD";
		final static String BallCord = "B";
		final static String serverDC = "ServerErrorDC";
		final static String Death = "D";
		final static String Reset = "Reset";
		final static String Ready = "R";
		public void process(String[] data){
			int CP=1;
			while(CP<data.length){
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
					//name x y power time
					for(int x=0;x<players.size();x++){
						if(players.get(x).getName().equals(data[CP+1])){
							skill = false;
							time = Integer.parseInt(data[CP+5]);
							return;
						}
					}
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
					JOptionPane.showMessageDialog(null, "Can't join! There is already a user with that name!");
				}else if(data[CP].equals(Success)){
					//name x y R G B power
					players.add(new Player(Integer.parseInt(data[CP+2]), Integer.parseInt(data[CP+3]), data[CP+1],getPower(Integer.parseInt(data[CP+7])),new Color(Integer.parseInt(data[CP+4]),Integer.parseInt(data[CP+5]),Integer.parseInt(data[CP+6]))));
					CP=CP+8;
				}else if(data[CP].equals(BallCreate)){
					// id x y r R G B dy dx time
					balls.add(new Ball(data[CP+1],Integer.parseInt(data[CP+2]),Integer.parseInt(data[CP+3]) , Integer.parseInt(data[CP+4]),new Color(Integer.parseInt(data[CP+5]),Integer.parseInt(data[CP+6]),Integer.parseInt(data[CP+7])),Integer.parseInt(data[CP+8]),Integer.parseInt(data[CP+9]),Long.parseLong(data[CP+10])));
					CP = CP + 11;
				}else if(data[CP].equals(BallCord)){
					// id x y
					//NOT USED
					for(int x=0;x<balls.size();x++){
						if(balls.get(x).getID().equals(data[CP+1])){
							balls.get(x).setX(Integer.parseInt(data[CP+2]));
							balls.get(x).setY(Integer.parseInt(data[CP+3]));
						}
					}
					CP=CP+4;
				}else if(data[CP].equals(BallDestroy)){
					// id
					for(int x=0;x<balls.size();x++){
						if(balls.get(x).getID().equals(data[CP+1])){
							//System.out.println("Destroy");
							balls.remove(x);
							x--;
						}
					}
					CP = CP+2;
				}else if(data[CP].equals(serverDC)){
					JOptionPane.showMessageDialog(null, "Server Experienced an error! \nDisconnected!");
					comm=null;
					connected=false; 
					CP = CP+1;
				}else if(data[CP].equals(Death)){
					//name
					for(int x=0;x<players.size();x++){
						if(players.get(x).getName().equals(data[CP+1])){
							players.get(x).Died();
						}
					}
					if(username.equals(data[CP+1])){
						Dead=true; 
					}
					CP=CP+2;
				}else if(data[CP].equals(Reset)){
					for(int x=0;x<players.size();x++){
						players.get(x).revive();
					}
					balls=new ArrayList<Ball>();
					Dead = false;
					CP = CP+1;
				}else if(data[CP].equals(Ready)){
					//name
					for(int x=0;x<players.size();x++){
						if(players.get(x).getName().equals(data[CP+1])){
							players.get(x).setReady(!players.get(x).isReady());
						}
					}
					CP = CP+2;
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
		if(!connected)
			return;
		g.setColor(new Color(50,50,50,100));
		g.drawRect(mX-5, players.get(0).getY()-25, 10, 25);
		for(int x=0;x<balls.size();x++){
			balls.get(x).draw(g);
		}
		for(int x=0;x<players.size();x++){
			g.setColor(players.get(x).getColor());
			FontMetrics fm   = g.getFontMetrics(font);
			java.awt.geom.Rectangle2D rect = fm.getStringBounds(players.get(x).getName(), g);
			if(!players.get(x).isDead()){
				g.drawString(players.get(x).getName(), (int) (players.get(x).getX()-(rect.getWidth()/2)), players.get(x).getY()-25);
				g.drawRect(players.get(x).getCords()[0]-5, players.get(x).getCords()[1]-25, 10, 25);
				if(players.get(x).isReady()){
					g.setColor(new Color(153,101,21));
					rect = fm.getStringBounds("ready", g);
					g.drawString("ready", (int) (players.get(x).getX()-(rect.getWidth()/2)), (int) (players.get(x).getY()-25-rect.getHeight()));
				}
			}else{
				g.drawString(players.get(x).getName(), (int) (players.get(x).getX()-(rect.getWidth()/2)), players.get(x).getY());
				if(players.get(x).isReady()){
					rect = fm.getStringBounds("ready", g);
					g.setColor(new Color(153,101,21));
					g.drawString("ready", (int) (players.get(x).getX()-(rect.getWidth()/2)), (int) (players.get(x).getY()-rect.getHeight()));
				}
			}
		}
		chatBox.draw(g);
		g.setColor(Color.BLACK);
		g.drawString(Typed, chatBox.getX(), chatBox.getHeight()+12);
		if(!skill)
			g.drawString((int)(time/60)+":"+(int)(time%60), mX, mY);
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
		double delta= (double)getDelta()/1000.0;
		if(time>0){
			time = time - delta;
		}else{
			skill = true;
		}
		try{
			for(int x=0;x<balls.size();x++){
				balls.get(x).update((System.currentTimeMillis()-balls.get(x).getTime())/1000.0);
				balls.get(x).setTime(System.currentTimeMillis());
			}
		}catch(Exception E){
			
		}
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
			while(username.contains("~") || username.length()>12 || username.length()==0){
				username = JOptionPane.showInputDialog("Something is wrong with your Username! Try a new one.");
			}
			startConnection();
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyPressed(KeyEvent arg0) {
		if(!connected)
			return;
		// TODO Auto-generated method stub
		if(arg0.getKeyCode()==KeyEvent.VK_ENTER){
			if(typing ){
				typing = false;
				if(Typed.length()>0)
					comm.send(System.currentTimeMillis()+"~CH~"+players.get(0).getName()+"~"+Typed+"~"+players.get(0).getColor().getRed()+"~"+players.get(0).getColor().getGreen()+"~"+players.get(0).getColor().getBlue());
				//name message R G B
				Typed = "";
			}else{
				typing = true;
			}
		}
		if(typing && arg0.getKeyCode()!=KeyEvent.VK_ENTER && arg0.getKeyCode()!=KeyEvent.VK_SHIFT){
			if(arg0.getKeyCode()==KeyEvent.VK_BACK_SPACE){
				if(Typed.length()==0){
					
				}else if(Typed.length()==1){
					Typed = "";
				}else{
					Typed = Typed.substring(0,Typed.length()-2);
				}
			}else{
				if(arg0.getKeyChar()!='~' && arg0.getKeyCode()!=KeyEvent.VK_BACK_SPACE){
					Typed = Typed+arg0.getKeyChar();
				}
			}
		}
		if(!typing && !Dead){
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
			case KeyEvent.VK_SPACE:
				//power1
				if(skill){
					comm.send(System.currentTimeMillis() + "~P~"+username+"~"+mX+"~"+players.get(0).getY()+"~"+players.get(0).getPower());
				}
				break; 
			}
			
		}
		if(arg0.getKeyCode()==KeyEvent.VK_R && !typing){
			comm.send(System.currentTimeMillis()+"~R~"+username);
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		if(true){
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
		mX = e.getX();
		mY = e.getY();
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
}
