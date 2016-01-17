package DataTypes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

public class Ball {
	int x,y,dx,dy;
	int radius;
	Color col;
	String id;
	long creationTime;
	public Ball(String ID,int x, int y, int radius, Color color,int dy,int dx,long time){
		//x y r C dy dx
		this.x=x;
		this.y=y;
		this.dx=dx;
		this.dy=dy;
		this.radius=radius;
		this.col=color;
		this.id=ID;
		this.creationTime = time;
	}
	public long getTime(){
		return creationTime;
	}
	public void setTime(long time){
		creationTime = time;
	}
	public String getID(){
		return id;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getDx() {
		return dx;
	}
	public void setDx(int dx) {
		this.dx = dx;
	}
	public int getDy() {
		return dy;
	}
	public void setDy(int dy) {
		this.dy = dy;
	}
	public int getRadius() {
		return radius;
	}
	public void setRadius(int radius) {
		this.radius = radius;
	}
	public Color getCol() {
		return col;
	}
	public void setCol(Color col) {
		this.col = col;
	}
	public void update(double delta){
		x = x+(int)(delta*dx);
		y = y+(int)(delta*dy);
	}
	public boolean intersects(Rectangle rect){
		Point cTop=new Point(x,y-radius);
		Point cBot=new Point(x,y+radius);
		Point cLeft=new Point(x-radius,y);
		Point cRight=new Point(x+radius,y);
		Point ctopRight=new Point((int)(x+(Math.cos(45/180 * 3.14)*radius)), (int)(y+(Math.sin(45/180 * 3.14)*radius)));
		Point ctopLeft=new Point((int)(x-(Math.cos(45/180 * 3.14)*radius)), (int)(y+(Math.sin(45/180 * 3.14)*radius)));
		Point cbotRight=new Point((int)(x+(Math.cos(45/180 * 3.14)*radius)), (int)(y-(Math.sin(45/180 * 3.14)*radius)));
		Point cbotLeft=new Point((int)(x-(Math.cos(45/180 * 3.14)*radius)), (int)(y-(Math.sin(45/180 * 3.14)*radius)));
		Rectangle circle = new Rectangle(ctopLeft.x,ctopLeft.y,Math.abs(cbotRight.x-ctopLeft.x), Math.abs(cbotRight.y-ctopLeft.y));
		ArrayList<Point> points=new ArrayList<Point>();
		points.add(cTop);
		points.add(cBot);
		points.add(cLeft);
		points.add(cRight);
		points.add(ctopRight);
		points.add(ctopLeft);
		points.add(cbotRight);
		points.add(cbotLeft);
		points.add(new Point(x,y));
		for(int x=0;x<points.size();x++){
			if(rect.contains(points.get(x)) || rect.contains(circle)){
				return true;
			}
		}
		return false;
	}
	public void draw(Graphics g){
		g.setColor(col);
		g.fillOval(x-radius, y-radius, radius*2, radius*2);
	}
}
