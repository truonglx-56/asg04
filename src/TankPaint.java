

/*
 * Programmer(s): Talon; Colton
 * Date: Nov 10, 2008
 * Project: Networked
 * Package: good
 * FileName:Player.java
 * Description:
 */

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

//Audio
import java.net.*;
import java.applet.AudioClip;

//for the Vector class
import java.util.*;

import java.awt.image.BufferedImage;
import javax.swing.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;

public class TankPaint extends JPanel implements MouseListener, ActionListener, MouseMotionListener
{
	static BufferedImage	background;
	private BufferedImage	top, base, tread;
	Image	              explode	 = Toolkit.getDefaultToolkit().getImage(Option.imageDir + Option.FILE_SEPARATOR + "ex1.gif");
	private Graphics	  g;

	static int	          backgroundX, backgroundY;
	static double	      baseAngle, topAngle;
	int	                  treadNum	 = 1;

	boolean	              explosion	 = false;
	int	                  eX	     = 0;	                                                    // explosion coords
	int	                  eY	     = 0;
	private int	          beginX;
	private int	          beginY;

	// ///
	Player	              player;

	Timer	              t;
	public int	          goalX;
	public int	          goalY;
	int	                  tempXstart	= 0;
	int	                  tempYstart	= 0;
	private Point	      cursorLoc	 = new Point(-20, -20);	                                // local variable for being able to get cursor location anywhere

	// ///

	public TankPaint(String back, String top, String base, String tread)
	{
		super();

		try
		{
			String backgroundPath = Option.backgroundDir + Option.FILE_SEPARATOR;
			String imagePath = Option.imageDir + Option.FILE_SEPARATOR;
			this.background = ImageIO.read(new File(backgroundPath + back));
			this.top = ImageIO.read(new File(imagePath + top));
			this.base = ImageIO.read(new File(imagePath + base));
			this.tread = ImageIO.read(new File(imagePath + tread));
		}
		catch (IOException ex){
			System.out.println("Error as loading images!" + ex);
		}
		this.backgroundX = 0;
		this.backgroundY = 0;
		this.baseAngle = 0;
		this.topAngle = 0;
		addMouseListener(this);
		// ////
		t = new Timer(20, this);
		loadBeginPoints();
		player = new Player(beginX, beginY, this.background.getWidth(), this.background.getHeight());
		player.setSpeedModifier(5);

		addMouseMotionListener(this);
		// ////
	}

	private void loadBeginPoints()
	{
		// 181, 165, 213 is rgb of beginning location

		for (int i = 0; i < background.getWidth(); i++)
		{
			for (int j = 0; j < background.getHeight(); j++)
			{
				try
				{
					int aux = TankPaint.background.getRGB(i, j);

					int red = ((aux & 0x00FF0000) >>> 16); // Red level
					int green = ((aux & 0x0000FF00) >>> 8); // Green level
					int blue = (aux & 0x000000FF); // Blue level

					if (red == 181 && green == 165 && blue == 213)
					{
						beginX = i;
						beginY = j;
					}
				}
				catch (Exception pedophile)
				{}
			}

		}

	}

	public void paintComponent(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform transform = new AffineTransform();

		g.drawImage(background, backgroundX, backgroundY, this);

		transform.setToTranslation(player.x - tread.getWidth() / 2 + 15, player.y - 5 - tread.getHeight() / 2 + 25);
		transform.rotate(Math.toRadians(baseAngle), tread.getWidth() / 2, tread.getHeight() / 2 - 10);
		g2d.drawImage(tread, transform, this);
		g2d.drawImage(base, transform, this);

		transform.setToTranslation(player.x - tread.getWidth() / 2 + 15, player.y - tread.getHeight() / 2 + 21);
		transform.rotate(Math.toRadians(topAngle), top.getWidth() / 2, top.getHeight() / 2 - 25);
		g2d.drawImage(top, transform, this);
		g.drawLine(player.x, player.y, goalX, goalY);
		if (explosion)
		{
			g.drawImage(explode, eX, eY, this);
		}

		// debugging: area where explosion may occur
		int tempDist = (int) (new Point(player.x, player.y).distance(cursorLoc));
		int radius = (int) (.0007628459470397 * Math.pow(tempDist, 2) - .09954558648855 * tempDist + 120.939080166846);
		g.drawOval((int) (cursorLoc.x - (.43 * radius)), (int) (cursorLoc.y - (.43 * radius)), radius, radius);

		// </debugging>
	}

	private void move()
	{
		player.moveToPoint(new Point(tempXstart, tempYstart), new Point(goalX, goalY));
		if (player.reachedGoal)
		{
			t.stop();
		}
		try
		{
			treadNum++;
			if (treadNum == 4)
				treadNum = 1;
			this.tread = ImageIO.read(new File(Option.imageDir + Option.FILE_SEPARATOR + "tread " + treadNum + ".gif"));
		}
		catch (IOException ex)
		{
			System.out.println("Error loading images: " + ex.getMessage());
		}

		repaint();
	}

	public void start()
	{
		t.start();
	}

	public void actionPerformed(ActionEvent arg0)
	{
		// System.out.println("Got into actionperformed");
		move();
		repaint();
	}

	public void stop()
	{
		t.stop();
	}

	public void mouseClicked(MouseEvent m)
	{

	}

	public void mouseEntered(MouseEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent m)
	{
		if (m.getButton() == m.BUTTON3)
		{
			goalX = m.getX();
			goalY = m.getY();
			tempXstart = player.x;
			tempYstart = player.y;
			Point temp1 = new Point(player.x, player.y);
			Point temp2 = new Point(goalX, goalY);
			if (temp1.distance(temp2) > player.speedMod * 2)
			{
				goalX = m.getX();
				goalY = m.getY();
				t.start();
			}
			else
			{
				goalX = player.x;
				goalY = player.y;
			}
			t.start();
			// System.out.println("Timer started? " + t.isRunning());
		}
		else if (m.getButton() == m.BUTTON2)
		{
			t.stop();
			if (explosion)
			{
				explosion = false;

			}
		}

		else if (m.getButton() == m.BUTTON1)
		{

			explosion = true;
			// where explosion x and y get set
			Point explodePoint = getExplosionPoint(new Point(m.getX(), m.getY()));
			eX = (int) explodePoint.getX();// m.getX() - 20;
			eY = (int) explodePoint.getY();// m.getY() - 20;
			repaint();

		}

	}

	private Point getExplosionPoint(Point center)
	{
		Point finalSelection = null;// new Point();
		int centerX = (int) center.getX();
		int centerY = (int) center.getY();
		Random rand = new Random();
		int tempDist = (int) (new Point(player.x, player.y).distance(cursorLoc));
		int radius = (int) (.0007628459470397 * Math.pow(tempDist, 2) - .09954558648855 * tempDist + 120.939080166846);
		int randAngle = rand.nextInt(360); // between 0 and 360 (degrees)
		int randAngle2 = rand.nextInt(360);
		int finalX = (int) (centerX + radius * Math.cos(randAngle));
		int finalY = (int) (centerY + radius * Math.sin(randAngle2));
		 if( finalX > centerX)
		 {
		 finalX = centerX - (randAngle % radius);
		 }
		 else
		 {
		 finalX = centerX + (randAngle % radius);
		 }
				
		 if( finalY > centerY)
		 {
		 finalY = centerY - (randAngle2 % radius);
		 }
		 else
		 {
		 finalY = centerY + (randAngle2 % radius);
		 }
		finalSelection = new Point(finalX, finalY);
		return finalSelection;
		
		//TODO: Probably trash this idea and replace it with a random angle, then go out a random amount (between 0 and radius), and make that
		//the point of explosion, just got to do some more trig, fun :P The Game!
	}

	public void mouseReleased(MouseEvent m)
	{

	}

	// /////

	public void mouseMoved(MouseEvent e)
	{

		double x1 = e.getX();
		double y1 = e.getY();
		cursorLoc = new Point(e.getX(), e.getY());
		x1 = x1 - player.x;
		y1 = y1 - player.y;

		if (x1 >= 0)
		{
			topAngle = Math.toDegrees(Math.atan(y1 / x1)) - 90;
		}
		else
		{
			topAngle = Math.toDegrees(Math.atan(y1 / x1)) + 90; // tan^-1(slopeAsRatio)
		}
		repaint();

	}

	public void mouseDragged(MouseEvent e)
	{
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
