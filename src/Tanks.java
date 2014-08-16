

/*
 * Programmer(s): Talon; Colton
 * Date: Nov 10, 2008
 * Project: Networked
 * Package: good
 * FileName:Player.java
 * Description:
 */

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class Tanks extends JFrame
{   
    final int NORTH = 1;
	final int EAST = 2;
	final int SOUTH = 3;
	final int WEST = 4;
	int direction, moveBy;
	Color color;
	Point target;
	public int x, y, w, h;
	public int maxX=400,maxY=360,minX=0,minY=0;
	public int maxFire;	// The maximum amount of shots allowed for the tank to fire at once
	public int maxShells;	// The max number of shells allowed to be fired
	public boolean isMoving, isAlive, exploded;
	Graphics g;

	String	  back, top, base, tread;

	TankPaint	mainPanel;

	public Tanks()
	{
		top = "top 1";
		base = "base 1";
		tread = "tread 1";
		back = "samplelevel.bmp";

		setBackground(Color.black);

		mainPanel = new TankPaint(back, top + ".gif", base + ".gif", tread + ".gif");

		Image pointer = Toolkit.getDefaultToolkit().getImage(Option.imageDir + Option.FILE_SEPARATOR + "pointer.gif");
		Cursor redicalCursor = Toolkit.getDefaultToolkit().createCustomCursor(pointer, new Point(0, 0), "redicalcursor");
		mainPanel.setCursor(redicalCursor);
		this.add(mainPanel);
	}
        public void move(){

		switch (direction) {
            case NORTH: if( (y-moveBy<minY) ){isMoving=false;} else { y -= moveBy;}break;
		case EAST:	if( (x+tankWidth()+moveBy)>maxX){isMoving=false;} else { x += moveBy;}break;
            case SOUTH: if( (y+tankHeight()+moveBy)>maxY ){isMoving=false;} else { y += moveBy;}break;
            case WEST:  if( (x-moveBy<minX) ){isMoving=false;} else { x -= moveBy;}break;
        }    
	}
        int tankWidth(){
		if(direction==1||direction==3)return 35;
		else return 40;
	}
	
	int tankHeight(){
		if(direction==2||direction==4)return 40;
		else return 35;
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