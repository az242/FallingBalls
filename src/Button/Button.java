package Button;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class Button {
	//Coded with Custom formatting abilities. ~ is escape character
	//Available formats is Bold, Italics, and UnderLined
	int x,y,width,height;
	String text;
	Color col;
	private Font font;
	private Font Plain;
	private Font Bold;
	private Font Italics;
	java.awt.geom.Rectangle2D rect;
	public Button(int x,int y, int width, int height,String text){
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
		this.text=text;
		col = Color.BLACK;
		font = new Font("Arial", Font.PLAIN, 12);
		Plain = new Font("Arial", Font.PLAIN, 12);
		Bold = new Font("Arial", Font.BOLD, 12);
		Italics = new Font("Arial", Font.ITALIC, 12);
	}
	public boolean contains(int x, int y){
		if(x>this.x && x<this.x+this.width && y>this.y && y<this.y+this.height){
			return true;
		}
		return false;
	}
	public String getUnformatedText(){
		return text;
	}
	public String getText(){
		return getFormattedText(text);
	}
	public String getFormattedText(String text){
		//returns string without formating chars
		String formated="";
		for(int x=0;x<text.length();x++){
			if(text.charAt(x)=='~'){
				if(text.charAt(x+1)=='U'){
					x++;
				}else if(text.charAt(x+1)=='B'){
					x++;
				}else if(text.charAt(x+1)=='I'){
					x++;
				}else if(text.charAt(x+1)=='P'){
					x++;
				}
			}else{
				formated = formated + text.charAt(x);
			}
		}
		return formated;
	}
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	public int getWidth(){
		return width;
	}
	public int getHeight(){
		return height;
	}
	public void draw(Graphics g){
		g.setColor(col);
		g.drawRect(x, y, width, height);
		FontMetrics plain   = g.getFontMetrics(Plain);
		FontMetrics b   = g.getFontMetrics(Bold);
		FontMetrics it   = g.getFontMetrics(Italics);
		java.awt.geom.Rectangle2D rect = plain.getStringBounds(getFormattedText(text), g);
		boolean underlined=false;
		for(int x=0,w=(int)(this.x+((width-rect.getWidth())/2));x<text.length();x++){
			rect = plain.getStringBounds(text.charAt(x)+"", g);
			if(text.charAt(x)=='~'){
				//detect escape char
				if(text.charAt(x+1)=='U'){//if Underlined
					rect = plain.getStringBounds(text.charAt(x)+"", g);
					g.setFont(Plain);
					x++;
					underlined=true;
				}else if(text.charAt(x+1)=='B'){//if Bold
					rect = b.getStringBounds(text.charAt(x)+"", g);
					g.setFont(Bold);
					x++;
					underlined=false;
				}else if(text.charAt(x+1)=='I'){//if Italics
					rect = it.getStringBounds(text.charAt(x)+"", g);
					g.setFont(Italics);
					x++;
					underlined=false;
				}else if(text.charAt(x+1)=='P'){//if plaintext
					rect = plain.getStringBounds(text.charAt(x)+"", g);
					g.setFont(Plain);
					x++;
					underlined=false;
				}
			}else{
				g.drawString(text.charAt(x)+"", w, (int)(y+((height+rect.getHeight())/2)));
				if(underlined){
					//draw underline char
					//System.out.println("w:"+w+", y:"+(int)(y+((height+rect.getHeight())/2)));
					g.drawLine(w, (int)(y+((height+rect.getHeight())/2)+1), (int)(w+rect.getWidth()-1), (int)(y+((height+rect.getHeight())/2)+1));
				}
				//increment w position by letter width
				w=(int) (w+rect.getWidth());
			}
			//g.drawString(text, (int)(x+((width-rect.getWidth())/2)), (int)(y+((height+rect.getHeight())/2)));
		}
		g.setFont(font);
		//reset font to default
	}
}
