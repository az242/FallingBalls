package Chat;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class Message implements ActionListener{
	static Color defaultUserColor = new Color(100,100,100);
	private String username, message;
	Color userColor;
	private int alpha;
	Timer myTimer;
	private final int stopwatch = 500;
	public Message(String name, String message,Color red){
		this.username=  name;
		this.userColor=red;
		this.message=message;
		alpha = stopwatch ;
		myTimer=new Timer(30, this);
		myTimer.start();
		
	}
	public Message(String name, String message){
		this.username=  name;
		this.message=message;
		this.userColor=defaultUserColor;
		alpha = stopwatch ;
		myTimer=new Timer(30, this);
		myTimer.start();
	}
	public void freeze(){
		myTimer.stop();
	}
	public String getMessage(){
		return message;
	}
	public int getAlpha(){
		if(alpha>255){
			return 255;
		}else{
			return alpha;
		}
	}
	public void reset(){
		alpha=stopwatch;
		myTimer.start();
	}
	public String getUsername(){
		return username;
	}
	public String getFMessage(){
		return ": "+message;
	}
	public String getFormatedMessage(){
		return username + ": "+message;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if(alpha>255){
			alpha-=1;
		}else{
			alpha-=5;
		}
		if(alpha==0){
			myTimer.stop();
		}
	}
}
