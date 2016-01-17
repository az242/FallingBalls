package Chat;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;

public class ArrayChat {
	private int x1,x2;
	private int y1,y2;
	public ArrayList<Message> chat;
	private int maxsize;
	private Font font;
	private boolean fading;
	public ArrayChat(int x, int y,int x2, int y2,int size,boolean fading){ // default font used
		chat = new ArrayList<Message>();
		this.fading=fading;
		maxsize = size;
		x1=x;this.x2=x2;
		this.y1=y;this.y2=y2;
		if(x1>x2){
			int temp = x1;
			x1=x2;
			x2=temp;
		}
		if(y1>y2){
			int temp = y1;
			y1=y2;
			y2=temp;
		}
		font = new Font("Arial", Font.PLAIN, 12);
	}
	
	public ArrayChat(int x, int y,int x2, int y2,int size,boolean fading,Font font){ //user prompted font used
		chat = new ArrayList<Message>();
		maxsize = size;
		this.fading=fading;
		this.font = font;
		x1=x;this.x2=x2;
		this.y1=y;this.y2=y2;
		if(x1>x2){
			int temp = x1;
			x1=x2;
			x2=temp;
		}
		if(y1>y2){
			int temp = y1;
			y1=y2;
			y2=temp;
		}
	}
	public int getX(){
		return x1;
	}
	public int getY(){
		return y1;
	}
	public int getWidth(){
		return Math.abs(x1-x2);
	}
	public int getHeight(){
		return Math.abs(y1-y2);
	}
	public void addMessage(Message temp){
		if(!fading)
			temp.freeze();
		chat.add(temp);
	}
	public ArrayList<Message> getMessages(){
		return chat;
	}
	public void draw(Graphics g){
		g.setColor(Color.BLACK);
		g.drawRect(x1, y1, x2-x1, y2-y1);
		g.setFont(font);
		for(int offset = 0,chatEntry=chat.size()-1;chatEntry>=0 && offset<maxsize;chatEntry--,offset++){
			FontMetrics fm   = g.getFontMetrics(font);
			java.awt.geom.Rectangle2D rect = fm.getStringBounds(chat.get(chatEntry).getFormatedMessage(), g);
			if(rect.getWidth()>Math.abs(x1-x2)){
				java.awt.geom.Rectangle2D rect2 = fm.getStringBounds(chat.get(chatEntry).getUsername(), g);
				//if size of message is too large
				String[] components = wrapAround(chat.get(chatEntry),fm,g);
				if(fading)
					g.setColor(new Color(0,0,0,chat.get(chatEntry).getAlpha()));
				else
					g.setColor(Color.BLACK);
				for(int y=components.length-1;y>0;y--){ //0 is first line, 1 is second, ect
					g.drawString(components[y], (int) (x1+rect2.getWidth()), (int) (y2-((offset)*rect.getHeight())));
					offset++;
				}
				g.drawString(components[0], (int) (x1+rect2.getWidth()),  (int) (y2-((offset)*rect.getHeight())));
				Color temp=chat.get(chatEntry).userColor;
				if(fading)
					g.setColor(new Color(temp.getRed(),temp.getGreen(),temp.getBlue(),chat.get(chatEntry).getAlpha()));
				else
					g.setColor(new Color(temp.getRed(),temp.getGreen(),temp.getBlue()));
				g.drawString(chat.get(chatEntry).getUsername(), x1, (int) (y2-((offset)*rect.getHeight())));
				
			}else{
				//draw message into chat box;
				Color temp=chat.get(chatEntry).userColor;
				if(fading)
					g.setColor(new Color(temp.getRed(),temp.getGreen(),temp.getBlue(),chat.get(chatEntry).getAlpha()));
				else
					g.setColor(new Color(temp.getRed(),temp.getGreen(),temp.getBlue()));
				//g.setColor(new Color(temp.getRed(),temp.getGreen(),temp.getBlue(),chat.get(chatEntry).getAlpha()));
				java.awt.geom.Rectangle2D rect2 = fm.getStringBounds(chat.get(chatEntry).getUsername(), g);
				
				g.drawString(chat.get(chatEntry).getUsername(), x1, (int) (y2-((offset)*rect.getHeight())));
				if(chat.get(chatEntry).getMessage()!=null && chat.get(chatEntry).getMessage().length()<=0){
					return;
				}
				if(fading)
					g.setColor(new Color(0,0,0,chat.get(chatEntry).getAlpha()));
				else
					g.setColor(Color.BLACK);
				g.drawString(chat.get(chatEntry).getFMessage(), (int) (x1+rect2.getWidth()), (int) (y2-((offset)*rect.getHeight())));
				//g.drawLine(x1, (int)(y2-((offset)*rect.getHeight())), (int) (x1+rect2.getWidth()),(int) (y2-((offset)*rect.getHeight())));
			}
		}
	}
	public String[] wrapAround(Message message,FontMetrics fm,Graphics g){
		java.awt.geom.Rectangle2D username = fm.getStringBounds(message.getUsername(), g);
		java.awt.geom.Rectangle2D mess = fm.getStringBounds(message.getFMessage(), g);
		String fmess = message.getFMessage();
		ArrayList<String> messages = new ArrayList<String>();
		//actual cutting
		int numOfChar = (int) (((Math.abs(x1-x2)-username.getWidth())/(username.getWidth()+mess.getWidth()))*message.getFormatedMessage().length());// find ratio of message size compared to chatbox
		int index=numOfChar;
		messages.add(message.getFMessage().substring(0, index));
		while(index<message.getFMessage().length()){
			if(index+numOfChar>message.getFMessage().length()){
				messages.add(message.getFMessage().substring(index));
			}else{
				messages.add(message.getFMessage().substring(index, index+numOfChar));
			}
			index = index+numOfChar;
		}
		//
		String[] temp = new String[messages.size()];
		messages.toArray(temp);
		return temp;
	}
	public void reset(){
		if(!fading)
			return;
		for(int x=chat.size()-1,temp=0;x>=0 && temp<maxsize;x--,temp++){
			chat.get(x).reset();
		}
	}
	public boolean contains(int x,int y){
		if(x>x1 && x<x2 && y>y1 && y<y2){
			return true;
		}
		return false;
	}
}
