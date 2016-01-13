package Chat;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;

public class ArrayChat {
	private int x1,x2;
	private int y1,y2;
	private ArrayList<Message> chat;
	private int maxsize;
	private Font font;
	public ArrayChat(int x, int y,int x2, int y2,int size){ // default font used
		chat = new ArrayList<Message>();
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
	public ArrayChat(int x, int y,int x2, int y2,int size,Font font){ //user prompted font used
		chat = new ArrayList<Message>();
		maxsize = size;
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
	public void addMessage(Message temp){
		chat.add(temp);
	}
	public void draw(Graphics g){
		g.drawRect(x1, y1, x2-x1, y2-y1);
		g.setFont(font);
		for(int offset = 0,chatEntry=chat.size()-1;chatEntry>=0 && offset<maxsize;chatEntry--,offset++){
			FontMetrics fm   = g.getFontMetrics(font);
			java.awt.geom.Rectangle2D rect = fm.getStringBounds(chat.get(chatEntry).getFormatedMessage(), g);
			if(rect.getWidth()>Math.abs(x1-x2)){
				//if size of message is too large
				String[] components = wrapAround(chat.get(chatEntry).getFormatedMessage(),rect);
				for(int y=components.length-1;y>0;y--){ //0 is first line, 1 is second, ect
					g.drawString(components[y], x1, (int) (y2-((offset)*rect.getHeight())));
					offset++;
				}
				offset--;
			}else{
				//draw message into chat box;
				Color temp=chat.get(chatEntry).userColor;
				g.setColor(new Color(temp.getRed(),temp.getGreen(),temp.getBlue(),chat.get(chatEntry).getAlpha()));
				java.awt.geom.Rectangle2D rect2 = fm.getStringBounds(chat.get(chatEntry).getUsername(), g);
				
				g.drawString(chat.get(chatEntry).getUsername(), x1, (int) (y2-((offset)*rect.getHeight())));
				g.setColor(new Color(0,0,0,chat.get(chatEntry).getAlpha()));
				g.drawString(chat.get(chatEntry).getFMessage(), (int) (x1+rect2.getWidth()), (int) (y2-((offset)*rect.getHeight())));
				//g.drawLine(x1, (int)(y2-((offset)*rect.getHeight())), (int) (x1+rect2.getWidth()),(int) (y2-((offset)*rect.getHeight())));
			}
		}
	}
	public String[] wrapAround(String message,java.awt.geom.Rectangle2D rect){
		int cutoff = (int) (rect.getWidth() - Math.abs(x1-x2));// find how much is outside chatbox
		cutoff = (int) (message.length()* (cutoff/rect.getWidth())); // get size of string for first line
		String[] components = {message.substring(0,cutoff),message.substring(cutoff)};
		return components;
	}
	public void reset(){
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
