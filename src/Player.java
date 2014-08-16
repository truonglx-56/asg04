

/*
 * Programmer(s): Talon; Colton
 * Date: Nov 10, 2008
 * Project: Networked
 * Package: good
 * FileName:Player.java
 * Description:
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class Player
{
	public int	   x	        = 200;
	public int	   y	        = 200;
	public int	   width	    = 30;	 // default value
	public int	   height	    = 30;	 // default value
	public boolean	reachedGoal	= false;
	private int	   speed	    = 1;
	public int	   speedMod	    = 5;
	private Point	self;
	private int	   xRestriction;
	private int	   yRestriction;

	/**
	 * @param xRes
	 *            the X restriction, usually a getWidth()
	 * @param yRes
	 *            the Y restriction, usually a getHeight()
	 */
	public Player(int x, int y, int xRes, int yRes)
	{
		this.x = x;
		this.y = y;
		self = new Point(x, y);
		xRestriction = xRes;
		yRestriction = yRes;

	}

	public void moveToPoint(Point a, Point b)
	{
		reachedGoal = false;
		self = a;// new Point(x, y);
		double distance = a.distance(b);
		double steps = Math.round(distance / (speedMod * speed));
		double deltaY = Math.round((b.getY() - a.getY()) / steps);
		double deltaX = Math.round((b.getX() - a.getX()) / steps);
		int nextX = this.x;
		int nextY = this.y;
		if (Double.isNaN(deltaX) || Double.isNaN(deltaY))
		{

		}
		else if (deltaX > xRestriction)
		{
			deltaX = 1;
		}
		else if (deltaY > yRestriction)
		{
			deltaY = 1;
		}
		if (checkMove((int) (this.x + deltaX), (int) (this.y + deltaY)))
		{
			nextX = ((int) (this.x + deltaX));
			nextY = ((int) (this.y + deltaY));

			if (deltaX >= 0)
				TankPaint.baseAngle = Math.toDegrees(Math.atan(deltaY / deltaX)) - 90;

			else
				TankPaint.baseAngle = Math.toDegrees(Math.atan(deltaY / deltaX)) + 90;

		}

		/*
		 * if(deltaX>0 && deltaY>0) TankPaint.deg1 = 0; if(deltaX<0 && deltaY>0) TankPaint.deg1 = 90; if(deltaX<0 && deltaY<0) TankPaint.deg1 = 180; if(deltaX>0 && deltaY<0) TankPaint.deg1 = 270;
		 */
		this.x = nextX;
		this.y = nextY;
		
		double distToGoal = new Point(this.x,this.y).distance(b);
		if ( distToGoal<= 80)
		{
			reachedGoal = true;
		}
		System.out.println(distToGoal); //debgugging
	}

	public boolean checkMove(int x, int y)
	{
		boolean wall = true;
		for (int xx = x - TankPaint.backgroundX - 20; xx < x - TankPaint.backgroundX + 50; xx++)
		{
			for (int yy = y - TankPaint.backgroundY - 20; yy < y - TankPaint.backgroundY + 60; yy++)
			{
				try
				{
					int aux = TankPaint.background.getRGB(xx, yy);

					int red = ((aux & 0x00FF0000) >>> 16); // Red level
					int green = ((aux & 0x0000FF00) >>> 8); // Green level
					int blue = (aux & 0x000000FF); // Blue level

					if (red <= 0 && green == 0 && blue == 0)
					{

						xx = x + 1000;
						yy = y + 1000;

						wall = false;

					}
				}
				catch (Exception rapist)
				{}
			}

		}
		return wall;
	}

	public void setSize(int width, int height)
	{
		this.width = width;
		this.height = height;
	}

	public void setSpeedModifier(int speedMod)
	{
		this.speedMod = speedMod;
	}

	public static void main(String[] args)
	{
		Tanks f = new Tanks();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(1280, 1025);
		f.setResizable(false);
		f.setTitle("Battle Tanks!");
		f.setLocation(0, 0);
		f.setVisible(true);
	}
}
