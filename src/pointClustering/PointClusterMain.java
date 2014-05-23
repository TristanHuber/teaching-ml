package pointClustering;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

public class PointClusterMain {
	public static int[][] POINT_CENTERS = {{100,100}, {300, 190}, {100,300}};
	public static Color[] CLUSTER_COLORS = {Color.BLUE, Color.RED, Color.GREEN, Color.PINK, Color.YELLOW};
	public static int LOCATION_DEVIATION = 170;
	
	
	public static int POINT_COUNT = 50;
	public static int FRAME_TIME = 5000;
	
	
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
		Collection<Point> centers = initializeCenters(3);
		do {
			assignPoints(centers, points);
			drawAll(g, centers, points);

			relocate(centers, points);
			drawAll(g, centers, points);
			
		} while (!converged);
		
	}
	
	/**
	 * Update centers to be at the central point of the points assigned to their cluster.
	 * @param centers
	 * @param points
	 */
	private static void relocate(Collection<Point> centers,
			Collection<Point> points) {
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
			c.setY((int)Math.round(sumY * 1.0 / count));
			c.setX((int)Math.round(sumX * 1.0 / count));
		}	
	}

	/**
	 * 
	 * @param g
	 * @param centers
	 * @param points
	 */
	private static void drawAll(Graphics g, Collection<Point> centers,
			Collection<Point> points) {
		
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
		
		for(Point c : centers){
			c.drawAsCenter(g);
		}
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
