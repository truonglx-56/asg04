/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author TruongLX
 */
import java.awt.*;
import java.applet.*;

public class Shell extends Applet        
{
	public int direction, x, y, w, h, speed;
	public int tankIndex, maxX=400,maxY=360,minX=0,minY=0;
	boolean moving, isAlive;
    

	public Shell(){
		this.direction = 2;
		this.x = 0;
		this.y = 0;		
		this.w = 3;
		this.h = 3;
		this.speed = 20;
		this.isAlive = true;
	}

	public Shell(int direction, int xpos, int ypos){
		this.direction = direction;
		this.x = xpos;
		this.y = ypos;		
		this.w = 3;
		this.h = 3;
		this.speed = 5;
	}

	public Shell(int direction, int xpos, int ypos, int width, int height, int speed)
	{
		this.direction = direction;
		this.x = xpos;
		this.y = ypos;		
		this.w = width;
		this.h = height;
		this.speed = speed;
	}

	public void paint(Graphics g){
		g.setColor(Color.yellow);
		g.fillOval(x,y,w,h);
	}
	
	public void move(){
		
		//Has the Shell travelled beyond the game area ? then this shell is not alive;
		if( (x+w)>maxX || (x<minX) ||(y+h)>maxY || (y<minY) ) isAlive=false; 

		switch (direction) {
            case 1: y -= speed; break;
            case 2:	x += speed; break;
            case 3: y += speed; break;
            case 4: x -= speed; break;
        }		
	}	
		
}
