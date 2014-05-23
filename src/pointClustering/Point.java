package pointClustering;

import java.awt.Color;
import java.awt.Graphics;

public class Point {
	public static int RADIUS = 2;
	private int x;
	private int y;
	private Color cluster;
	
	public Point(int x, int y){
		this.x = x;
		this.y = y;
		this.cluster = Color.black;
	}
	
	public void setColor(Color cluster){
		this.cluster = cluster;
	}
	
	public Color getColor(){
		return this.cluster;
	}
	
	public int getX(){ return x; }
	public int getY(){ return y; }
	public void setX(int x){ this.x = x; }
	public void setY(int y){ this.y = y; }
	
	
	/**
	 * Draws point on graphics g at appropriate location with appropriate color.
	 * post: color of g is unchanged.
	 * 
	 * @param g
	 */
	public void draw(Graphics g){
		Color temp = g.getColor();
		g.setColor(cluster);
		g.drawOval(x - RADIUS, y - RADIUS, 2*RADIUS, 2*RADIUS);
		g.setColor(temp);
	}
	
	public void drawAsCenter(Graphics g){
		Color temp = g.getColor();
		g.setColor(cluster);
		g.drawString("X", x, y);
		g.setColor(temp);
	}

	/**
	 * 
	 * @param p
	 * @return euclidian distance between this point and p (as a double)
	 */
	public double distance(Point p) {
		return Math.sqrt((this.x - p.x)*(this.x - p.x) + (this.y - p.y)*(this.y - p.y));
	}

}
