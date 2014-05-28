package pointClustering;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class PointClusterMain {
	public static int[][] POINT_CENTERS = {{100,100}, {300, 190}, {100,300}, {200, 200}};
	public static Color[] CLUSTER_COLORS = {Color.BLUE, Color.RED, Color.GREEN, Color.PINK, Color.YELLOW};
	public static int LOCATION_DEVIATION = 80;
	
	public static boolean TRAILS = false;
	public static int POINT_COUNT = 100;
	public static int FRAME_TIME = 2000;
	
	
	public static void main(String[] args) {
		Collection<Point> points = generatePoints();
		
		DrawingPanel panel = new DrawingPanel(400, 400);
		panel.setBackground(Color.LIGHT_GRAY);
		Graphics g = panel.getGraphics();
		g.setColor(Color.LIGHT_GRAY);
		
		for(Point p : points){
			p.draw(g);
		}
		
		boolean converged = false;
		Collection<Point> centers = initializeCenters(POINT_CENTERS.length);
		for(Point c : centers){
			c.drawAsCenter(g);
		}
		
		List<Collection<Point>> prevCenters = new ArrayList<Collection<Point>>();
		prevCenters.add(centers);
		do {

			assignPoints(centers, points);
			drawAll(g, centers, points, prevCenters);
			
			centers = relocate(centers, points);
			prevCenters.add(centers);
			drawAll(g, centers, points, prevCenters);
			
		} while (!converged);
		
	}
	
	/**
	 * Update centers to be at the central point of the points assigned to their cluster.
	 * @param centers
	 * @param points
	 */
	private static Collection<Point> relocate(Collection<Point> centers,
			Collection<Point> points) {
		Collection<Point> result = new HashSet<Point>();
		for(Point c : centers){
			int sumX = 0;
			int sumY = 0;
			int count = 0;
			for(Point p : points){
				if(p.getColor() == c.getColor()){
					sumX += p.getX();
					sumY += p.getY();
					count ++;
				}
			}
			Point newCenter = new Point(0,0);
			newCenter.setY((int)Math.round(sumY * 1.0 / count));
			newCenter.setX((int)Math.round(sumX * 1.0 / count));
			newCenter.setColor(c.getColor());
			result.add(newCenter);
		}	
		return result;
	}

	/**
	 * 
	 * 
	 * @param g
	 * @param centers
	 * @param points
	 */
	private static void drawAll(Graphics g, Collection<Point> centers,
			Collection<Point> points, List<Collection<Point>> prevCenters) {
		
		try {
		    Thread.sleep(FRAME_TIME);
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
		g.clearRect(0,0,400,400);
		g.fillRect(0, 0, 400, 400);
		
		for(Point p : points){
			p.draw(g);
		}
		
		if(TRAILS) {
			Collection<Point> prev = prevCenters.get(0);
			for(int i = 1; i < prevCenters.size(); i++){
				Collection<Point> current = prevCenters.get(i);
				for(Point p : current){
					Point prior = getOfMatchingColor(p, prev);
					g.setColor(p.getColor());
					g.drawLine(prior.getX(), prior.getY(), p.getX(), p.getY());
					
					Color temp = prior.getColor();
					prior.setColor(Color.GRAY);
					prior.drawAsCenter(g);
					prior.setColor(temp);
				}
				prev = current;
			}
			for(Point p : prev){
				p.drawAsCenter(g);
			}
			g.setColor(Color.LIGHT_GRAY);
			
			
		} else {
			
			for(Point c : centers){
				c.drawAsCenter(g);
			}
		}
			
		
	}
	
	private static Point getOfMatchingColor(Point p, Collection<Point> col){
		for (Point c : col){
			if(p.getColor().equals(c.getColor())){
				return c;
			}
		}
		return null;
	}
	

	/**
	 * Assign each point to be part of the cluster whose center is nearest!
	 * post: centers is unmodified
	 * 
	 * @param centers
	 * @param points
	 */
	private static void assignPoints(Collection<Point> centers,
			Collection<Point> points) {
		for (Point p : points) {
			Point center = getNearest(centers, p);
			p.setColor(center.getColor());
		}
	}
	
	/**
	 * private helper to find nearest point in centers to  p
	 * 
	 * @param centers
	 * @param p
	 * @return
	 */
	private static Point getNearest(Collection<Point> centers, Point p) {
		double min = 0;
		Point minPoint = null;
		for(Point c : centers){
			if(minPoint == null || c.distance(p) < min){
				min = c.distance(p);
				minPoint = c;
			}
		}
		return minPoint;
	}

	/**
	 * return a set of randomly located points. Uniformly distributed over the
	 * 400 by 400 square with top left at (0,0)
	 * 
	 * @param count 	- how may random points to create
	 * @return a list of size count, filled with randomly located points.
	 */
	private static Collection<Point> initializeCenters(int count) {
		if(count > CLUSTER_COLORS.length){
			throw new IllegalStateException("not enough colors...");
		}
		
		Collection<Point> result = new HashSet<Point>();
		Random r = new Random();
		
		for(int i = 0; i < count; i++){
			Point p = new Point(r.nextInt(400), r.nextInt(400));
			p.setColor(CLUSTER_COLORS[i]);
			result.add(p);
		}
		return result;
	}


	private static <E> E getRandomElement(Collection<E> col){
		Random r = new Random();
		int index = r.nextInt(col.size());
		for(E el : col){
			if(index == 0){
				return el;
			}
			index--;
		}
		return null;
	}
	
	private static Collection<Point> generatePoints() {
		Collection<Point> points = new HashSet<Point>();
		Random r = new Random();
		for(int[] center : POINT_CENTERS){
			for(int i = 0; i < POINT_COUNT / POINT_CENTERS.length; i++){
				points.add(new Point((center[0] + LOCATION_DEVIATION / 2 - r.nextInt(LOCATION_DEVIATION)) % 400,
									 (center[1] + LOCATION_DEVIATION / 2 - r.nextInt(LOCATION_DEVIATION)) % 400));
			}
		}
		return points;
	}

}
