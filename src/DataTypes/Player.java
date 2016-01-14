package DataTypes;

import java.awt.Color;
import java.net.InetAddress;

public class Player {
	int x,y;
	int dx,dy;
	String username;
	Power power;
	boolean jumping;
	String ID;
	public long lastTimeSeen;
	InetAddress  IP;
	int port;
	Color col;
	public Player(int x,int y, String username, Power power,InetAddress ip,int port){
		this.port=port;
		this.x=x;
		this.IP=ip;
		this.y=y;
		this.username=username;
		this.power=power;
		jumping = false;
		//this.ID=ID;
	}
	public Player(int x, int y,String username, Power power,Color color){
		col=color;
		this.x=x;
		this.y=y;
		this.username=username;
		this.power=power;jumping = false;
	}
	public Color getColor(){
		return col;
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
	public boolean isJumping(){
		return jumping;
	}
	public void stopJump(){
		jumping=false;
		dy=0;
	}
	public void startJump(){
		if(!jumping){
			jumping=true;
			dy=-10;
		}
	}
	public void setDy(int dY){
		dy=dY;
	}
	public void setDx(int dX){
		dx=dX;
	}
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	public int getDx(){
		return dx; 
	}
	public int getDy(){
		return dy;
	}
	public void setCords(int x, int y){
		this.x = x;
		this.y = y;
	}
}
