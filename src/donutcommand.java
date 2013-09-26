import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.awt.Graphics2D;
//import javax.sound.sampled.AudioFormat;
import java.util.*;
import java.awt.geom.*;
import java.net.URL;
import javax.sound.sampled.*;

enum SoundEffect {
	   GREETING("donutcommand_audio/DonutCommand.wav"), // Greeting
	   FIRE_CANNON("donutcommand_audio/CannonShot.wav"),
	   ALIEN_BLEET("donutcommand_audio/AlienBleet.wav"),
	   BROWN_STATIC("donutcommand_audio/brownstatic.wav"),
	   BULLET_COUNT("donutcommand_audio/bulletcount.wav"),
	   CANNON_CLICK("donutcommand_audio/cannonclick.wav"),
	   COFFEE_POP("donutcommand_audio/CoffeePop.wav"),
	   COLLECTOR_EXPLODE("donutcommand_audio/CollectorExplode.wav"),
	   COLLECTOR_PLOP("donutcommand_audio/collectorplop.wav"),
	   ENEMY_MISSILE_EXPLODE("donutcommand_audio/enemyMissleExplosion.wav"),
	   EXTRA_GUY("donutcommand_audio/extraguy.wav"),
	   FUZZ_OUT("donutcommand_audio/FuzzOut.wav"),
	   MUNCH_MUNCH("donutcommand_audio/MunchMunch.wav");   
	   
	   // Nested class for specifying volume
	   public static enum Volume {
	      MUTE, LOW, MEDIUM, HIGH
	   }
	   
	   public static Volume volume = Volume.LOW;
	   
	   // Each sound effect has its own clip, loaded with its own sound file.
	   private Clip clip;
	   
	   // Constructor to construct each element of the enum with its own sound file.
	   SoundEffect(String soundFileName) {
		   
	      try {
	         // Use URL (instead of File) to read from disk and JAR.
	         URL url = this.getClass().getClassLoader().getResource(soundFileName);
	         // Set up an audio input stream piped from the sound file.
	         AudioInputStream audioInputStream = null;
	         if(null == url)
	         {
	        	 File f = new File(soundFileName);
	        	 audioInputStream = AudioSystem.getAudioInputStream(f); 
	         }
	         else
	         {
	        	 audioInputStream = AudioSystem.getAudioInputStream(url); 
	         }
	         // Get a clip resource.
	         clip = AudioSystem.getClip();
	         // Open audio clip and load samples from the audio input stream.
	         clip.open(audioInputStream);
	         
	         
	      } catch (UnsupportedAudioFileException e) {
	         e.printStackTrace();
	      } catch (IOException e) {
	         e.printStackTrace();
	      } catch (LineUnavailableException e) {
	         e.printStackTrace();
	      }
	      catch (Exception e)
	      {
	    	  e.printStackTrace();
	      }
	      
	        	 
	   }
	   
	   // Play or Re-play the sound effect from the beginning, by rewinding.
	   public void play() {
	      if (volume != Volume.MUTE) {
	         if (clip.isRunning())
	            clip.stop();   // Stop the player if it is still running
	         clip.setFramePosition(0); // rewind to the beginning
	         clip.start();     // Start playing
	      }
	   }
	   public void playIfNotPlaying()
	   {
		   if (volume != Volume.MUTE) {
		         if (clip.isRunning())
		            return;   // Stop the player if it is still running
		         clip.setFramePosition(0); // rewind to the beginning
		         clip.start();     // Start playing
		      }
	   }
	   
	   // Optional static method to pre-load all the sound files.
	   static void init() {
	      values(); // calls the constructor for all the elements
	   }
	}

final class Crumb
{
    public boolean active;
    boolean deactivatenext;
    int x,y,basey;
    boolean timeset;
    int time;
    double rx,ry;
    double cx,cy;

    Color p;

    public static boolean needcolors = true;
    public static Color colors[] = null;

    static Vector<Crumb> crumbs = null;

    public Crumb(int sx,int sy,double vx,double vy)
    {
	active = true;
	deactivatenext = false;
	timeset = false;
	init(sx,sy,vx,vy);
	cx = vx + Math.random()*5-2.5;
	cy = vy + Math.random()*5;
    }

    public static void makeColors()
    {
	colors = new Color[99];

	int i;
	for(i=0;i<colors.length;i++)
	    {
		switch (i%3)
		    {
		    case 0:
			colors[i] = new Color(255,
					      (int)(Math.random()*128)+128,
					      (int)(Math.random()*128)+128);
			break;
		    case 1:
			colors[i] = new Color((int)(Math.random()*128)+128,
					      255,
					      (int)(Math.random()*128)+128);
			break;
		    default :
			colors[i] = new Color((int)(Math.random()*128)+128,
					      (int)(Math.random()*128)+128,
					      255);
			break;
		    }
	    }
	needcolors = false;
    }

    public Crumb(int sx,int sy,double vx,
		 double vy,double speed,
		 int lives,int livel)
    {
	active = true;
	deactivatenext = false;
	timeset = true;
	time = lives + (int)(Math.random()*(livel + 1));
	init(sx,sy,vx,vy);
	cx = vx/speed*4 + Math.random()*5-2.5;
	cy = vy/speed*4 + Math.random()*5-2.0;
	
    }

    private void init(int sx,int sy,double vx,double vy)
    {
	if(needcolors)
	    {
		needcolors = false;
		makeColors();
	    }
	
	x = sx;
	y = sy;
	rx = sx;
	ry = sy;
	basey = sy; /*Must fall below to deactivate */

	int yy = (int)(Math.random()*colors.length);
	p = colors[yy];

	if(crumbs == null)
	    {
		crumbs = new Vector<Crumb>();
	    }
	crumbs.add((Crumb)this);
    }

    public void animate()
    {
	if(!active)
	    return;

	if(deactivatenext)
	    {
		active = false;
		return;
	    }

	rx = rx + cx;
	ry = ry + cy;
	if(!timeset) cy = cy + 1;

	x = (int)rx;
	y = (int)ry;

	if(timeset)
	    {
		time--;
		if(time<0)
		    active = false;
	    }
	else
	    {
		if(y > basey)
		    {
			deactivatenext = true;
		    }
	    }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static void animateAll()
    {
	int i;

	Vector tmp;
	if(crumbs == null)
	    return;

	tmp = new Vector();

	for(i=0;i<crumbs.size();i++)
	    {
		Crumb c = (Crumb)crumbs.elementAt(i);
		c.animate();
		if(c.active)
		    {
			tmp.add((Object)c);
		    }
	    }
	crumbs = tmp;
    }


    public void drawCrumb(Graphics g)
    {
	g.setColor(p);
	g.fillOval(x-1,y-1,3,3);
    }

    public static void drawAll(Graphics g)
    {
	if(crumbs == null)
	    return;

	int i;
	for(i=0;i<crumbs.size();i++)
	    {
		Crumb c = (Crumb)crumbs.elementAt(i);
		c.drawCrumb(g);
	    }
    }

}

final class Explosion
{

    double ex,ey,erad;

    @SuppressWarnings("rawtypes")
	static Vector explosion = null;

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Explosion(int x,int y,int dia)
    {

	ex = x;
	ey = y;
	erad = dia/2;

	if(explosion == null)
	    {
		explosion = new Vector();
	    }

	explosion.add((Object)this);

    }

    public static void nullifyVector()
    {
	explosion = null;
    }

    public boolean testHit(int x,int y)
    {
	double d = Math.sqrt((x - ex)*(x-ex)+(y - ey)*(y - ey));

	if(d<(erad + 1.0))
	    return true;

	return false;
    }

    public static boolean testAll(int x,int y)
    {
	int i;

	if(explosion == null)
	    return false;

	for(i=0;i<explosion.size();i++)
	    {
		Explosion e;
		e = (Explosion) explosion.elementAt(i);
		if(e != null)
		    {
			if(e.testHit(x,y))
			    return true;
		    }
	    }

	return false;
    }
}


final class donutAlien
{

    public boolean active=false;
    public boolean exploding=false;

    public int x,y;
    public int tx,ty,sx,sy;
    public static double speed=4;

    double rx,ry,cx,cy;
    double d;
    int cycle = 0;

    int exrad = 0;

    public donutAlien()
    {
	active = false;
	exploding = false;
	rx = 0.0;
	ry = 0.0;
	cx = 0.0;
	cy = 0.0;
	x = 0;
	y = 0;
	tx = 0;
	ty = 0;
	d = 0.0;
	exrad = 0;
    }

    public static void setAlienSpeed(double speed)
    {
	donutAlien.speed = speed;
    }

    public boolean setAlien(int x1,int y1,int x2,int y2)
    {

	double dx,dy;

	if(active)
	    return false;

	if(exploding)
	    return false;

	sx = x1;
	sy = y1;
	x = x1;
	y = y1;
	tx = x2;
	ty = y2;

	dx = tx - x;
	dy = ty - y;

	d = Math.sqrt((dx*dx) + (dy*dy));

	cx = dx/d * speed;
	cy = dy/d * speed;

	rx = x1;
	ry = y1;

	active = true;

	return true;
    }

    public boolean resetAlien(int x1,int y1)
    {

	double dx,dy;
	
	int t[] = new int[6];
	t[0] = 27+16;
	t[1] = 109+16;
	t[2] = 191+16;
	t[3] = 416+16;
	t[4] = 492+16;
	t[5] = 580+16;

	if(y<300)
	    {
		int j = (int)(Math.random()*6);
		tx = t[j];
	    }


	sx = x1;
	sy = y1;
	x = x1;
	y = y1;

	dx = tx - x;
	dy = ty - y;

	d = Math.sqrt((dx*dx) + (dy*dy));

	cx = dx/d * speed;
	cy = dy/d * speed;

	rx = x1;
	ry = y1;

	return true;
    }


    @SuppressWarnings("unused")
	public void animate()
    {
	if(!active && !exploding)
	    return;

	if(exploding)
	    {
		cy += 0.3;
		cx += (int)(Math.random()*9)-4;

		if(cx>9)
		    cx = 9;

		if(cx< -9)
		    cx = -9;

		active = false;
		
		exrad += 10;

		Explosion e;
		if(y>= (ty-9))
		    {
			exploding = false;
		    }
	
		
		if(exploding)
		{
			SoundEffect.FUZZ_OUT.playIfNotPlaying();
		}
		
		y += cy;
		x += cx;
		int ii;
		for(ii=0;ii<6;ii++)
		    {
			Crumb c = new Crumb(x+(int)(Math.random()*5-2),
					    y+(int)(Math.random()*5-2),
					    -cx,
					    -cy,speed*2,8,20);
		    }

		for(ii=0;ii<4;ii++)
		    {
			Crumb c = new Crumb(x,y,
					    (int)(Math.random()*5)-2,
					    -5-(int)(Math.random()*5));
		    }

		//cy += 2; /*Fall Faster and Faster*/
 
		/*e = new Explosion(x,y,exrad+4);*/

		return;
	    }

	/*Check to see if we have hit a colle3ctor desk. If do check
	  to see if the collector is alive if they are well they won't 
	  be for long. :) */

	/* This does not belong here. Donut Strink should be queried
	   from class collector */
	if(ry > 461.0)
	    {
		if(rx > 580.0)
		    {
			if(rx < 613)
			    {
				donutcommand.collectors[5].donutStrike(this);
			    }
		    }
		else if(rx >= 492.0)
		    {
			if(rx < 531)
			    {
				donutcommand.collectors[4].donutStrike(this);
			    }
		    }
		else if(rx >= 416.0)
		    {
			if(rx < 449)
			    {
				donutcommand.collectors[3].donutStrike(this);
			    }
		    }
		else if(rx >= 191.0)
		    {
			if(rx < 224)
			    {
				donutcommand.collectors[2].donutStrike(this);
			    }
		    }
		else if(rx >= 109.0)
		    {
			if(rx < 142)
			    {
				donutcommand.collectors[1].donutStrike(this);
			    }
		    }
		else if(rx >= 27.0)
		    {
			if(rx < 60)
			    {
				donutcommand.collectors[0].donutStrike(this);
			    }
		    }
		if(!active)
		    return;
	    }

  	double newd;

	double dx,dy;

	rx = rx + cx;
	ry = ry + cy;

	newd = (rx - sx)*(rx - sx) + (ry - sy)*(ry - sy);
        newd = Math.sqrt(newd);

        if(newd >= d)
	    {
     		active = false;
		rx = tx;
		ry = ty;
		x = (int) rx;
		y = (int) ry;

		/* Activate shattering crumbs here. */
		int ii;
		for(ii=0;ii<15;ii++)
		    {
			Crumb c = new Crumb(x,y,
					    (int)(Math.random()*5)-2,
					    -10-(int)(Math.random()*5));
		    }
		return;
  	    }

	cycle ++;
	if(cycle > 3)
	    cycle = 0;

	x = (int) rx;
	y = (int) ry;

		SoundEffect.ALIEN_BLEET.playIfNotPlaying();
    }

    public void testExplosion()
    {
	double xx,yy;

	xx = x;
	yy = y;

	if(!active)
	    return;

	if(!Explosion.testAll((int)xx,(int)yy))
	    {
		return;
	    }

	if(y>373)
	    {
		active = false;
		exploding = true;
		setBurnParticles();
		exrad = 3;
		return;
	    }

	xx = rx - cx*12.0/speed;
	yy = ry - cy*12.0/speed;

	if(!Explosion.testAll((int)xx,(int)yy))
	    {
		resetAlien((int)xx,(int)yy);
		return;
	    }

	xx = xx - cx*12.0/speed;
	yy = yy - cy*12.0/speed;

	if(!Explosion.testAll((int)xx,(int)yy))
	    {
		resetAlien((int)xx,(int)yy);
		return;
	    }

	xx = rx + cx*12.0/speed;
	yy = ry - cy*12.0/speed;

	if(!Explosion.testAll((int)xx,(int)yy))
	    {
		resetAlien((int)xx,(int)yy);
		return;
	    }

	active = false;
	exploding = true;
	setBurnParticles();
	exrad = 3;
    }

    public void setBurnParticles()
    {
    }

    public void drawBurnParticles(Graphics g)
    {
    }

    public void drawAlien(Graphics g)
    {
	Graphics2D g2d = (Graphics2D)g;


	if(active)
	{
	    //g.setColor(Color.blue);
	    //g.drawLine((int)sx,(int)sy,(int)tx,(int)ty);
	    g2d.drawImage(donutcommand.donutBox,x-8,y-7,null);
	    return;
	}

	if(exploding)
	    {
		
		g2d.drawImage(donutcommand.boxCrumple[(int)(Math.random()*3)],x-8,y-7,null);
		return;
	    }
    }

}

final class donutMissile
{

    public boolean active=false;
    public boolean exploding=false;

    public int x,y;
    public int tx,ty,sx,sy;
    public static double speed=4;

    double rx,ry,cx,cy;
    double d;
    int cycle = 0;

    int exrad = 0;

    public static void setDonutSpeed(double speed)
    {
	donutMissile.speed = speed;
    }

    public donutMissile()
    {
	active = false;
	exploding = false;
	rx = 0.0;
	ry = 0.0;
	cx = 0.0;
	cy = 0.0;
	x = 0;
	y = 0;
	tx = 0;
	ty = 0;
	d = 0.0;
	exrad = 0;
    }

    public boolean setDonut(int x1,int y1,int x2,int y2)
    {

	double dx,dy;

	if(active)
	    return false;

	if(exploding)
	    return false;

	sx = x1;
	sy = y1;
	x = x1;
	y = y1;
	tx = x2;
	ty = y2;

	dx = tx - x;
	dy = ty - y;

	d = Math.sqrt((dx*dx) + (dy*dy));

	cx = dx/d * speed;
	cy = dy/d * speed;

	rx = x1;
	ry = y1;

	active = true;

	return true;
    }

    public static boolean pagodaHit(int x,int y)
    {

	if(y>=471)
	    {
		if(x>=262 && x<379)
		    {
			return true;
		    }
	    }

	if(y>=463)
	    {
		if(x>=272 && x<369)
		    {
			return true;
		    }
	    }

	if(y>=455)
	    {
		if(x>=282 && x<359)
		    {
			return true;
		    }
	    }


	if(y>=447)
	    {
		if(x>=292 && x<349)
		    {
			return true;
		    }
	    }

	if(y>=439)
	    {
		if(x>=302 && x<339)
		    {
			return true;
		    }
	    }

	if(y>=431)
	    {
		if(x>=312 && x<329)
		    {
			return true;
		    }
	    }

	return false;
    }

    @SuppressWarnings("unused")
	public void animate()
    {
	if(!active && !exploding)
	    return;

	if(exploding)
	    {
		active = false;
		exrad += 10;

		Explosion e;
		if(exrad>120)
		    {
			exploding = false;
		    }

		e = new Explosion(x,y,exrad+4);

		return;
	    }

	if(pagodaHit((int)rx,(int)ry) )
	    {
		active=false;
		/* Activate shattering crumbs here. */
		int ii;
		for(ii=0;ii<25;ii++)
		    {
			Crumb c = new Crumb(x+(int)(Math.random()*5-2),
					    y+(int)(Math.random()*5-2),
					    -cx,
					    -cy,speed,8,20);
		    }
		donutcommand.hitPagoda();
		return;
	    }

	/*Check to see if we have hit a colle3ctor desk. If do check
	  to see if the collector is alive if they are well they won't 
	  be for long. :) */

	/* This does not belong here. Donut Strink should be queried
	   from class collector */
	if(ry > 461.0)
	    {
		if(rx > 580.0)
		    {
			if(rx < 613)
			    {
				donutcommand.collectors[5].donutStrike(this);
			    }
		    }
		else if(rx >= 492.0)
		    {
			if(rx < 531)
			    {
				donutcommand.collectors[4].donutStrike(this);
			    }
		    }
		else if(rx >= 416.0)
		    {
			if(rx < 449)
			    {
				donutcommand.collectors[3].donutStrike(this);
			    }
		    }
		else if(rx >= 191.0)
		    {
			if(rx < 224)
			    {
				donutcommand.collectors[2].donutStrike(this);
			    }
		    }
		else if(rx >= 109.0)
		    {
			if(rx < 142)
			    {
				donutcommand.collectors[1].donutStrike(this);
			    }
		    }
		else if(rx >= 27.0)
		    {
			if(rx < 60)
			    {
				donutcommand.collectors[0].donutStrike(this);
			    }
		    }
		if(!active)
		    return;
	    }

  	double newd;

	double dx,dy;

	rx = rx + cx;
	ry = ry + cy;

	newd = (rx - sx)*(rx - sx) + (ry - sy)*(ry - sy);
        newd = Math.sqrt(newd);

        if(newd >= d)
	    {
     		active = false;
		rx = tx;
		ry = ty;
		x = (int) rx;
		y = (int) ry;

		/* Activate shattering crumbs here. */
		int ii;
		for(ii=0;ii<15;ii++)
		    {
			Crumb c = new Crumb(x,y,
					    (int)(Math.random()*5)-2,
					    -10-(int)(Math.random()*5));
		    }
		return;
  	    }

	cycle ++;
	if(cycle > 3)
	    cycle = 0;

	x = (int) rx;
	y = (int) ry;

    }

    @SuppressWarnings("unused")
	public static void testExplosion(donutMissile donuts[])
    {
	int i;

	for(i=0;i<donuts.length;i++)
	    {
		if(!donuts[i].active)
		    continue;

		if(Explosion.testAll(donuts[i].x,donuts[i].y))
		    {
			donuts[i].active = false;
			donuts[i].exploding = true;

			/* SOUND Donut Explosion */
			/*
			if(donutcommand.playMissilePop)
			    {
				donutcommand.soundManager.play(donutcommand.missilePop);
				donutcommand.playMissilePop = false;
			    }
			*/
			//remove comment SoundEffect.ENEMY_MISSILE_EXPLODE.play();
			
			donutcommand.gameScore += 25;
			donuts[i].exrad = 3;
			int ii;
			for(ii=0;ii<15;ii++)
			    {
				Crumb c = new Crumb(donuts[i].x,
						    donuts[i].y,
						    donuts[i].cx,
						    donuts[i].cy,speed,
						    5,20);
			    }
		    }
	    }
    }

    public void drawDonut(Graphics g)
    {
	Graphics2D g2d = (Graphics2D)g;


	if(active)
	{


	    BasicStroke myst = new BasicStroke(10,
					       BasicStroke.CAP_BUTT,
					       BasicStroke.JOIN_ROUND,
					       0,new float[]{2,4},0); /* JJJ Move*/
	    g2d.setStroke((Stroke)myst);
	    
	    g2d.setColor(Color.yellow);
	    /*
	    g.setColor(new Color(200,255,200));
	    g.drawLine((int)sx,(int)sy,x,y);
	    g.drawLine((int)sx-1,(int)sy,x-1,y);
	    */
	    g2d.drawLine(sx,sy,(int)(x-5*cx/speed),(int)(y-5*cy/speed));

	    g2d.drawImage(donutcommand.donut[cycle],x-6,y-6,null);
	    return;
	}

	if(exploding)
	    {
		int ex,ey;
		ex = x - (exrad+1)/2;
		ey = y - (exrad+1)/2;
		g.setColor(Color.blue);
		g.fillOval(ex,ey,exrad,exrad);

		int lrad=exrad - 20;
		if(lrad > 0)
		    {
			ex = x - (lrad+1)/2 + (int)(Math.random()*11-5);
			ey = y - (lrad+1)/2 + (int)(Math.random()*11-5);
			g.setColor(Color.black);
			g.fillOval(ex,ey,lrad,lrad);
		    }

		//g.drawImage(donutcommand.donut[cycle],x-6,y-6,null);
		return;
	    }
    }
}

final class collector
{

    int x,y; /*The upper left corner as opposed to the center.*/
    boolean alive;
    boolean dying;
    int phase;
    int deathphase;

    int delay;

    public collector(int sx,int sy)
    {

	x = sx;
	y = sy;

	phase = (int)(Math.random()*6);
	alive = true;
	dying = false;
	delay = 0;
	deathphase = 0;
    }

    public void reset()
    {
	phase = (int)(Math.random()*6);
	alive = true;
	dying = false;
	delay = 0;
	deathphase = 0;
    }

    public void donutStrike(donutAlien m)
    {
	if(!alive)
	    {
		return;
	    }

	m.active = false;
	m.exploding = false;
	alive = false;
	dying = true;
	deathphase = 0;
	delay = -1;
    }

    public void donutStrike(donutMissile m)
    {
	if(!alive)
	    {
		return;
	    }

	m.active = false;
	m.exploding = false;
	alive = false;
	dying = true;
	deathphase = 0;
	delay = -1;
    }

    @SuppressWarnings("unused")
	public void animate()
    {
	if(alive)
	    {
		delay++;
		if(delay>2)
		    {
			delay = 0;
		    }
		else
		    {
			return;
		    }

		int newphase = (int)(Math.random()*6);
		if(phase == newphase)
		    newphase = (newphase + 1) % 6;
		phase = newphase;
		return;
	    }

	if(dying)
	    {
		delay++;
		switch(deathphase)
		    {
		    case 0:
		    	SoundEffect.MUNCH_MUNCH.play();
		    case 1:
		    case 2:
		    case 3:
		    case 4:
		    case 5:
		    case 6:
			if(delay>5)
			    {
				delay = 0;
				deathphase++;
			    }
			break;
		    case 7:
		    	SoundEffect.COLLECTOR_EXPLODE.play();
		    case 8:
		    case 9:
		    case 10:
			int ii;
			for(ii=0;ii<30;ii++)
			    {
				Crumb c = new Crumb(x+(int)(Math.random()*15)-2+17,
						    478,
						    (int)(Math.random()*11)-5,
						    -10-(int)(Math.random()*10));
			    }
		    default:
			if(delay>0)
			    {
				delay = 0;
				deathphase++;
			    }
		    }
		if(deathphase > 20)
		    {
			deathphase = 20;
			delay = 0;
		    }

		return ;
	    }
    }

    public void draw(Graphics g)
    {
	if(alive)
	    {
		g.drawImage(donutcommand.coll[phase],x,y,null);
		return;
	    }

	g.drawImage(donutcommand.explode[deathphase],x-32,y-21,null);
    }
}

final class missile
{

    public boolean active;
    public boolean exploding;

    int explodecount = 0;
    int explodecountmax = 3;

    int exrad = 0;

    double rx,ry;
    double tx,ty; 
    public int x,y;
    public int ex,ey;

    double dx,dy;
    double cx,cy;

    double d; /* distance */

    static double stepper = 10.0;
    public int cycle=0;

    public static int radmax = 150; /* May tinker with power */

    public missile()
    {
	active = false;
	exploding = false;

	rx = 0.0;
	ry = 0.0;
	tx = 0.0;
	ty = 0.0;

	dx = 0.0;
	dy = 0.0;

	cx = 0.0;
	cy = 0.0;

	/*stepper = 10.0;*/
    }

    public static void setMugSpeed(double ms)
    {
	stepper = ms;
    }

    public boolean setTarget(int ix,int iy)
    {
      
	if(active || exploding)
	    return false;

	if((ix == 320) && (iy == 429))
	    {
		iy = 430;
	    }
	tx = (double)ix;
	ty = (double)iy;

	ry = 429.0;
	rx = 320.0;

	dx = tx - rx;
	dy = ty - ry;

	d = Math.sqrt((dx*dx) + (dy*dy));

	cx = dx/d * stepper;
	cy = dy/d * stepper;

	/* take 1 set forward */
	rx = rx + cx;
	ry = ry + cy;

	x = (int) rx;
	y = (int) ry;

	active = true;
	cycle = 0;

	SoundEffect.FIRE_CANNON.play();
	
	return true;
    }

    @SuppressWarnings("unused")
	public void animateExplode()
    {
	if(exploding == false)
	    return;

	exrad += 20;

	if(exrad > radmax)
	    {
		exrad = exrad/(1+explodecountmax - explodecount);
		explodecount++;
		if(explodecount >= explodecountmax)
		    {
			exploding = false;
		    }
	    }

        ex = x - (exrad + 1)/2;
	ey = y - (exrad + 1)/2;

	if(exploding)
	    {
		Explosion e;
		e = new Explosion((int)x,(int)y,(int)exrad+4);
	    }
    }

    public void animate()
    {
	double ddx = rx - 320.0;
	double ddy = ry - 429.0;
	
	double td = Math.sqrt((ddx*ddx) + (ddy*ddy));

	if(active == false)
	    return;

	if(td >= d)
	    {
		active = false;
		x = (int)tx;
		y = (int)ty;
		exploding = true;
		SoundEffect.COFFEE_POP.play();
		explodecount = 0;
		exrad = 7;
		ex = x - 4;
		ey = y - 4;
		return;
	    }

	cycle++;
	if(cycle>7)
	    cycle = 0;

	/* take 1 set forward*/
	rx = rx + cx;
	ry = ry + cy;
	
	x = (int)rx;
	y = (int)ry;

    }

    public void drawMissile(Graphics g)
    {
	if(!active)
	    return;

	g.drawImage(donutcommand.cup[cycle],x-8,y-5,null);
    }

    public void drawExploding(Graphics g)
    {
	if(!exploding)
	    return;

	if(explodecount < 2)
	    {
		g.setColor(Color.red);
	    }
	else
	    {
		int yy = (int)(Math.random()*Crumb.colors.length);
		g.setColor(Crumb.colors[yy]);
	    }

	g.fillOval(ex,
		   ey,
		   exrad,
		   exrad);
	
	int brad = exrad - 20;
	int bx = x - (brad + 1)/2 + (int)(Math.random()*10) -5;
	int by = y - (brad + 1)/2 + (int)(Math.random()*10) -5;
	if(brad>0 && explodecount < 2)
	    {
		if(explodecount == 0)
		    {
			g.setColor(Color.black);
		    }
		else
		    {
			int yy = (int)(Math.random()*Crumb.colors.length);
			g.setColor(Crumb.colors[yy]);
		    }
		g.fillOval(bx,by,brad,brad);
	    }
	
    }
    
}

@SuppressWarnings("serial")
public final class donutcommand 
    extends Applet 
    implements Runnable,
	       MouseListener, 
	       MouseMotionListener, 
	       KeyListener
{

    donutAlien theFiend;
    
    public static int DELAY = 40;

	/*
    private static final AudioFormat PLAYBACK_FORMAT =
        new AudioFormat(8000, 16, 1, true, false);
    */

	/*
    //all of these sounds are crap now
    public static SoundManager soundManager;
    public static Sound missilePop;
    public static boolean playMissilePop=true; //Play Flag

    public static Sound coffeeShot;
    public static boolean playCoffeeShot=true; //Play Flag

    public static Sound collectorPop;
    public static boolean playCollectorPop=true; //Play Flag

    public static Sound coffeePop;
    public static boolean playCoffeePop=true;    //Play Flag

    public static Sound munchMunch;
    public static boolean playMunchMunch=true; //Play Flag

    public static Sound alienBleet;
    public static boolean playAlienBleet = true; //Play Flag

    public static Sound fuzzOut;
    public static boolean playFuzzOut = true;//Play Flag

    public static Sound donutcommandSound;

    public static Sound cannonClick = null;
    public static Sound brownStatic = null;
    public static Sound extraGuy = null;
    public static Sound bulletCount = null;
    public static Sound collectorPlop = null;
    */

    public static Image pDO = null;
    public static Image pNUT = null;
    public static Image pCOM = null;
    public static Image pMAND = null;

    public boolean startplay = false;

    Graphics superg = null;
    
    public static Image coll[];
    public static Image explode[];
    static Image db;

    public static Image boxCrumple[];

    public static Image level;
    public static Image score;
    public static Image number[];

    public static Image donutBox=null;

    public static boolean ceasefire = false;

    public static missile missiles[];

    public static Image staticField[];

    public static Image levelComplete = null;

    public static collector collectors[];

    public static int gameScore = 0;
    public static int gameLevel = 1;

    static Image cross[];
    public static Image cup[];

    public static Image donut[];
    public static Image pagoda[];

    public static Image click2play = null;
    public static Image splashscreen = null;

    public static Image gameover = null;

    double moonx = -42.0;
    double moony = 40.0;

    static Image moon;
    static Image background;

    static Image ammo;

    public static int reloadcountdown = 0;
    public static int reloadtime = 10;

    int aimx = 0;
    int aimy = 0;
    int aimp = 0;

    static int shellcount = 28;

    public static int extraAmmo = 2;

    public static int nextBonus = 0;
    public static int nextBonusAmount = 0;

    public static donutMissile donuts[];

    /************ SET LEVEL VARIABLES ************/
    private int singleShot=0; /* Random Aim */
    private int doubleShot=0; /* Random Aim */
    private int tripleShot=0; /* Random Aim Equidistant */
    private int sniperShot=0; /* Aimed Single */
    private int sniperDouble=0; /* Double Shot one Aimed */
    private int sniperTripple=0; /* Tripple Shot Equidistant One Aimed */
    private int fireDelay=0; /* Min time between Shots */
    private int donutThreshold=0; /* Active donut number must be less 
				   than to fire.*/

    private int fireDelayCounter=0;
    private int levelCountDownTimer = 0;

    private int activeDonutCount = 0;

    int shootThis[];

    public static boolean PAUSED = true;

    public donutcommand()
    {
    }

    public void resetGame()
    {
	gameScore = 0;
	gameLevel = 0;
	int i;
	for(i=0;i<6;i++)
	    {
		collectors[i].alive = true;
	    }
    }

    /** I do not want the sound manager to be over run, some sounds
     * may get multiple play in a given cycle. A chunky but ok for this
     * program method to control this is to have a play flag and only let a sound
     * play once. We reset that variables each animation cycle.
     */
	public static void resetSoundVars()
	{
		/*
		playMissilePop=true;   //Play Flag
		playCoffeeShot=true;   //Play Flag
		playCollectorPop=true; //Play Flag
		playCoffeePop=true;    //Play Flag
		playMunchMunch = true; //Play Flag
		playAlienBleet = true; //Play Flag
		playFuzzOut = true;    //Play Flag
		*/
	}

	public void init()
	{

		/*
		soundManager = new SoundManager(PLAYBACK_FORMAT, 16);
		*/

		/*
		//Sound Variables
		missilePop = null;
		coffeeShot = null;
		collectorPop = null;
		coffeePop = null;
		munchMunch = null;
		alienBleet = null;
		fuzzOut = null;
		donutcommandSound = null;
		cannonClick = null;
		brownStatic = null;
		extraGuy = null;
		bulletCount = null;
		collectorPlop = null;
        */
		/*
		 missilePop = soundManager.getSound("enemyMissleExplosion.wav");
		 coffeeShot = soundManager.getSound("CannonShot.wav");
		 collectorPop = soundManager.getSound("CollectorExplode.wav");
		 coffeePop = soundManager.getSound("CoffeePop.wav");
		 munchMunch = soundManager.getSound("MunchMunch.wav");
		 */
		
		
		coll = new Image[6];
		explode = new Image[21];
		donut = new Image[4];
		moon = null;
		background = null;

		boxCrumple = new Image[3];

		level = null;
		score = null;
		number = new Image[10];

		staticField = new Image[4];

		int i;

		shootThis = new int[6];

		missiles = new missile[10];
		for(i=0;i<missiles.length;i++)
			missiles[i] = new missile();

		donuts = new donutMissile[100];
		for(i=0;i<donuts.length;i++)
			donuts[i] = new donutMissile();

		theFiend = new donutAlien();

		collectors = new collector[6];
		collectors[0] = new collector(27,446);
		collectors[1] = new collector(109,446);
		collectors[2] = new collector(191,446);
		collectors[3] = new collector(416,446);
		collectors[4] = new collector(492,446);
		collectors[5] = new collector(580,446);

		cross = new Image[2];
		cup = new Image[8];
		pagoda = new Image[2];

		ammo = null;

		db = createImage(640,524);
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
	}

	public static boolean started = false; /*Not a good way*/

	public void stop()
	{
		donutcommand.PAUSED = true;
	}

	public void destroy()
	{
		started = false;
	}

	public void start()
	{
		SoundEffect.init();
		
		donutcommand.PAUSED = false;

		if(started)
			return;
	
		started = true;

		Crumb.makeColors();

		try
		{
			coll [0] = getImage(getCodeBase(),"donutcommand_images/coll01.gif");
			coll [1] = getImage(getCodeBase(),"donutcommand_images/coll02.gif");
			coll [2] = getImage(getCodeBase(),"donutcommand_images/coll03.gif");
			coll [3] = getImage(getCodeBase(),"donutcommand_images/coll04.gif");
			coll [4] = getImage(getCodeBase(),"donutcommand_images/coll05.gif");
			coll [5] = getImage(getCodeBase(),"donutcommand_images/coll06.gif");

			explode [0] = getImage(getCodeBase(),"donutcommand_images/explode01.gif");
			explode [1] = getImage(getCodeBase(),"donutcommand_images/explode02.gif");
			explode [2] = getImage(getCodeBase(),"donutcommand_images/explode03.gif");
			explode [3] = getImage(getCodeBase(),"donutcommand_images/explode04.gif");
			explode [4] = getImage(getCodeBase(),"donutcommand_images/explode05.gif");
			explode [5] = getImage(getCodeBase(),"donutcommand_images/explode06.gif");
			explode [6] = getImage(getCodeBase(),"donutcommand_images/explode07.gif");
			explode [7] = getImage(getCodeBase(),"donutcommand_images/explode08.gif");
			explode [8] = getImage(getCodeBase(),"donutcommand_images/explode09.gif");
			explode [9] = getImage(getCodeBase(),"donutcommand_images/explode10.gif");
			explode [10] = getImage(getCodeBase(),"donutcommand_images/explode11.gif");
			explode [11] = getImage(getCodeBase(),"donutcommand_images/explode12.gif");
			explode [12] = getImage(getCodeBase(),"donutcommand_images/explode13.gif");
			explode [13] = getImage(getCodeBase(),"donutcommand_images/explode14.gif");
			explode [14] = getImage(getCodeBase(),"donutcommand_images/explode15.gif");
			explode [15] = getImage(getCodeBase(),"donutcommand_images/explode16.gif");
			explode [16] = getImage(getCodeBase(),"donutcommand_images/explode17.gif");
			explode [17] = getImage(getCodeBase(),"donutcommand_images/explode18.gif");
			explode [18] = getImage(getCodeBase(),"donutcommand_images/explode19.gif");
			explode [19] = getImage(getCodeBase(),"donutcommand_images/explode20.gif");
			explode [20] = getImage(getCodeBase(),"donutcommand_images/explode21.gif");

			donut [0] = getImage(getCodeBase(),"donutcommand_images/donut01.gif");
			donut [1] = getImage(getCodeBase(),"donutcommand_images/donut02.gif");
			donut [2] = getImage(getCodeBase(),"donutcommand_images/donut03.gif");
			donut [3] = getImage(getCodeBase(),"donutcommand_images/donut04.gif");

			cup [0] = getImage(getCodeBase(),"donutcommand_images/cup01.gif");
			cup [1] = getImage(getCodeBase(),"donutcommand_images/cup02.gif");
			cup [2] = getImage(getCodeBase(),"donutcommand_images/cup03.gif");
			cup [3] = getImage(getCodeBase(),"donutcommand_images/cup04.gif");
			cup [4] = getImage(getCodeBase(),"donutcommand_images/cup05.gif");
			cup [5] = getImage(getCodeBase(),"donutcommand_images/cup06.gif");
			cup [6] = getImage(getCodeBase(),"donutcommand_images/cup07.gif");
			cup [7] = getImage(getCodeBase(),"donutcommand_images/cup08.gif");

			cross [0] = getImage(getCodeBase(),"donutcommand_images/cross02.gif");
			cross [1] = getImage(getCodeBase(),"donutcommand_images/cross01.gif");

			pagoda[0] = getImage(getCodeBase(),"donutcommand_images/pagoda01.gif");
			pagoda[1] = getImage(getCodeBase(),"donutcommand_images/pagoda02.gif");

			moon = getImage(getCodeBase(),"donutcommand_images/moon.gif");
			background = getImage(getCodeBase(),"donutcommand_images/background.gif");

			ammo = getImage(getCodeBase(),"donutcommand_images/ammo.gif");

			score = getImage(getCodeBase(),"donutcommand_images/score.gif");

			level = getImage(getCodeBase(),"donutcommand_images/level.gif");

			number[0] = getImage(getCodeBase(),"donutcommand_images/0.gif");
			number[1] = getImage(getCodeBase(),"donutcommand_images/1.gif");
			number[2] = getImage(getCodeBase(),"donutcommand_images/2.gif");
			number[3] = getImage(getCodeBase(),"donutcommand_images/3.gif");
			number[4] = getImage(getCodeBase(),"donutcommand_images/4.gif");
			number[5] = getImage(getCodeBase(),"donutcommand_images/5.gif");
			number[6] = getImage(getCodeBase(),"donutcommand_images/6.gif");
			number[7] = getImage(getCodeBase(),"donutcommand_images/7.gif");
			number[8] = getImage(getCodeBase(),"donutcommand_images/8.gif");
			number[9] = getImage(getCodeBase(),"donutcommand_images/9.gif");

			staticField[0] = getImage(getCodeBase(),"donutcommand_images/static1.gif");
			staticField[1] = getImage(getCodeBase(),"donutcommand_images/static2.gif");
			staticField[2] = getImage(getCodeBase(),"donutcommand_images/static3.gif");
			staticField[3] = getImage(getCodeBase(),"donutcommand_images/static4.gif");

			boxCrumple [0] = getImage(getCodeBase(),"donutcommand_images/boxCrumple1.gif");
			boxCrumple [1] = getImage(getCodeBase(),"donutcommand_images/boxCrumple2.gif");
			boxCrumple [2] = getImage(getCodeBase(),"donutcommand_images/boxCrumple3.gif");

			click2play =getImage(getCodeBase(),"donutcommand_images/click2play.gif");
			splashscreen =getImage(getCodeBase(),"donutcommand_images/SplashScreen.gif");

			gameover = getImage(getCodeBase(),"donutcommand_images/gameover.gif");

			levelComplete = getImage(getCodeBase(),"donutcommand_images/LevelComplete.gif");

			pDO = getImage(getCodeBase(),"donutcommand_images/DO.gif");
			pNUT = getImage(getCodeBase(),"donutcommand_images/NUT.gif");
			pCOM = getImage(getCodeBase(),"donutcommand_images/COM.gif");
			pMAND = getImage(getCodeBase(),"donutcommand_images/MAND.gif");

			donutBox = getImage(getCodeBase(),"donutcommand_images/DonutBox.gif");

			/*
			missilePop = soundManager.getSound(getCodeBase(),
				"enemyMissleExplosion.wav");
			coffeeShot = soundManager.getSound(getCodeBase(),
				"CannonShot.wav");
			collectorPop = soundManager.getSound(getCodeBase(),
				"CollectorExplode.wav");
			coffeePop = soundManager.getSound(getCodeBase(),
				"CoffeePop.wav");
			munchMunch = soundManager.getSound(getCodeBase(),
				"MunchMunch.wav");

			alienBleet = soundManager.getSound(getCodeBase(),
				"AlienBleet.wav");

			fuzzOut = soundManager.getSound(getCodeBase(),
				"FuzzOut.wav");

			donutcommandSound = soundManager.getSound(getCodeBase(),
				"DonutCommand.wav");

			cannonClick = soundManager.getSound(getCodeBase(),
				"cannonclick.wav");

			brownStatic = soundManager.getSound(getCodeBase(),
				"brownstatic.wav");

			extraGuy = soundManager.getSound(getCodeBase(),
				"extraguy.wav");
			bulletCount = soundManager.getSound(getCodeBase(),
				"bulletcount.wav");

			collectorPlop = soundManager.getSound(getCodeBase(),
				"collectorplop.wav");
			*/
		}
		catch(Exception e)
		{
			System.out.println("Exception "+e.getMessage());
			e.printStackTrace();

			Toolkit t = Toolkit.getDefaultToolkit();
		
			coll [0] = t.getImage("donutcommand_images/coll01.gif");
			coll [1] = t.getImage("donutcommand_images/coll02.gif");
			coll [2] = t.getImage("donutcommand_images/coll03.gif");
			coll [3] = t.getImage("donutcommand_images/coll04.gif");
			coll [4] = t.getImage("donutcommand_images/coll05.gif");
			coll [5] = t.getImage("donutcommand_images/coll06.gif");

			explode [0] = t.getImage("donutcommand_images/explode01.gif");
			explode [1] = t.getImage("donutcommand_images/explode02.gif");
			explode [2] = t.getImage("donutcommand_images/explode03.gif");
			explode [3] = t.getImage("donutcommand_images/explode04.gif");
			explode [4] = t.getImage("donutcommand_images/explode05.gif");
			explode [5] = t.getImage("donutcommand_images/explode06.gif");
			explode [6] = t.getImage("donutcommand_images/explode07.gif");
			explode [7] = t.getImage("donutcommand_images/explode08.gif");
			explode [8] = t.getImage("donutcommand_images/explode09.gif");
			explode [9] = t.getImage("donutcommand_images/explode10.gif");
			explode [10] = t.getImage("donutcommand_images/explode11.gif");
			explode [11] = t.getImage("donutcommand_images/explode12.gif");
			explode [12] = t.getImage("donutcommand_images/explode13.gif");
			explode [13] = t.getImage("donutcommand_images/explode14.gif");
			explode [14] = t.getImage("donutcommand_images/explode15.gif");
			explode [15] = t.getImage("donutcommand_images/explode16.gif");
			explode [16] = t.getImage("donutcommand_images/explode17.gif");
			explode [17] = t.getImage("donutcommand_images/explode18.gif");
			explode [18] = t.getImage("donutcommand_images/explode19.gif");
			explode [19] = t.getImage("donutcommand_images/explode20.gif");
			explode [20] = t.getImage("donutcommand_images/explode21.gif");

			donut [0] = t.getImage("donutcommand_images/donut01.gif");
			donut [1] = t.getImage("donutcommand_images/donut02.gif");
			donut [2] = t.getImage("donutcommand_images/donut03.gif");
			donut [3] = t.getImage("donutcommand_images/donut04.gif");

			cup [0] = t.getImage("donutcommand_images/cup01.gif");
			cup [1] = t.getImage("donutcommand_images/cup02.gif");
			cup [2] = t.getImage("donutcommand_images/cup03.gif");
			cup [3] = t.getImage("donutcommand_images/cup04.gif");
			cup [4] = t.getImage("donutcommand_images/cup05.gif");
			cup [5] = t.getImage("donutcommand_images/cup06.gif");
			cup [6] = t.getImage("donutcommand_images/cup07.gif");
			cup [7] = t.getImage("donutcommand_images/cup08.gif");

			cross [0] = t.getImage("donutcommand_images/cross02.gif");
			cross [1] = t.getImage("donutcommand_images/cross01.gif");

			pagoda[0] = t.getImage("donutcommand_images/pagoda01.gif");
			pagoda[1] = t.getImage("donutcommand_images/pagoda02.gif");

			moon = t.getImage("donutcommand_images/moon.gif");
			background = t.getImage("donutcommand_images/background.gif");
			ammo = t.getImage("donutcommand_images/ammo.gif");

			score = t.getImage("donutcommand_images/score.gif");

			level = t.getImage("donutcommand_images/level.gif");

			number[0] = t.getImage("donutcommand_images/0.gif");
			number[1] = t.getImage("donutcommand_images/1.gif");
			number[2] = t.getImage("donutcommand_images/2.gif");
			number[3] = t.getImage("donutcommand_images/3.gif");
			number[4] = t.getImage("donutcommand_images/4.gif");
			number[5] = t.getImage("donutcommand_images/5.gif");
			number[6] = t.getImage("donutcommand_images/6.gif");
			number[7] = t.getImage("donutcommand_images/7.gif");
			number[8] = t.getImage("donutcommand_images/8.gif");
			number[9] = t.getImage("donutcommand_images/9.gif");

			staticField[0] = t.getImage("donutcommand_images/static1.gif");
			staticField[1] = t.getImage("donutcommand_images/static2.gif");
			staticField[2] = t.getImage("donutcommand_images/static3.gif");
			staticField[3] = t.getImage("donutcommand_images/static4.gif");

			boxCrumple [0] = t.getImage("donutcommand_images/boxCrumple1.gif");
			boxCrumple [1] = t.getImage("donutcommand_images/boxCrumple2.gif");
			boxCrumple [2] = t.getImage("donutcommand_images/boxCrumple3.gif");

			click2play = t.getImage("donutcommand_images/click2play.gif");
			splashscreen = t.getImage("donutcommand_images/SplashScreen.gif");

			gameover = t.getImage("donutcommand_images/gameover.gif");
	      

			levelComplete = t.getImage("donutcommand_images/LevelComplete.gif");

			pDO = t.getImage("donutcommand_images/DO.gif");
			pNUT = t.getImage("donutcommand_images/NUT.gif");
			pCOM = t.getImage("donutcommand_images/COM.gif");
			pMAND = t.getImage("donutcommand_images/MAND.gif");

			donutBox = t.getImage("donutcommand_images/DonutBox.gif");

			/*
			missilePop = soundManager.getSound("enemyMissleExplosion.wav");
			coffeeShot = soundManager.getSound("CannonShot.wav");
			collectorPop = soundManager.getSound("CollectorExplode.wav");
			coffeePop = soundManager.getSound("CoffeePop.wav");
			munchMunch = soundManager.getSound("MunchMunch.wav");
			alienBleet = soundManager.getSound("AlienBleet.wav");
			fuzzOut = soundManager.getSound("FuzzOut.wav");
			donutcommandSound = soundManager.getSound("DonutCommand.wav");
			cannonClick = soundManager.getSound("cannonclick.wav");
			brownStatic = soundManager.getSound("brownstatic.wav");
			extraGuy = soundManager.getSound("extraguy.wav");
			bulletCount = soundManager.getSound("bulletcount.wav");
			collectorPlop = soundManager.getSound("collectorplop.wav");
			*/
		}

		try
		{
			int[] pixels = new int[16 * 16];
		    Image image = Toolkit.getDefaultToolkit().createImage(
		        new MemoryImageSource(16, 16, pixels, 0, 16));
		    Cursor transparentCursor = Toolkit.getDefaultToolkit().createCustomCursor(
		        image, new Point(0, 0), "invisibleCursor");
		    
		    this.setCursor(transparentCursor);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			
		}
		
		MediaTracker MT;
		MT = new MediaTracker(this);

		int i;

		for(i=0;i<2;i++)
		{
			MT.addImage(pagoda[i],0);
			MT.addImage(cross[i],0);
			MT.addImage(boxCrumple[i],0);
		}

		for(i=0;i<8;i++)
		{
			MT.addImage(cup[i],0);
		}

		for(i=0; i<21; i++)
		{
			MT.addImage(explode[i],0);
		}

		for(i=0;i<6;i++)
		{
			MT.addImage(coll[i],0);
		}

		for(i=0;i<4;i++)
		{
			MT.addImage(donut[i],0);
			MT.addImage(staticField[i],0);
		}

		for(i=0;i<10;i++)
		{
			MT.addImage(number[i],0);
		}

		MT.addImage(moon,0);
		MT.addImage(background,0);
		MT.addImage(ammo,0);

		MT.addImage(score,0);

		MT.addImage(level,0);

		MT.addImage(levelComplete,0);

		MT.addImage(donutBox,0);

		MT.addImage(click2play,0);
		MT.addImage(splashscreen,0);
		MT.addImage(gameover,0);

		MT.addImage(pDO,0);
		MT.addImage(pNUT,0);
		MT.addImage(pCOM,0);
		MT.addImage(pMAND,0);

		try{MT.waitForAll();}
		catch(Exception e){}

		//System.out.println("About to Start!");
		Thread t;
		t = new Thread(this);
		t.start();
	}

	public static void hitPagoda()
	{
		shellcount -= 5;
		if(shellcount<1)
		{
			shellcount = 0;
			reloadcountdown = reloadtime;
		}
	}

    public void drawAmmoPile(Graphics g)
    {
	int i;
	int j=1;

	int s;
	int startx = 318;
	int starty = 439;

	s = shellcount;

	while(s>0)
	    {
		for(i=0;i<j&&s>0;i++,s--)
		    {
			g.drawImage(ammo,startx+(10*i),starty,null);
		    }
		j++;
		startx = startx-5;
		starty = starty+5;
	    }

    }

    public void drawAmmoPile2(Graphics g)
    {
	int i;
	int j=7;

	int s;
	int startx = 289;
	int starty = 469;

	s = shellcount;

	while(s>0)
	    {
		for(i=0;i<j && s>0 && j>0;i++,s--)
		    {
			g.drawImage(ammo,startx+(10*i),starty,null);
		    }
		j--;
		startx = startx+5;
		starty = starty-5;
	    }

    }


    public void drawAmmoPile3(Graphics g,int shells)
    {
	int i;
	int j=7;

	int s;
	int startx = 289;
	int starty = 469;

	s = shells;

	while(s>0)
	    {
		for(i=0;i<j && s>0 && j>0;i++,s--)
		    {
			g.drawImage(ammo,startx+(10*i),starty,null);
		    }
		j--;
		startx = startx+5;
		starty = starty-5;
	    }

    }



    public void drawGameScreen(Graphics g)
    {
	int i,sx;
	g.drawImage(background,0,0,null); /*Clear The Screen (Sorta)*/

	g.drawImage(score,6,2,null);

	if(gameScore<0)
	    {
		gameScore = 0;
	    }

	int tmpscore = gameScore;

	for(sx = 151, i = 0;i<7;i++,sx -= 13)
	    {
		g.drawImage(number[tmpscore % 10],sx,2,null);
		tmpscore = tmpscore / 10;
	    }

	int tmplevel = gameLevel;

	g.drawImage(level,546,2,null);

	g.drawImage(number[tmplevel % 10],622,2,null);
	tmplevel = tmplevel/10;
	g.drawImage(number[tmplevel % 10],609,2,null);

	moonx += 1;

	if(moonx>1000.0)
	    {
		moonx = -42.0;
	    }

	if(moonx<641)
	    {
		double moond = Math.sqrt((moonx-299.0)*(moonx-299.0))/341.0;
		moond = 140*(moond*moond) + 1;

		g.drawImage(moon,(int)moonx,(int) moond,null);
	    }

	for(i=0;i<collectors.length;i++)
	    {
		collectors[i].draw(g);
	    }

	g.drawImage(pagoda[aimp],262,430,null);

	drawAmmoPile2(g);

	for(i=0;i<missiles.length;i++)
	    {
		missiles[i].drawExploding(g);
	    }

	for(i=0;i<missiles.length;i++)
	    {
		missiles[i].drawMissile(g);
	    }

	for(i=0;i<donuts.length;i++)
	    {
		donuts[i].drawDonut(g);
	    }

	theFiend.drawAlien(g);

	Crumb.drawAll(g);

	g.drawImage(cross[0],aimx-14,aimy-14,null);
	if(aimp == 1)
	    {
		g.drawImage(cross[1],aimx-14,aimy-14,null);
	    }

    }

    public void fastpaint()
    {
	Graphics g = getGraphics();
	if(g!=null)
	    g.drawImage(db,0,0,null);
    }

    public void paint(Graphics g)
    {
    }

    /*MouseMotionListener functions*/
    public void mouseDragged(MouseEvent e)
    {
	aimx = e.getX();
	aimy = e.getY();
	if (aimy>410)
	    aimy = 410;
	e.consume();
    }
    
    public void mouseMoved(MouseEvent e)
    {
	aimx = e.getX();
	aimy = e.getY();
	if (aimy>410)
	    aimy = 410;

	e.consume();
    }
    
    /*Mouselistener functions*/
    public void mouseReleased(MouseEvent e)
    {
	e.consume();
    }
    
    public void mouseClicked(MouseEvent e)
    {

	e.consume();
    }
    
    public void mousePressed(MouseEvent e)
    {
	int i;

	if(!startplay)
	    {
		startplay = true;
		e.consume();
		return;
	    }

	if(ceasefire)
	    {
		e.consume();
		return;
	    }

	if(shellcount < 1)
	    {
		SoundEffect.CANNON_CLICK.play();
		e.consume();
		return ;
	    }

	//System.out.println("Fire a missile at "+e.getX()+","+e.getY());

	/*for(i=0;i<missiles.length && !missiles[i].setTarget(e.getX(),e.getY());i++);*/
	for(i=0;i<missiles.length && !missiles[i].setTarget(aimx,aimy);i++);

	if(i>=missiles.length)
	    {
		e.consume();
		return; 
	    }

	/* RELOAD */
	shellcount--;
	if(shellcount<1)
	    {
		reloadcountdown = reloadtime;
		/*shellcount = 28;*/
	    }

	gameScore -= 3;

	this.requestFocus();
	e.consume();
    }
    
    public void mouseEntered(MouseEvent e)
    {  
	e.consume();
    }
    
    public void mouseExited(MouseEvent e)
    {  
	e.consume();
    }
    
    /*KeyListener functions*/
    public void keyPressed(KeyEvent e)
    {

	char r = e.getKeyChar();

	switch (r)
	    {
	    case '1':
		donutcommand.DELAY = 55;
		break;
	    case '2':
		donutcommand.DELAY = 50;
		break;
	    case '3':
		donutcommand.DELAY = 45;
		break;
	    case '4':
		donutcommand.DELAY = 40;
		break;
	    case '5':
		donutcommand.DELAY = 35;
		break;
	    case '6':
		donutcommand.DELAY = 30;
		break;
	    case '7':
		donutcommand.DELAY = 25;
		break;
	    case '8':
		donutcommand.DELAY = 20;
		break;
	    case '9':
		donutcommand.DELAY = 15;
		break;
	    case '0':
		donutcommand.DELAY = 10;
		break;
	    case '-':
		donutcommand.DELAY += 5;
		if(donutcommand.DELAY > 100)
		    donutcommand.DELAY = 100;
		break;
	    case '+':
		donutcommand.DELAY -= 5;
		if(donutcommand.DELAY < 5)
		    donutcommand.DELAY = 5;
		break;
	    }

	/*
	if(r == ' ')
	    {
		int i;
		for(i=0;i<6;i++)
		    collectors[i].reset();
	    }
	*/

	e.consume();
    }
    
    public void keyReleased(KeyEvent e)
    {
	e.consume();
    }
    
    public void keyTyped(KeyEvent e)
    {
	e.consume();
    }

    public void gameoverscreen()
    {
	boolean c[] = new boolean[6];
	int i;
	int brad = 2;
	int bx=0;
	int by=0;

	Graphics g;
	Graphics2D g2;

	for(i=0;i<6;i++)
	    {
		c[i] = false;
	    }
	drawLevelScreen(c,0,0,0,true);
	for(i=0; i < 200;i++)
	    {
		brad += 8;
		g = db.getGraphics();
		g2 = (Graphics2D) g;

		int ee = (int)(Math.random()*4);

		BufferedImage bi = new BufferedImage(25,25,BufferedImage.TYPE_3BYTE_BGR);
		Graphics gg = bi.createGraphics();
		gg.drawImage(staticField[ee],0,0,null);
		Rectangle rr = new Rectangle(0,0,25,25);
		Rectangle2D rr2d = rr.getBounds2D();
		TexturePaint tp = new TexturePaint(bi,rr2d);

		
		if(i%4 == 0)
		    {
			SoundEffect.BROWN_STATIC.playIfNotPlaying();
		    }
		if(i%10 == 0 && i < 100)
		    {
			SoundEffect.ENEMY_MISSILE_EXPLODE.play();
		    }
		
		//int yy = (int)(Math.random()*Crumb.colors.length);
		//g.setColor(Crumb.colors[yy]);
		
		bx = 320 - (brad + 1)/2 /* + (int)(Math.random()*10) -5*/;
		by = 262 - (brad + 1)/2 /* + (int)(Math.random()*10) -5*/;

		g2.setPaint(tp);

		g2.fillOval(bx,by,brad,brad);
		g.drawImage(gameover,135,155,null);
		fastpaint();
		try{Thread.sleep(40);}
		catch (Exception e){}
	    }

	try{Thread.sleep(1000);}
	catch (Exception e){}

    }

    public void splashscreen()
    {
	double rx=267;
	double ry=259;
	double cx=0;
	double cy=0;
	int i = 38;
	Image ps[] = new Image [4];

	int xs[] = new int [4];
	int ys[] = new int [4];
	int ds[] = new int [4];

	xs[0] = 62;
	ys[0] = 434;
	xs[1] = 146;
	ys[1] = 374;
	xs[2] = 313;
	ys[2] = 353;
	xs[3] = 438;
	ys[3] = 420;

	ds[0] = 400;
	ds[1] = 500;
	ds[2] = 300;
	ds[3] = 1400;

	ps[0]=pDO;
	ps[1]=pNUT;
	ps[2]=pCOM;
	ps[3]=pMAND;

	Graphics g = null;

	g = db.getGraphics();
	g.drawImage(background,0,0,null);
	g.drawImage(splashscreen,0,0,null);

	try{Thread.sleep(100);}
	catch(Exception e){}
	
	SoundEffect.GREETING.play();
	
	for(int j = 0; j < 4; j++)
	    {
		g.drawImage(background,0,0,null);
		g.drawImage(splashscreen,0,0,null);
		
		g.drawImage(ps[j],xs[j],ys[j],null);
		fastpaint();
		try{Thread.sleep(ds[j]);}
		catch(Exception e){}
	    }

	fastpaint();
	try{Thread.sleep(100);}
	catch(Exception e){}

	this.requestFocus();

	startplay = false;
	while(!startplay)
	    {
		
		g = db.getGraphics();
		g.drawImage(background,0,0,null);
		g.drawImage(splashscreen,0,0,null);

		g.drawImage(click2play,(int)rx,(int)ry,null);

		g.drawImage(cross[0],aimx-14,aimy-14,null);
		
		rx = rx + cx;
		ry = ry + cy;

		if(rx>535)
		    {
			rx = 535;
			cx = -cx;
		    }

		if(ry>461)
		    {
			ry = 461;
			cy = -cy;
		    }


		if(rx<0)
		    {
			rx = 0;
			cx = -cx;
		    }

		if(ry<0)
		    {
			ry = 0;
			cy = -cy;
		    }

		fastpaint();
		try{Thread.sleep(donutcommand.DELAY);}
		catch(Exception e){}

		i++;
		if(i>40)
		    {
			i = 0;
			cx = Math.random()*5.0;
			cy = Math.random()*5.0;
			if(Math.random() < 0.5)
			    cx = -cx;

			if(Math.random() < 0.5)
			    cy = -cy;
		    }
	    }

    }

    public void drawLevelScreen(boolean c[],int bullets,int bonusc,int bonusm,boolean supresslevel)
    {
	int i;
	int sx;
	int xoff[] = new int[6];
	Graphics g = db.getGraphics();

	xoff[0]=27;
	xoff[1]=109;
	xoff[2]=191;
	xoff[3]=416;
	xoff[4]=492;
	xoff[5]=580;

	g.drawImage(background,0,0,null); /*Clear The Screen (Sorta)*/

	g.drawImage(score,6,2,null);

	if(gameScore<0)
	    {
		gameScore = 0;
	    }

	int tmpscore = gameScore;
	for(sx = 151, i = 0;i<7;i++,sx -= 13)
	    {
		g.drawImage(number[tmpscore % 10],sx,2,null);
		tmpscore = tmpscore / 10;
	    }

	int tmplevel = gameLevel;

	g.drawImage(level,546,2,null);

	g.drawImage(number[tmplevel % 10],622,2,null);
	tmplevel = tmplevel/10;
	g.drawImage(number[tmplevel % 10],609,2,null);

	/*

	if(moonx<641)
	    {
		double moond = Math.sqrt((moonx-299.0)*(moonx-299.0))/341.0;
		moond = 140*(moond*moond) + 1;

		g.drawImage(donutcommand.moon,(int)moonx,(int) moond,null);
	    }
	*/


	for(i=0;i<6;i++)
	    {
		if(c[i])
		    {
			g.drawImage(donutcommand.coll[0],xoff[i],446,null);
		    }
		else
		    {
			g.drawImage(donutcommand.explode[20],xoff[i]-32,446-21,null);
		    }
	    }

	g.drawImage(donutcommand.pagoda[1],262,430,null);

	if(!supresslevel)
	    {

		g.drawImage(donutcommand.levelComplete,189,158,null);
		
		tmpscore = bonusc;
		for(sx = 453, i = 0;i<4;i++,sx -= 13)
		    {
			g.drawImage(number[tmpscore % 10],sx,216,null);
			tmpscore = tmpscore / 10;
		    }
		
		tmpscore = bonusm;
		for(sx = 451, i = 0;i<4;i++,sx -= 13)
		    {
			g.drawImage(number[tmpscore % 10],sx,275,null);
			tmpscore = tmpscore / 10;
		    }
		
	    }

	g.drawImage(level,546,2,null);

	/*
	g.drawImage(number[tmplevel % 10],622,2,null);
	tmplevel = tmplevel/10;
	g.drawImage(number[tmplevel % 10],609,2,null);
	*/

	drawAmmoPile3(g,bullets);

	fastpaint();
    }

    /*Level*/

    public void showLevelComplete()
    {
	int bonusc = 0;
	int bonusm = 0;

	boolean b[] = new boolean[6];
	int i;
	for(i=0;i<6;i++)
	    {
		b[i] = donutcommand.collectors[i].alive;
	    }

	drawLevelScreen(b,shellcount,0,0,false);

	try{Thread.sleep(1000);}
	catch(Exception e){}

	for(i=shellcount;i>-1;i--)
	    {
		if(i!=0)
		    {
			bonusm += 5;
			gameScore += 5;
			
			SoundEffect.BULLET_COUNT.play();
		    }
		drawLevelScreen(b,i,bonusc,bonusm,false);
		try{Thread.sleep(20);}
		catch(Exception e){}
	    }

	for(int j = 0;j < extraAmmo;j++)
	    {
		for(i=28;i>-1;i--)
		    {
			if(i!=0)
			    {
				bonusm += 5;
				gameScore += 5;
				
				SoundEffect.BULLET_COUNT.play();
			    }
			drawLevelScreen(b,i,bonusc,bonusm,false);
			try{Thread.sleep(20);}
			catch(Exception e){}
		    }
	    }

	for(i=0;i<6;i++)
	    {
		if(b[i])
		    {
			bonusc += 250;
			gameScore += 250;
			
			try
			  {
			    SoundEffect.COLLECTOR_PLOP.play();
			  }
			catch (Exception e)
			  {
				System.out.println(e.getMessage());
				System.out.println("----------------------");
				e.printStackTrace();
			  }
		    }
		else
		    continue;
		b[i] = false;
		drawLevelScreen(b,0,bonusc,bonusm,false);
		
		try{Thread.sleep(500);}
		catch(Exception e){}
	    }

    }

    @SuppressWarnings("static-access")
	private void setGameLevel(int level)
    {

	int i,j,k;
	Crumb.crumbs = null;
	Explosion.explosion = null;

	int ind[] = new int [6];

	for(i=0,j=0,k=0;i<6;i++)
	    {
		if(donutcommand.collectors[i].alive)
		    {
			j++;
		    }
		else
		    {
			ind[k] = i;
			k++;
		    }
	    }


	shellcount = 28;

	extraAmmo = 1 + (int)j/3; 
	/**< an extra pile of ammo for every 2 collectors alive*/

	for(i=0;i<missiles.length;i++)
	    {
		missiles[i].active = false;
		missiles[i].exploding = false;
	    }

	//System.out.println("Game Level Called " + level);

	switch(level)
	    {
	    case 0:
		gameScore = 0;
		gameLevel = 0;
		nextBonus = 2000;
		nextBonusAmount = 3000;

		for(i=0;i<6;i++)
		    collectors[i].reset();

		theFiend.setAlienSpeed(4);
		donutMissile.setDonutSpeed(4);
		missile.setMugSpeed(10);
		singleShot = 4;
		doubleShot = 3;
		tripleShot = 1;
		sniperShot = 4;
		sniperDouble = 4;
		sniperTripple = 3;
		fireDelay=60;
		donutThreshold = 5; /*18 shots*/
		break;
	    case 1:
		donutMissile.setDonutSpeed(4.5);
		theFiend.setAlienSpeed(4.5);
		missile.setMugSpeed(10);
		singleShot = 4;
		doubleShot = 3;
		tripleShot = 1;
		sniperShot = 5;
		sniperDouble = 4;
		sniperTripple = 3;
		fireDelay=55;
		donutThreshold = 5; /*19 shots*/
		break;
	    case 2:
		donutMissile.setDonutSpeed(5);
		theFiend.setAlienSpeed(4.7);
		missile.setMugSpeed(10.2);
		singleShot = 4;
		doubleShot = 3;
		tripleShot = 2;
		sniperShot = 5;
		sniperDouble = 4;
		sniperTripple = 3;
		fireDelay=55;
		donutThreshold = 6; /*20 shots*/
		break;
	    case 3:
		donutMissile.setDonutSpeed(5.1);
		theFiend.setAlienSpeed(4.9);
		missile.setMugSpeed(10.4);
		singleShot = 4;
		doubleShot = 2;
		tripleShot = 3;
		sniperShot = 6;
		sniperDouble = 5;
		sniperTripple = 3;
		fireDelay=50;
		donutThreshold = 6; /*23 shots*/
		break;
	    case 4:
		donutMissile.setDonutSpeed(5);
		theFiend.setAlienSpeed(5);
		missile.setMugSpeed(10.5);
		singleShot = 3;
		doubleShot = 3;
		tripleShot = 3;
		sniperShot = 7;
		sniperDouble = 5;
		sniperTripple = 4;
		fireDelay=50;
		donutThreshold = 6; /*25 shots*/
		break;
	    case 5:
		donutMissile.setDonutSpeed(6);
		theFiend.setAlienSpeed(5.5);
		missile.setMugSpeed(10.6);
		singleShot = 3;
		doubleShot = 3;
		tripleShot = 3;
		sniperShot = 7;
		sniperDouble = 5;
		sniperTripple = 4;
		fireDelay=45;
		donutThreshold = 7; /*25 shots*/
		break;

	    case 6:
		donutMissile.setDonutSpeed(7);
		theFiend.setAlienSpeed(6);
		missile.setMugSpeed(10.8);
		singleShot = 3;
		doubleShot = 4;
		tripleShot = 3;
		sniperShot = 8;
		sniperDouble = 5;
		sniperTripple = 5;
		fireDelay=40;
		donutThreshold = 7; /*28 shots*/
		break;

	    case 7:
		donutMissile.setDonutSpeed(8);
		theFiend.setAlienSpeed(6.5);
		missile.setMugSpeed(11);
		singleShot = 3;
		doubleShot = 4;
		tripleShot = 3;
		sniperShot = 8;
		sniperDouble = 5;
		sniperTripple = 5;
		fireDelay=35;
		donutThreshold = 7; /*28 shots*/
		break;

	    case 8:

		donutMissile.setDonutSpeed(9);
		theFiend.setAlienSpeed(7);
		missile.setMugSpeed(12);
		singleShot = 3;
		doubleShot = 4;
		tripleShot = 3;
		sniperShot = 8;
		sniperDouble = 5;
		sniperTripple = 5;
		fireDelay=30;
		donutThreshold = 8; /*28 shots*/
		break;

	    case 9:
		donutMissile.setDonutSpeed(10);
		theFiend.setAlienSpeed(7.5);
		missile.setMugSpeed(13);
		singleShot = 3;
		doubleShot = 4;
		tripleShot = 3;
		sniperShot = 8;
		sniperDouble = 5;
		sniperTripple = 5;
		fireDelay=25;
		donutThreshold = 8; /*28 shots*/
		break;

	    case 10:
		theFiend.setAlienSpeed(8);
		donutMissile.setDonutSpeed(11);
		missile.setMugSpeed(14);
		singleShot = 5;
		doubleShot = 5;
		tripleShot = 5;
		sniperShot = 20;
		sniperDouble = 10;
		sniperTripple = 10;
		fireDelay=20;
		donutThreshold = 9;
		break;

	    case 11:
		theFiend.setAlienSpeed(9);
		donutMissile.setDonutSpeed(12);
		missile.setMugSpeed(15);
		singleShot = 8;
		doubleShot = 9;
		tripleShot = 5;
		sniperShot = 20;
		sniperDouble = 14;
		sniperTripple = 10;
		fireDelay=15;
		donutThreshold = 13;
		break;

	    case 12:
		theFiend.setAlienSpeed(9);
		donutMissile.setDonutSpeed(12);
		missile.setMugSpeed(16);
		singleShot = 8;
		doubleShot = 9;
		tripleShot = 5;
		sniperShot = 20;
		sniperDouble = 15;
		sniperTripple = 10;
		fireDelay=14;
		donutThreshold = 14;
		break;

	    default:
		theFiend.setAlienSpeed(9);
		donutMissile.setDonutSpeed(12);
		missile.setMugSpeed(17);
		singleShot = 8;
		doubleShot = 9;
		tripleShot = 5;
		sniperShot = 21;
		sniperDouble = 15;
		sniperTripple = 10;
		fireDelay=10;
		donutThreshold = 15;
		break;

	    }

	levelCountDownTimer = 100;
	fireDelayCounter = -20;
	gameLevel++; /*Advance the game level*/

	if(gameLevel > 11)
	    extraAmmo++;

	/**We will check to see if we can revive a collector*/
	if(k>0)
	    {
		if(gameScore >= nextBonus)
		    {
			//System.out.println("Bonus Amount " + nextBonus);
			nextBonus += nextBonusAmount;
			nextBonusAmount += 3000; /*BONUS*/
			if(nextBonusAmount > 15000)
			    nextBonusAmount = 15000;
			//System.out.println("Next Bonus Amount " + nextBonus);
			j = (int)(Math.random()*k);
			j = ind[j];
			donutcommand.collectors[j].alive = true;
			SoundEffect.EXTRA_GUY.play();
			try{Thread.sleep(1000);}
			catch(Exception e){}
			extraAmmo++;
		    }
	    }

    }

    private void fireDonut()
    {
	int i;
	for(i=0;i<donuts.length;i++)
	    {
		if(donuts[i].setDonut((int)(Math.random()*640),
				      -80,
				      (int)(Math.random()*640),
				      478))
		    {
			//System.out.println("Set Donut " + i);
			i = donuts.length;
			
		    }
	    }
    }

    private void fireTheFiend()
    {
	int t[] = new int[6];
	t[0] = 27+16;
	t[1] = 109+16;
	t[2] = 191+16;
	t[3] = 416+16;
	t[4] = 492+16;
	t[5] = 580+16;
	int j = (int)(Math.random()*6);

	if(theFiend.setAlien((int)(Math.random()*640),
			      -80,
			      (int)(t[j]),
			      478))
	    {
		//System.out.println("Set Fiend to Kill!");
	    }
	
    }

    private void aimDonut(int num)
    {
	int i;
	int x,y,x2,y2;

	int a,b;

	int t[] = new int[6];
	t[0] = 27;
	t[1] = 109;
	t[2] = 191;
	t[3] = 416;
	t[4] = 492;
	t[5] = 580;
	int j = (int)(Math.random()*6);

	x = (int)(Math.random()*640);
	y = -80;

	x2 = t[j]+(int)(Math.random()*33);

	y2 = 478;

	for(i=0;i<donuts.length;i++)
	    {
		if(donuts[i].setDonut(x,y,x2,y2))
		    {
			//System.out.println("Set Donut " + i);
			i = donuts.length;
			
		    }
	    }

	a = (int) (Math.random()*3);
	b = (int) (Math.random()*2);

	switch(a)
	    {
	    case 1:
		if(b>0)
		    {
			a = -40;
			b = 40;

			if((x+b)>640)
			    {
				b = -40;
				a = -80;
			    }
			else if((x+a)<0)
			    {
				a = 40;
				b = 80;
			    }
		    }
		else
		    {
			b = -40;
			a = 40;

			if((x+a)>640)
			    {
				b =-80;
				a =-40;
			    }
			else if((x+b)<0)
			    {
				a = 80;
				b = 40;
			    }
		    }
		break;
	    case 2:
		if(b>0)
		    {
			a = -40;
			b = -80;

			if((x+a) < 0)
			    {
				b = 80;
				a = 40;
			    }
			else  if((x+b)<0)
			    {
				b = 40;
			    }
		    }
		else
		    {
			b = -40;
			a = -80;

			if((x+b) < 0)
			    {
				b = 40;
				a = 80;
			    }
			else  if((x+a)<0)
			    {
				a = 40;
			    }
		    }
		break;
	    default:
		if(b>0)
		    {
			a = 40;
			b = 80;

			if((x+b) > 640)
			    {
				b = 40;
				a = -40;
			    }
			if((x+b) > 640)
			    {
				b = -40;
				a = -80;
			    }
		    }
		else
		    {
			b = 40;
			a = 80;

			if((x+a) > 640)
			    {
				b = -40;
				a = 40;
			    }
			if((x+b) < 0)
			    {
				b = -80;
				a = -40;
			    }
		    }
		break;
	    }

	double randexpand = Math.random()+1;

	b = (int) (b*randexpand);
	a = (int) (a*randexpand);

	if(num > 1)
	    {
		for(i=0;i<donuts.length;i++)
		    {
			if(donuts[i].setDonut(x+a,y,x2+a,y2))
			    {
				//System.out.println("Set Donut " + i);
				i = donuts.length;
				
			    }
		    }
	    }
       
	if(num>2)
	    {
		for(i=0;i<donuts.length;i++)
		    {
			if(donuts[i].setDonut(x+b,y,x2+b,y2))
			    {
				//System.out.println("Set Donut " + i);
				i = donuts.length;
				
			    }
		    }
	    }


    }

    private boolean gameManager()
    {
	
	fireDelayCounter ++;

	int i,j;

	int totalLeft = singleShot + 
	    doubleShot +
	    tripleShot +
	    sniperShot +
	    sniperDouble +
	    sniperTripple;

	if(totalLeft < 1 && activeDonutCount < 1 && !theFiend.active)
	    {
		levelCountDownTimer--;

		/*******************************/
		/*  Set new level here if the  */
		/* levelCountDownTimer < 1     */
		/*******************************/

		if(levelCountDownTimer < 1)
		    {

			ceasefire = true;
			showLevelComplete();
			int jj = 0;
			int alives=0;
			for(jj=0,alives=0;jj<6;jj++)
			    {
				if(collectors[jj].alive)
				    alives++;
			    }
			ceasefire = false;

			if(alives<1)
			    {
			    return false;
			    }
			setGameLevel(gameLevel);
		    }

		return true;
	    }

	if(totalLeft<1)
	    return true;

	if(activeDonutCount > donutThreshold)
	    {
		//System.out.println("Too Many Active Donuts");
		return true;
	    }

	if(fireDelayCounter < fireDelay)
	    {
		//System.out.println("Fire Delay");
		return true;
	    }

	i = 0; /* Initialize the variable. */

	if(singleShot > 0)
	    {
		shootThis[i] = 0;
		i++;
	    }
	if(doubleShot > 0)
	    {
		shootThis[i] = 1;
		i++;
	    }
	if(tripleShot > 0)
	    {
		shootThis[i] = 2;
		i++;
	    }
	if(sniperShot > 0)
	    {
		shootThis[i] = 3;
		i++;
	    }
	if(sniperDouble > 0)
	    {
		shootThis[i] = 4;
		i++;
	    }
	if(sniperTripple > 0)
	    {
		shootThis[i] = 5;
		i++;
	    }

	j = 0;

	//System.out.println("Fire Array Size is " + i);

	if (i > 0)
	    {
		j = (int)(Math.random()*i);
		//System.out.println("Selected Fire Array Element " + j);
		i = j;
		j = shootThis[i];
		//System.out.println("Selected Fire Type " + j);
	    }

	/*
	j = 5;
	*/
	switch(j)
	    {
	    case 1:
		doubleShot--;
		fireDonut();
		fireDonut();
		break;
	    case 2:
		tripleShot--;
		fireDonut();
		fireDonut();
		fireDonut();
		break;
	    case 3:
		sniperShot--;
		aimDonut(1);
		break;
	    case 4:
		sniperDouble--;
		aimDonut(2);
		//System.out.println("Aim 2");
		break;
	    case 5:
		sniperTripple--;
		aimDonut(3);
		//System.out.println("Aim 3");
		break;
	    default:
		singleShot--;

		if(!theFiend.active && !theFiend.exploding)
		    fireTheFiend();
		else
		    fireDonut();

		break;
	    }

	fireDelayCounter = 0;
	return true;
    }

    public void paused()
    {
	if(donutcommand.PAUSED)
	    {
		while(donutcommand.PAUSED)
		    try{Thread.sleep(100);}
		    catch(Exception e){}
	    }
    }

    public boolean simpleloop()
    {
	int i;
        
	Graphics dbg;
	ceasefire = false;
	for(;;)
	    {
		Explosion.nullifyVector();
		resetSoundVars();
		aimp++;
		aimp = aimp % 2;

		if(reloadcountdown>0)
		    {
			reloadcountdown--;
			if(reloadcountdown<1 && (extraAmmo>0))
			    {
				extraAmmo--;
				shellcount = 28;
			    }
		    }


		for(i=0;i<missiles.length;i++)
		    {
			missiles[i].animateExplode();
			missiles[i].animate();
		    }


		activeDonutCount = 0;
		for(i=0;i<donuts.length;i++)
		   {
			donuts[i].animate();
			if(donuts[i].active)
			    activeDonutCount ++;
		   }

		theFiend.animate();

/*
** Insert Enemy AI/Level Manager Here
*/

		paused();


		if(!gameManager())
		    {
			/*Check win or lose*/
			return false; /*False is lose*/
		    }

		for(i=0;i<collectors.length;i++)
		    {
			collectors[i].animate();
		    }

		donutMissile.testExplosion(donuts);

		/*Test and mess with the alien here*/
		theFiend.testExplosion();

		Crumb.animateAll();

		dbg = db.getGraphics();
		drawGameScreen(dbg);
		fastpaint();
		try{Thread.sleep(donutcommand.DELAY);}
		catch(Exception e){}
		//System.out.println("Paint");
	    }
    }
    
    public void run()
    {

	while(superg == null)
	    {
		superg = getGraphics();
		try
		    {
			Thread.sleep(50);
		    }
		catch(Exception e)
		    {
		    }
		
		repaint(0);
	    }
	for(;;)
	    {
		//gameoverscreen();
		splashscreen();
		setGameLevel(0);
		simpleloop();/*game play loop*/
		gameoverscreen();
		resetGame();
	    }
    }

    /* Should only be run as a jar
    public static void main(String args[]){
	String D=new String("Welcome to 'Donut Command!'");
	
	System.out.println(D);
	
	donutcommand P = new donutcommand();
	
	Frame f = new Frame("Donut Command....");
	Dimension dim = new Dimension((640+10),(535+30));
	f.setSize(dim);
	
	f.add("Center",P);
	f.show();
	
	P.init();
	P.start();

    }
*/

}
