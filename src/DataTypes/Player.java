package DataTypes;

import java.net.InetAddress;

public class Player {
	int x,y;
	String username;
	Power power;
	String ID;
	public long lastTimeSeen;
	InetAddress  IP;
	int port;
	public Player(int x,int y, String username, Power power,InetAddress ip,int port){
		this.port=port;
		this.x=x;
		this.IP=ip;
		this.y=y;
		this.username=username;
		this.power=power;
		//this.ID=ID;
	}
	public Player(int x, int y,String username, Power power){
		this.x=x;
		this.y=y;
		this.username=username;
		this.power=power;
	}
	public int getPort(){
		return port;
	}
	public InetAddress getIP(){
		return IP;
	}
	public Power getPower(){
		return power;
	}
	public String getName(){
		return username;
	}
	public String getID(){
		return ID;
	}
	public int[] getCords(){
		return new int[]{x,y};
	}
	public void setCords(int[] cords){
		x = cords[0];
		y = cords[1];
	}
}
