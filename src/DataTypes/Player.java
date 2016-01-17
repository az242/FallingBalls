package DataTypes;

import java.awt.Color;
import java.awt.Rectangle;
import java.net.InetAddress;

public class Player {
	public static int jumpConstant = 12;
	int x,y;
	int dx,dy;
	String username;
	Power power;
	boolean jumping;
	boolean dead;
	String ID;
	public long lastTimeSeen;
	InetAddress  IP;
	int port;
	Color col;
	public Player(int x,int y, String username, Power power,Color color,InetAddress ip,int port){
		this.port=port;
		this.x=x;
		this.IP=ip;
		this.col=color;
		this.y=y;
		this.username=username;
		this.power=power;
		jumping = false;
		dead = false;
		//this.ID=ID;
	}
	public Player(int x, int y,String username, Power power,Color color){
		col=color;
		this.x=x;
		this.y=y;
		this.username=username;
		this.power=power;jumping = false;
		dead = false;
	}
	public boolean isDead(){
		return dead;
	}
	public void Died(){
		dead = true;
	}
	public void revive(){
		dead = false;
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
			dy=-jumpConstant;
		}
	}
	public Rectangle getRect(){
		Rectangle rect = new Rectangle(x-5,y-25,10,25);
		return rect;
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
