package DataTypes;

import java.net.InetAddress;

public class Player {
	int x,y;
	String username;
	Power power;
	String ID;
	long lastTimeSeen;
	InetAddress  IP;
	public Player(int x,int y, String username, Power power,InetAddress ip){
		this.x=x;
		this.IP=ip;
		this.y=y;
		this.username=username;
		this.power=power;
		//this.ID=ID;
	}
	public Player(int x, int y,String username, Power power,String ID){
		this.x=x;
		this.y=y;
		this.username=username;
		this.power=power;
		this.ID=ID;
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
