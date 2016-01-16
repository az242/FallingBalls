package Button;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class Button {
	int x,y,width,height;
	String text;
	Color col;
	private Font font;
	java.awt.geom.Rectangle2D rect;
	public Button(int x,int y, int width, int height,String text){
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
		this.text=text;
		col = Color.BLACK;
		font = new Font("Arial", Font.PLAIN, 12);
	}
	public boolean contains(int x, int y){
		if(x>this.x && x<this.x+this.width && y>this.y && y<this.y+this.height){
			return true;
		}
		return false;
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
		FontMetrics fm   = g.getFontMetrics(font);
		java.awt.geom.Rectangle2D rect = fm.getStringBounds(text, g);
		g.drawString(text, (int)(x+((width-rect.getWidth())/2)), (int)(y+((height-rect.getHeight())/2)));
	}
}
