//****************************************************************************
// SketchBase.  
//****************************************************************************
// Comments : 
//   Subroutines to manage and draw points, lines an triangles
//
// History :
//   Aug 2014 Created by Jianming Zhang (jimmie33@gmail.com) based on code by
//   Stan Sclaroff (from CS480 '06 poly.c)

import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class SketchBase 
{
	public SketchBase()
	{
		// deliberately left blank
	}
	
	// draw a point
	public static void drawPoint(BufferedImage buff, Point2D p)
	{
		buff.setRGB(p.x, buff.getHeight()-p.y-1, p.c.getBRGUint8());
	}
	
	//////////////////////////////////////////////////
	//	Implement the following two functions
	//////////////////////////////////////////////////
	
	// draw a line segment

	public static void drawLine(BufferedImage buff, Point2D p1, Point2D p2){
		//drawTempLine(buff, p1, p2);
		HashMap<Integer, List<Point2D>> map = getLinePoints(p1, p2);
		drawPoints(map, buff);
	}

	private static HashMap<Integer,List<Point2D>> getLinePoints(Point2D p1, Point2D p2) {
		Point2D temp1 = new Point2D(p1);
		Point2D temp2 = new Point2D(p2);
		return getOneLineAllPoints(temp1, temp2);
	}

	private static HashMap<Integer,List<Point2D>> getOneLineAllPoints(Point2D p1, Point2D p2){
		HashMap<Integer, List<Point2D>> map = new HashMap<>();
		boolean slot = Math.abs(p2.y - p1.y) > Math.abs(p2.x - p1.x);
		swap(p1, p2);
		int slotx = Math.abs(p2.x - p1.x);
		int sloty = Math.abs(p2.y - p1.y);
		int error = - slotx;
		int y = p1.y;
		int ystep;
		if(p1.y < p2.y){
			ystep = 1;
		}else{
			ystep = -1;
		}
		float r =(p2.c.r - p1.c.r) / (p2.x - p1.x);
		float g =(p2.c.g - p1.c.g) / (p2.x - p1.x);
		float b =(p2.c.b - p1.c.b) / (p2.x - p1.x);
		for (int i =p1.x; i <= p2.x; i++){
			p1.c.r += r;
			p1.c.g += g;
			p1.c.b += b;
			Point2D point2D;
			if(slot){
				point2D = new Point2D(y, i, new ColorType(p1.c.r, p1.c.g, p1.c.b));
				if (!map.containsKey(i)) {
					map.put(i, new ArrayList<Point2D>());
				}
				map.get(i).add(point2D);
			}else{
				point2D = new Point2D(i, y, new ColorType(p1.c.r, p1.c.g, p1.c.b));
				if (!map.containsKey(y)) {
					map.put(y, new ArrayList<Point2D>());
				}
				map.get(y).add(point2D);
			}
			error += 2 * sloty;
			if(error > 0){
				y = y + ystep;
				error -= 2 * slotx;
			}
		}
		return map;
	}

	// draw a triangle

	public static void drawTriangle(BufferedImage buff, Point2D p1, Point2D p2, Point2D p3, boolean do_smooth){
		if (do_smooth){
			drawTriangleWithSmooth(buff, p1, p2, p3);
		}else{
			Point2D temp1 = new Point2D(p1);
			Point2D temp2 = new Point2D(p2);
			Point2D temp3 = new Point2D(p3);
			temp2.c = temp3.c =temp1.c;
			drawTriangleWithSmooth(buff, temp1, temp2, temp3);
		}
	}

	public static void drawTriangleWithSmooth(BufferedImage buff, Point2D p1, Point2D p2, Point2D p3)
	{
		// replace the following line with your implementation
		Point2D[] points = sortTrianglePoints(p1, p2, p3);

		HashMap<Integer, List<Point2D>> topleft = getLinePoints(points[0], points[1]);
		HashMap<Integer, List<Point2D>> topright = getLinePoints(points[0], points[2]);
		for (int i = points[0].y; i <= points[1].y; i++){
			Point2D leftPoint = topleft.get(i).get(0);
			Point2D rightPoint = topright.get(i).get(0);
			drawLine(buff, leftPoint, rightPoint);
		}

		HashMap<Integer, List<Point2D>> bottomleft = getLinePoints(points[1], points[2]);
		HashMap<Integer, List<Point2D>> bottomright = getLinePoints(points[0], points[2]);
		for (int i = points[1].y+1; i <= points[2].y; i++){
			Point2D leftPoint = bottomleft.get(i).get(0);
			Point2D rightPoint = bottomright.get(i).get(0);
			drawLine(buff, leftPoint, rightPoint);
		}

		drawPoints(topleft, buff);
		drawPoints(topright, buff);
		drawPoints(bottomleft, buff);
		drawPoints(bottomright, buff);
	}

	/////////////////////////////////////////////////
	// for texture mapping (Extra Credit for CS680)
	/////////////////////////////////////////////////
	public static void triangleTextureMap(BufferedImage buff, BufferedImage texture, Point2D p1, Point2D p2, Point2D p3)
	{
		// replace the following line with your implementation
		Point2D[] points = sortTrianglePoints(p1, p2, p3);
		HashMap<Integer, List<Point2D>> topleft = getLinePoints(points[0], points[1]);
		HashMap<Integer, List<Point2D>> topright = getLinePoints(points[0], points[2]);
		for (int i = points[0].y; i <= points[1].y; i++){
			Point2D leftPoint = topleft.get(i).get(0);
			Point2D rightPoint = topright.get(i).get(0);
			drawLineWithTexture(buff, leftPoint, rightPoint, texture);
		}
		HashMap<Integer, List<Point2D>> bottomleft = getLinePoints(points[1], points[2]);
		HashMap<Integer, List<Point2D>> bottomright = getLinePoints(points[0], points[2]);
		for (int i = points[1].y+1; i <= points[2].y; i++){
			Point2D leftPoint = bottomleft.get(i).get(0);
			Point2D rightPoint = bottomright.get(i).get(0);
			drawLineWithTexture(buff, leftPoint, rightPoint, texture);
		}
		drawPointsWithTexture(topleft, buff, texture);
		drawPointsWithTexture(topright, buff, texture);
		drawPointsWithTexture(bottomleft, buff, texture);
		drawPointsWithTexture(bottomright, buff, texture);
	}

	private static void drawLineWithTexture(BufferedImage buff, Point2D p1, Point2D p2, BufferedImage texture) {
		swap(p1, p2);
		int y = p1.y;
		for (int i =p1.x; i <= p2.x; i++){
			buff.setRGB(i, buff.getHeight()-y-1, texture.getRGB(i % texture.getWidth(), y % texture.getHeight()));
		}
	}

	private static void swap(Point2D p1, Point2D p2){
		boolean slot = Math.abs(p2.y - p1.y) > Math.abs(p2.x - p1.x);
		if(slot){
			//swap(p1.x, p1.y);
			p1.x = p1.x ^ p1.y;
			p1.y = p1.x ^ p1.y;
			p1.x = p1.x ^ p1.y;

			//swap(p2.x, p2.y);
			p2.x = p2.x ^ p2.y;
			p2.y = p2.x ^ p2.y;
			p2.x = p2.x ^ p2.y;

		}
		if(p1.x > p2.x){
			//swap(p1.x, p2.x);
			p1.x = p1.x ^ p2.x;
			p2.x = p1.x ^ p2.x;
			p1.x = p1.x ^ p2.x;

			//swap(p1.y, p2.y);
			p1.y = p1.y ^ p2.y;
			p2.y = p1.y ^ p2.y;
			p1.y = p1.y ^ p2.y;

			//swap(p1.c, p2.c);
			ColorType tempColor = p1.c;
			p1.c = p2.c;
			p2.c = tempColor;
			tempColor = null;
		}
	}

	private static Point2D[] sortTrianglePoints(Point2D p1, Point2D p2, Point2D p3){
		Point2D[] points = new Point2D[3];
		points[0] = new Point2D(p1);
		points[1] = new Point2D(p2);
		points[2] = new Point2D(p3);
		Arrays.sort(points, new Comparator<Point2D>() {
			@Override
			public int compare(Point2D o1, Point2D o2) {
				if(o1.y == o2.y){
					return o1.x - o2.x;
				}
				return o1.y - o2.y;
			}
		});
		return points;
	}

	private static void drawPoints(HashMap<Integer, List<Point2D>> points, BufferedImage buff){
		for (Integer key: points.keySet()){
			for (Point2D point2D: points.get(key)){
				drawPoint(buff, point2D);
			}
		}
	}

	private static void drawPointsWithTexture(HashMap<Integer, List<Point2D>> points, BufferedImage buff, BufferedImage texture){
		for (Integer key: points.keySet()){
			for (Point2D point2D: points.get(key)){
				buff.setRGB(point2D.x, buff.getHeight()- point2D.y-1, texture.getRGB(point2D.x % texture.getWidth(), point2D.y % texture.getHeight()));
			}
		}
	}

	public static void drawLineWithAntiAliased(BufferedImage buff, Point2D p1, Point2D p2){
		Point2D temp1 = new Point2D(p1);
		Point2D temp2 = new Point2D(p2);
		temp1.x *= 2;
		temp1.y *= 2;
		temp2.x *= 2;
		temp2.y *= 2;
		HashMap<Integer, List<Point2D>> map = getLinePoints(temp1, temp2);
		drawPointsWithAliased(map, buff);
	}

	private static void drawPointsWithAliased(HashMap<Integer, List<Point2D>> map, BufferedImage buff) {
		for (int i = 0; i < buff.getWidth()*2; i+=2){
			for (int j = 0 ; j < buff.getHeight()*2; j+=2){
				ColorType c = new ColorType(0,0,0);
				int count = 0;
				if (map.containsKey(j)) {
					List<Point2D> list = map.get(j);
					for (Point2D point2D : list){
						if (point2D.x == i){
							count ++;
							c.r += point2D.c.r;
							c.g += point2D.c.g;
							c.b += point2D.c.b;
						}
						if (point2D.x == i+1){
							count++;
							c.r += point2D.c.r;
							c.g += point2D.c.g;
							c.b += point2D.c.b;
						}
					}
				}
				if (map.containsKey(j+1)) {
					List<Point2D> list = map.get(j+1);
					for (Point2D point2D : list){
						if (point2D.x == i){
							count ++;
							c.r += point2D.c.r;
							c.g += point2D.c.g;
							c.b += point2D.c.b;
						}
						if (point2D.x == i+1){
							count++;
							c.r += point2D.c.r;
							c.g += point2D.c.g;
							c.b += point2D.c.b;
						}
					}
				}
				int x = i/2;
				int y = j/2;
				if (count != 0) {
					c.r /= count;
					c.g /= count;
					c.b /= count;
				}
				c.r /= 4;
				c.g /= 4;
				c.b /= 4;
				c.r *= count;
				c.g *= count;
				c.b *= count;
				drawPoint(buff, new Point2D(x, y, c));
			}
		}
	}

	public static void drawTempLine(BufferedImage buff, Point2D p1, Point2D p2)
	{
		// replace the following line with your implementation
		boolean slot = Math.abs(p2.y - p1.y) > Math.abs(p2.x - p1.x);
		swap(p1, p2);
		int slotx = Math.abs(p2.x - p1.x);
		int sloty = Math.abs(p2.y - p1.y);
		int error = - slotx;
		int y = p1.y;
		int ystep;
		if(p1.y < p2.y){
			ystep = 1;
		}else{
			ystep = -1;
		}
		float r =(p2.c.r - p1.c.r) / (p2.x - p1.x);
		float g =(p2.c.g - p1.c.g) / (p2.x - p1.x);
		float b =(p2.c.b - p1.c.b) / (p2.x - p1.x);
		for (int i =p1.x; i <= p2.x; i++){
			p1.c.r += r;
			p1.c.g += g;
			p1.c.b += b;
			if(slot){
				drawPoint(buff,new Point2D(y,i,new ColorType(p1.c.r,p1.c.g,p1.c.b)));
			}else{
				drawPoint(buff,new Point2D(i,y,new ColorType(p1.c.r,p1.c.g,p1.c.b)));
			}
			error += 2 * sloty;
			if(error > 0){
				y = y + ystep;
				error -= 2 * slotx;
			}
		}
	}

}
