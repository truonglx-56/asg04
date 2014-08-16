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
import java.awt.image.*;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

//Audio
import java.net.*;
import java.applet.AudioClip;

//for the Vector class
import java.util.*;

public class obstacle extends Tanks implements Runnable, KeyListener, MouseListener {

	int delay, counter;
        Thread animator;
	Image Buffer;
	Graphics gBuffer;
	Intro intro;
	Tank tank[];
	Shell myShell;	  
	
	Vector v;	//our vector variable, can store unlimited number of shells
	int index;	//Our vector index
	
    //how many tanks?
    static final int MAX=10;
	
	//Our game bearings (directions)
	static final int NORTH = 1;
	static final int EAST = 2;
	static final int SOUTH = 3;
	static final int WEST = 4;
	
	boolean gameOver, animateField, playerWon, startNew=false;
	int shieldsLeft, gameLevel, playerPoints, tanksLeft;
	Image img1, img2;
	String player1;
	Point target;
	Color bgColor = new Color(170,191,169);
	Rectangle rect1, rect2;
	
	// Sounds
	AudioClip	fire, crash, explosion, gong;

	public void init() {
		img1 = getImage(getDocumentBase(),"images/welcome1.gif");
		img2 = getImage(getDocumentBase(),"images/welcome2.gif");
		
		loadSounds();
		
		String str = getParameter("playerName");
		player1 = (str != null) ? str : "Player 1";
		
		delay = 100;
		gameOver = true;
		animateField = false;
		playerWon = false;
		
		Buffer = createImage(getSize().width,getSize().height);
		gBuffer = Buffer.getGraphics();
		
		intro = new Intro();
		intro.img1 = img1;
		intro.img2 = img2;
		
		tank = new Tank[MAX];
		initTanks();
		
		v = new Vector();
		
		this.setBackground(bgColor);
		this.addMouseListener(this);
		this.addKeyListener(this);
    }
	
	public void start() {animator = new Thread(this); animator.start();}

	public void run() {
		while (Thread.currentThread() == animator) {
		    repaint();
			simulateTanks();
			
			try { Thread.sleep(delay); } 
			catch (InterruptedException e) { break; }
		}
    }

    public void stop() { animator = null; }
	
	public void update(Graphics g) { paint(g); }
	
	public void paint(Graphics g)
	{
		gBuffer.clearRect(0,0,400,360);		
		drawBg();
		
		if (!intro.opened) { intro.paint(gBuffer); if(intro.opening) {intro.move();} }
		
		paintTanks(); //paint all tanks	
		paintShells(); //paint all shells	
		
		g.drawImage(Buffer,0,0,this); 	
		
		//showStatus("Game Level: " + new Integer(gameLevel).toString() + "  Tanks Left: " + new Integer(tanksLeft).toString());
	}

	public void mouseClicked(MouseEvent e){} 
	public void mouseEntered(MouseEvent e){} 
	public void mouseExited(MouseEvent e){}
	public void mousePressed(MouseEvent e){} 
	public void mouseReleased(MouseEvent e){
		Rectangle r1 = new Rectangle(100,90,200,180);
		if(gameOver && gameLevel==0){
			if(r1.contains(e.getX(), e.getY())){ 
				if(!intro.opened) {
					intro.opening=true; 
					gong.play();
				}  
				else {newGame();}
			}
		}
	} 

	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch (keyCode) {
			case 38: tank[0].turn(1);	break; 
            case 39: tank[0].turn(2);	break;
            case 40: tank[0].turn(3);	break;
            case 37: tank[0].turn(4);	break;
            case 65: tank[0].isMoving = true;	break;
            case 32: fireShell(0);	break;
        }   
    }
	
    public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch (keyCode) {
          case 65: tank[0].isMoving = false;	break;
        }   
    }
	
    public void keyTyped(KeyEvent e) { }
			
	
	void drawBg(){
		
		gBuffer.setColor(bgColor);
		gBuffer.fillRect(0,0,400,360);
		gBuffer.setColor(Color.black);
		gBuffer.drawRect(2,2,394,354);		
		
		gBuffer.setColor(Color.gray);
		gBuffer.setFont(new Font("Arial", Font.BOLD, 150));
		gBuffer.drawString(new Integer(gameLevel).toString() , 150, 220); 		

		if(gameLevel==3 && !gameOver )animateField=true;
		
		if (animateField){
			float h, s, b;
			int i;
			for (i=0; i<400; i++){
				h = (float)Math.random(); s = (float)Math.random(); b = (float)Math.random();
				gBuffer.setColor(Color.getHSBColor(h, s, b));
				gBuffer.drawLine(i,0,i,360);
				i+=20;
			}	
			for (i=0; i<360; i++){
				h = (float)Math.random(); s = (float)Math.random(); b = (float)Math.random();
				gBuffer.setColor(Color.getHSBColor(h, s, b));
				gBuffer.drawLine(0,i,400,i);
				i+=20;
			}	
		}

		if(gameOver){
			if(gameLevel!=0)endGame(!playerWon);
			else {
				gBuffer.setFont(new Font("Arial", Font.BOLD, 18));
				gBuffer.setColor(Color.blue);
				gBuffer.drawString("Start!",185,175); 
				gBuffer.drawRect(170,150,80,40);
			}
			
			gBuffer.setColor(Color.red);
			gBuffer.setFont(new Font("Arial", Font.BOLD, 16));
			gBuffer.drawString("1. Key \"A\" to move tank",10,20); 			
			gBuffer.drawString("2. Key \"Space\" to fire shells",10,40); 			
			gBuffer.drawString("3. Arraow keys to change direction",10,60); 			
			gBuffer.drawString("4. Player's tank is green",10,80); 			
			
		}
		else{
			if(startNew)newGame();
		}
	}
	
	void newGame(){
		inactivateTanks(2);
		gameLevel = 0;
		playerPoints = 0;
		gameOver = false;
		animateField=false;		
		nextLevel();
	}
	
	void endGame(boolean pcWon){
		gameOver = true;
		inactivateTanks(1);
		gBuffer.setColor(Color.yellow);
		gBuffer.setFont(new Font("Arial", Font.BOLD, 20));
		gBuffer.drawString("Game Over: ",115,140); 		
		if(pcWon){gBuffer.drawString("You Lost",240,140); }
		else {gBuffer.drawString("You Won",240,140); }
		gBuffer.setColor(Color.orange);
		gBuffer.drawString(player1 +"'s Points: " + new Integer(playerPoints).toString() ,150,240);		
	}
	
	void endLevel(){				
		if(gameLevel>2){ 
			gameOver = true;
			playerWon = true;
			startNew=true;
		}
		else {nextLevel();}
	}
	
	void nextLevel(){		
		gameLevel++;
		shieldsLeft = 3; 
		tanksLeft = 3;
		gBuffer.setColor(Color.yellow);
		counter = 1000;
		initGameLevel();
	}
	
	void playerHit(){
		shieldsLeft -= 1; 
		if(shieldsLeft<0){
			tank[0].explode();
			gameOver = true;
			playerWon = false;
			startNew=true;
			inactivateTanks(0);
		}
	}
	void enemyHit(int i){
		tanksLeft -= 1; 
		playerPoints += tank[i].moveBy;
		tank[i].explode();
		if(tanksLeft<1)endLevel();		
	}
	
	void initGameLevel(){
		int i;
		switch (gameLevel) {
			case 1: for(i=0;i<4;i++){tank[i].isAlive = true;tank[i].isMoving = true;} break; 
            case 2: for(i=4;i<7;i++){tank[i].isAlive = true;tank[i].isMoving = true;} break; 
            case 3: for(i=7;i<10;i++){tank[i].isAlive = true;tank[i].isMoving = true;} break; 
        }  		
		tank[0].isAlive = true; //Keep player alive incase they start at a higher level
		tank[0].isMoving = false;
	}
	
	void initTanks(){
		int x[] = {340,320,10,80,10,340,320,10,80,10};
		int y[] = {300,10,10,120,310,300,10,10,120,310};
		int d[] = {1,4,3,2,1,1,4,3,2,1};
		int s[] = {5,5,5,5,10,8,8,10,10,20};
		int mf[] = {1,1,1,1,2,2,2,3,3,4};
		Color ct = new Color(139,0,0);
		Color c[] = {new Color(0,113,0),ct,ct,ct,Color.red,Color.orange,Color.orange,Color.red, Color.red, Color.black};
		for(int i=0;i<MAX;i++){			
			tank[i] = new Tank(x[i],y[i],c[i],s[i],d[i],mf[i]);
		}
	}
	
	void paintTanks(){			
		for(int i=0;i<MAX;i++){
			if(tank[i].isAlive)tank[i].paint(gBuffer);
		}
	}
	
	void inactivateTanks(int n){			
		for(int i=0;i<MAX;i++){
			if(n==0)tank[i].isAlive=false;
			if(n==1)tank[i].isMoving=false;
			if(n==2)tank[i].exploded=false;
		}
	}
	
	void paintShells(){
		int prevIndex=-1, currIndex;
        for(int i=0;i<index;i++)
        {
            //the element stored in the vector at index position i
            //is casted to a Shell object and assigned to
            //the variable myShell, to access it's methods!
            myShell=(Shell)v.elementAt(i);

			if(myShell.isAlive){
				myShell.move();
				myShell.paint(gBuffer);
				for(int j=0;j<MAX;j++){
					if(tank[j].isAlive){
						rect1 = new Rectangle(tank[j].x,tank[j].y,35,35);
						if(rect1.contains(myShell.x,myShell.y)){
							tankHit(j);
							myShell.isAlive = false;
							explosion.play();
						}
					}
				}
			}
        }
	}
	
	void tankHit(int i){		
		if(i==0)playerHit();
		else enemyHit(i);		
	}
	
	void simulateTanks(){
		for(int i=0;i<MAX;i++){
			if(tank[i].isAlive){
				handleIntersections();
				simulateMovement(i);
				if(i!=0) simulateFire(i);
			}
		}
	}
	
	void handleIntersections()
	{
		//we iterate through all the tanks, checking if they meet at an intersection
		for(int i=0;i<MAX;i++)
			for(int j=0;j<MAX;j++)
				{
					if( tank[i].isAlive && tank[j].isAlive && (i!=j))
					{								
						if( collide(tank[i], tank[j])  )
						{
							tank[i].turnAround();
							tank[j].turnAround();
							tank[i].move();
							tank[j].move();
						}
					}
				}
	}
	
	boolean collide(Tank t1, Tank t2)
	{
		rect1 = new Rectangle(t1.x, t1.y, t1.tankWidth(), t1.tankHeight()); 
		rect2 = new Rectangle(t2.x, t2.y, t2.tankWidth(), t2.tankHeight()); 
		return rect1.intersects(rect2);
	}
	
	void simulateMovement(int i){
		
		if(tank[i].isMoving){tank[i].move();}
		else{	
			if(i!=0){
				tank[i].turnAround();
				tank[i].isMoving = true;
			}
		}		
	}
	
	void simulateFire(int i){
		boolean canfire = false;
		int cord;
		rect2 = new Rectangle(tank[0].x,tank[0].y,tank[0].tankWidth(),tank[0].tankHeight());
		
		if(tank[i].direction==1 || tank[i].direction==3 ){
			cord = tank[i].x+((int)tank[i].tankWidth()/2)-2;
			rect1 = new Rectangle(cord,0,3,360);
		}else {
			cord = tank[i].y+((int)tank[i].tankHeight()/2)-2;
			rect1 = new Rectangle(0,cord,400,3);
		}
		
		if (rect1.intersects(rect2)){
			if(gameLevel>1 )simulateDirection(i);
			canfire = true;
		}
		
		if(canfire)fireShell(i); 
	}
	
	void simulateDirection(int i){
		
		if(tank[i].x > tank[0].x){
			if(tank[i].direction==EAST && tank[0].direction==EAST)tank[i].turnAround();
		}						
		if(tank[i].x < tank[0].x){
			if(tank[i].direction==WEST && tank[0].direction==WEST)tank[i].turnAround();
		}	
		if(tank[i].y > tank[0].y){
			if(tank[i].direction==SOUTH && tank[0].direction==SOUTH)tank[i].turnAround();
		}						
		if(tank[i].y < tank[0].y){
			if(tank[i].direction==NORTH && tank[0].direction==NORTH)tank[i].turnAround();
		}	
	}
	
	void fireShell(int i)
    {
		int cnt=0;		
		for(int n=0;n<index;n++) {
            myShell=(Shell)v.elementAt(n);
			if(myShell.tankIndex==i && myShell.isAlive) cnt++;
			if(cnt==tank[i].maxFire) return;
		}
		 
		int dir = tank[i].direction;
        myShell = new Shell();
        myShell.direction = dir;
		myShell.tankIndex = i;

		switch (dir) {
            case NORTH: myShell.w=3; myShell.h=10; myShell.x=tank[i].x+17; myShell.y=tank[i].y-10; break;
            case EAST:	myShell.w=10; myShell.h=3; myShell.x=tank[i].x+40; myShell.y=tank[i].y+17; break;
            case SOUTH: myShell.w=3; myShell.h=10; myShell.x=tank[i].x+17; myShell.y=tank[i].y+40; break;
            case WEST:  myShell.w=10; myShell.h=3; myShell.x=tank[i].x-10; myShell.y=tank[i].y+17; break;
        }    
		fire.play();
		
        //we add a new element to the vector
        v.addElement(myShell);

        //our index number is incremented, to keep track of the number
        //of created shells
        index++;
    }
}









