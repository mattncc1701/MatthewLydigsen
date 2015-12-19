import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import sun.audio.*;

import javax.imageio.ImageIO;
import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.sound.*;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.Timer;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.FPSAnimator;
import com.sun.opengl.util.GLUT;
import com.sun.opengl.util.j2d.TextRenderer;

import java.util.ArrayList;
import java.util.Random;

class FinalProject extends JFrame implements GLEventListener, KeyListener, MouseListener, MouseMotionListener, ActionListener {

	/* This defines the objModel class, which takes care
	 * of loading a triangular mesh from an obj file,
	 * estimating per vertex average normal,
	 * and displaying the mesh.
	 */
	public enum ScreenView {
		BEGINNINGSCREEN,
		LEVELSELECTION,
	    DIFFICULTY,
		COUNTDOWN,
		DUCKS,
		BALLOON,
		WATERGUN,
		ENDSCREEN,
		
	}
	
	public enum Difficulty {
		EASY,
		MEDIUM,
		HARD,
		IMPOSSIBLE,
		
	}
	
	private GLUT glut = new GLUT();
	
	class objModel {
		public FloatBuffer vertexBuffer;
		public IntBuffer faceBuffer;
		public FloatBuffer normalBuffer;
		public Point3f center;
		public int num_verts;		// number of vertices
		public int num_faces;		// number of triangle faces

		public void Draw() {
			vertexBuffer.rewind();
			normalBuffer.rewind();
			faceBuffer.rewind();
			gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
			
			gl.glVertexPointer(3, GL.GL_FLOAT, 0, vertexBuffer);
			gl.glNormalPointer(GL.GL_FLOAT, 0, normalBuffer);
			
			gl.glDrawElements(GL.GL_TRIANGLES, num_faces*3, GL.GL_UNSIGNED_INT, faceBuffer);
			
			gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
			gl.glDisableClientState(GL.GL_NORMAL_ARRAY);
		}
		
		public objModel(String filename) {
			/* load a triangular mesh model from a .obj file */
			BufferedReader in = null;
			try {
				in = new BufferedReader(new FileReader(filename));
			} catch (IOException e) {
				System.out.println("Error reading from file " + filename);
				System.exit(0);
			}

			center = new Point3f();			
			float x, y, z;
			int v1, v2, v3;
			float minx, miny, minz;
			float maxx, maxy, maxz;
			float bbx, bby, bbz;
			minx = miny = minz = 10000.f;
			maxx = maxy = maxz = -10000.f;
			
			String line;
			String[] tokens;
			ArrayList<Point3f> input_verts = new ArrayList<Point3f> ();
			ArrayList<Integer> input_faces = new ArrayList<Integer> ();
			ArrayList<Vector3f> input_norms = new ArrayList<Vector3f> ();
			try {
			while ((line = in.readLine()) != null) {
				if (line.length() == 0)
					continue;
				switch(line.charAt(0)) {
				case 'v':
					tokens = line.split("[ ]+");
					x = Float.valueOf(tokens[1]);
					y = Float.valueOf(tokens[2]);
					z = Float.valueOf(tokens[3]);
					minx = Math.min(minx, x);
					miny = Math.min(miny, y);
					minz = Math.min(minz, z);
					maxx = Math.max(maxx, x);
					maxy = Math.max(maxy, y);
					maxz = Math.max(maxz, z);
					input_verts.add(new Point3f(x, y, z));
					center.add(new Point3f(x, y, z));
					break;
				case 'f':
					tokens = line.split("[ ]+");
					v1 = Integer.valueOf(tokens[1])-1;
					v2 = Integer.valueOf(tokens[2])-1;
					v3 = Integer.valueOf(tokens[3])-1;
					input_faces.add(v1);
					input_faces.add(v2);
					input_faces.add(v3);				
					break;
				default:
					continue;
				}
			}
			in.close();	
			} catch(IOException e) {
				System.out.println("Unhandled error while reading input file.");
			}

			System.out.println("Read " + input_verts.size() +
						   	" vertices and " + input_faces.size() + " faces.");
			
			center.scale(1.f / (float) input_verts.size());
			
			bbx = maxx - minx;
			bby = maxy - miny;
			bbz = maxz - minz;
			float bbmax = Math.max(bbx, Math.max(bby, bbz));
			
			for (Point3f p : input_verts) {
				
				p.x = (p.x - center.x) / bbmax;
				p.y = (p.y - center.y) / bbmax;
				p.z = (p.z - center.z) / bbmax;
			}
			center.x = center.y = center.z = 0.f;
			
			/* estimate per vertex average normal */
			int i;
			for (i = 0; i < input_verts.size(); i ++) {
				input_norms.add(new Vector3f());
			}
			
			Vector3f e1 = new Vector3f();
			Vector3f e2 = new Vector3f();
			Vector3f tn = new Vector3f();
			for (i = 0; i < input_faces.size(); i += 3) {
				v1 = input_faces.get(i+0);
				v2 = input_faces.get(i+1);
				v3 = input_faces.get(i+2);
				
				e1.sub(input_verts.get(v2), input_verts.get(v1));
				e2.sub(input_verts.get(v3), input_verts.get(v1));
				tn.cross(e1, e2);
				input_norms.get(v1).add(tn);
				
				e1.sub(input_verts.get(v3), input_verts.get(v2));
				e2.sub(input_verts.get(v1), input_verts.get(v2));
				tn.cross(e1, e2);
				input_norms.get(v2).add(tn);
				
				e1.sub(input_verts.get(v1), input_verts.get(v3));
				e2.sub(input_verts.get(v2), input_verts.get(v3));
				tn.cross(e1, e2);
				input_norms.get(v3).add(tn);			
			}

			/* convert to buffers to improve display speed */
			for (i = 0; i < input_verts.size(); i ++) {
				input_norms.get(i).normalize();
			}
			
			vertexBuffer = BufferUtil.newFloatBuffer(input_verts.size()*3);
			normalBuffer = BufferUtil.newFloatBuffer(input_verts.size()*3);
			faceBuffer = BufferUtil.newIntBuffer(input_faces.size());
			
			for (i = 0; i < input_verts.size(); i ++) {
				vertexBuffer.put(input_verts.get(i).x);
				vertexBuffer.put(input_verts.get(i).y);
				vertexBuffer.put(input_verts.get(i).z);
				normalBuffer.put(input_norms.get(i).x);
				normalBuffer.put(input_norms.get(i).y);
				normalBuffer.put(input_norms.get(i).z);			
			}
			
			for (i = 0; i < input_faces.size(); i ++) {
				faceBuffer.put(input_faces.get(i));	
			}			
			num_verts = input_verts.size();
			num_faces = input_faces.size()/3;
		}		
	}


	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
		case KeyEvent.VK_Q:
			currentView = ScreenView.BEGINNINGSCREEN;
			break;		
		case 'r':
		case 'R':
			initViewParameters();
			break;
		case 'w':
		case 'W':
			wireframe = ! wireframe;
			break;
		case 'b':
		case 'B':
			cullface = !cullface;
			break;
		case 'f':
		case 'F':
			flatshade = !flatshade;
			break;
		case 'a':
		case 'A':
			if (animator.isAnimating())
				animator.stop();
			else 
				animator.start();
			break;
		case '+':
		case '=':
			animation_speed *= 1.2f;
			break;
		case '-':
		case '_':
			animation_speed /= 1.2;
			break;
		default:
			break;
		}
		canvas.display();
	}
	
	/* GL, display, model transformation, and mouse control variables */
	private final GLCanvas canvas;
	private GL gl;
	private final GLU glu = new GLU();	
	private FPSAnimator animator;

	private int winW = 800, winH = 800;
	private boolean wireframe = false;
	private boolean cullface = true;
	private boolean flatshade = false;
	
	private float xpos = 0, ypos = 0, zpos = 0;
	private float centerx, centery, centerz;
	private float roth = 0, rotv = 30;
	private float znear, zfar;
	private double mouseX, mouseY, mouseButton;
	private float motionSpeed, rotateSpeed;
	private float animation_speed = 1.0f;
	private float counter = 0;
	private float speed = 1;
	
	AudioStream stream = null;
	
	private objModel bird = new objModel("bird.obj");
	Random randomPrize = new Random();
	boolean onlyOnePrize = false;
	private objModel prize;
	double newPrize = 0;
	
	private float example_rotateT = 0.f;
	
	boolean mouseClick = false;
	boolean mousePressed = false;
	
	private float xmin = -1f, ymin = -1f, zmin = -1f;
	private float xmax = 1f, ymax = 1f, zmax = 1f;
	
	float currentMouseX = 0;
	float currentMouseY = 0;
	float move = 0;
	
	float lastMouseX = 0;
	float lastMouseY = 0;
	float lastMouseZ = 0;
	
	boolean ducksKilled[] = {false, false, false, false, false, false, false, false, false, false};
	

	boolean ducksKilledSmall[] = {false, false, false, false, false,false};
	
	ScreenView currentView = ScreenView.BEGINNINGSCREEN;
	ScreenView selectedLevel = ScreenView.BEGINNINGSCREEN;
	ScreenView previousView = ScreenView.BEGINNINGSCREEN;
    Font font = new Font("Cooper Black", Font.BOLD, 50);
    TextRenderer renderer = new TextRenderer(font);

    Difficulty currentDifficulty = Difficulty.MEDIUM;
    
	boolean switchDirection = false;
	boolean switchDirectionSmall = false;
	boolean switchBackgroundColors = true;
	boolean caluculateBackgroundColors = true;
	boolean soundOff = false;
	boolean play = true;
	boolean bonusScore = false;
	
	long timerStart = 0;
	long timerEnd = 0;
	long switchBackgroundTimer = 0;
	long waterGunTimer = 0;
	
	int score = 0;
	int bonus  = 0;
	boolean allDucksKilled = false;
	
	float[] balloonPosX;
	float[] balloonPosY = new float[50];
	boolean[] balloonPopped = new boolean[50];
	float[][] balloonColor = new float[50][4];
	
	BufferedImage background;
	int width = 0, height = 0;
	byte[] srcRed = null;
	byte[] srcBlue = null;
	byte[] srcGreen = null;
	// a buffer that stores the destination image pixels
	
	Random randomTargetPosition = new Random();
	
	float currentRandomXPosition = 0;
	float currentRandomYPosition = 0;
	
	float waterScore = 0;
	
	public void display(GLAutoDrawable drawable) {
		//set background color to light blue
		float mat_diffuse[] = { 1f, 1f, 0f, 1 };
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glClearColor(0,.7f,1f,1);
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, wireframe ? GL.GL_LINE : GL.GL_FILL);	
		gl.glShadeModel(flatshade ? GL.GL_FLAT : GL.GL_SMOOTH);		
		if (cullface)
			gl.glEnable(GL.GL_CULL_FACE);
		else
			gl.glDisable(GL.GL_CULL_FACE);		
		
		gl.glLoadIdentity();
		
		/* this is the transformation of the entire scene */
		gl.glTranslatef(-xpos, -ypos, -zpos);
		gl.glTranslatef(centerx, centery, centerz);
		gl.glRotatef(360.f - roth, 0, 1.0f, 0);
		gl.glRotatef(rotv, 1.0f, 0, 0);
		gl.glTranslatef(-centerx, -centery, -centerz);		
		
	    gl.glDisable(GL.GL_LIGHTING);
		int mod = 0;
		/*this sets up the rest of the background which is the red and white semi speheres on the top and bottom of the screen*/ 
	   for(int j = 0; j < 10; j++){
		   
			gl.glPushMatrix();
			/*this timer is used for switching the background circles every .3 seconds*/
			if(switchBackgroundColors == true ){
				if(switchBackgroundTimer == 0){
					switchBackgroundTimer = (long)((System.currentTimeMillis()/100)+3);
				}
				mod = 1;
			}
			else{
				if(switchBackgroundTimer == 0){
					switchBackgroundTimer = (long)((System.currentTimeMillis()/100)+3);
				}
				mod = 0;
			}
			//this is for every other view in the game
			if(currentView != ScreenView.BEGINNINGSCREEN){
				mod = 0;
			}
			   if((j+mod)%2 == 0){
				   renderer.setColor(1.0f, 0.2f, 0.2f, 0.8f);
				   renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
				   renderer.endRendering();
			   }
			   else{
				   renderer.setColor(1.0f, 1f, 1f, 1f);
				   renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
				   renderer.endRendering();
			   }
			double angle = 2*  Math.PI/100 ;
		   gl.glPolygonMode(gl.GL_FRONT, gl.GL_FILL );
		   //gl.glColor3f(0.2f, 0.5f, 0.5f);

		   gl.glTranslatef((float)(-.9 + .2*j), 1f, 0);
		   gl.glBegin(gl.GL_POLYGON);
		       double angle1=0.0;
		       gl.glVertex2d( .1f * Math.cos(0.0) , .1 * Math.sin(0.0));
		       int i;
		       for ( i=0 ; i< 100;i++)
		       {
		           gl.glVertex2d(.1f * Math.cos(angle1), .1f *Math.sin(angle1));
		           angle1 += angle ;
		       }
		    
		   gl.glEnd();
		   gl.glPopMatrix();
	   }
	   
       //this is for the bottom screen circles
	   for(int j = 0; j < 10; j++){
		   

		gl.glPushMatrix();
			   if((j+mod)%2 == 0){
				   renderer.setColor(1.0f, 0.2f, 0.2f, 0.8f);
				   renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
				   renderer.endRendering();
			   }
			   else{
				   renderer.setColor(1.0f, 1f, 1f, 1f);
				   renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
				   renderer.endRendering();
			   }
			   
		   double angle = 2*  Math.PI/100 ;
		   gl.glPolygonMode(gl.GL_FRONT, gl.GL_FILL );
		   //gl.glColor3f(0.2f, 0.5f, 0.5f);

		   gl.glTranslatef((float)(-.9 + .2*j), -1f, 0);
		   gl.glBegin(gl.GL_POLYGON);
		       double angle1=0.0;
		       gl.glVertex2d( .1f * Math.cos(0.0) , .1 * Math.sin(0.0));
		       int i;
		       for ( i=0 ; i< 100;i++)
		       {

		           gl.glVertex2d(.1f * Math.cos(angle1), .1f *Math.sin(angle1));
		           angle1 += angle ;
		       }
		    
		   gl.glEnd();
		   gl.glPopMatrix();
	   }
	   if(switchBackgroundColors == true && switchBackgroundTimer <= (System.currentTimeMillis()/100)){
		   switchBackgroundColors = false;
		   switchBackgroundTimer = 0;
	   }
	   else if(switchBackgroundColors == false && switchBackgroundTimer <= (System.currentTimeMillis()/100)){
		   switchBackgroundColors = true;
		   switchBackgroundTimer = 0;
	   }
		gl.glEnable( GL.GL_LIGHTING );
	    gl.glEnable( GL.GL_LIGHT0 );
	    gl.glEnable( GL.GL_LIGHT1 );
	    gl.glEnable( GL.GL_LIGHT2 );
		
	    
		   mat_diffuse [0] = 1f;
		   mat_diffuse [1] = 1f;
		   mat_diffuse [2] = 0f;
		   mat_diffuse [3] = 1f;
		   gl.glMaterialfv( GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse, 0);  
		
		   /*here is where we transition from one screen to the next based off of the currentView value*/   
		if(currentView ==  ScreenView.BEGINNINGSCREEN){
			beginscreen(drawable);
			
		}
		
		if(currentView ==  ScreenView.COUNTDOWN){
			 countdown(drawable);
		}		
		
		if(currentView == ScreenView.DIFFICULTY){
			difficulty(drawable);
		}
			
		if(currentView ==  ScreenView.ENDSCREEN){
			endscreen(drawable);
		}
	
		//balloon game
		if(currentView == ScreenView.BALLOON){
			balloon(drawable);			
		}
		
		//water game
		else if(currentView ==  ScreenView.WATERGUN){
			watergun(drawable);
		}	
		
		//duck game
		else if(currentView == ScreenView.DUCKS){
			ducks(drawable);
		}
		
		
		/* increment example_rotateT */
		if (animator.isAnimating())
			example_rotateT += 1.0f * animation_speed;
	}	

	/*this method is for the difficulty selection*/
	    public void difficulty(GLAutoDrawable drawable){
			gl.glDisable(gl.GL_LIGHTING);		
			/*the code below is used to draw the selection boxes and make them change color when the mouse hovers over them*/
			   if(currentMouseX > 200 && currentMouseX < 600 && currentMouseY > 110 && currentMouseY < 225){
				   if(mouseClick == true){
					   /*when one of the boxes has been clicked we set the difficulty and then go to the countdown screen*/
					   currentDifficulty = Difficulty.EASY;
					   speed = .5f;
					  currentView = ScreenView.COUNTDOWN;
					  timerStart = System.currentTimeMillis()/1000;
					  timerEnd = (timerStart + 3);
					  mouseClick = false;
				   }
				   else{
					      gl.glColor3f(.8f,.8f,.8f);
						  gl.glRectf(.5f, .7f, -.5f, .4f);
				   }
			   }
			   else{
				   gl.glColor3f(.85f,.85f,.85f);
				   gl.glRectf(.5f, .7f, -.5f, .4f);

			   }
			   
			   if(currentMouseX > 200 && currentMouseX < 600 && currentMouseY > 255 && currentMouseY < 375){
				   if(mouseClick == true){
					   speed = 1f;
					   currentDifficulty = Difficulty.MEDIUM;
					  currentView = ScreenView.COUNTDOWN; 
					  timerStart = System.currentTimeMillis()/1000;
					  timerEnd = (timerStart + 3);
					  
					  mouseClick = false;
				   }
				   else{
					   	  gl.glColor3f(.8f,.8f,.8f);
						  gl.glRectf(.5f, .3f, -.5f, 0f);
				   }
			   }
			   else{
				   gl.glColor3f(.85f,.85f,.85f);
				   gl.glRectf(.5f, .3f, -.5f, 0f);
			   }
			   if(currentMouseX > 200 && currentMouseX < 600 && currentMouseY > 415 && currentMouseY < 530){
				   if(mouseClick == true){
					   currentDifficulty = Difficulty.HARD;
					   speed = 1.5f;
					  currentView = ScreenView.COUNTDOWN;
					  timerStart = System.currentTimeMillis()/1000;
					  timerEnd = (timerStart + 3);

					  
				   }
				   else{
					       gl.glColor3f(.8f,.8f,.8f);
						   gl.glRectf(.5f, -.1f, -.5f, -.4f);
				   }
			   }
			   else{
				   gl.glColor3f(.85f,.85f,.85f);
				   gl.glRectf(.5f, -.1f, -.5f, -.4f);
			   }
			   if(currentMouseX > 200 && currentMouseX < 600 && currentMouseY > 560 && currentMouseY < 680){
				   if(mouseClick == true){
					   currentDifficulty = Difficulty.IMPOSSIBLE;
					   speed = 2f;
					  currentView = ScreenView.COUNTDOWN;
					  timerStart = System.currentTimeMillis()/1000;
					  timerEnd = (timerStart + 3);

					  
				   }
				   else{
					       gl.glColor3f(.8f,.8f,.8f);
						   gl.glRectf(.5f, -.5f, -.5f, -.8f);
				   }
			   }
			   else{
				   gl.glColor3f(.85f,.85f,.85f);
				   gl.glRectf(.5f, -.5f, -.5f, -.8f);
			   }
				gl.glEnable(gl.GL_LIGHTING);
			   mouseClick = false;
			  
			   /*this code renders the text on the screen*/
			   font = new Font("Cooper Black", Font.BOLD, 47);
			   renderer = new TextRenderer(font);
		
			   renderer.setColor(1.0f, 0.2f, 0.2f, 0.8f);
			   renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
			   renderer.draw("Easy", 320, 570);
			   renderer.draw("Medium", 280, 420);
			   renderer.draw("Hard", 320, 270);
			   renderer.draw("Impossible", 250, 120);
			   renderer.endRendering();
	    }
	
	    /*this screen provides the countdown before any of the games start*/
		public void countdown(GLAutoDrawable drawable){
			//this code is used for hiding the cursor
			Toolkit t = Toolkit.getDefaultToolkit();
			 Image imageTemp = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
			Cursor noCursor = t.createCustomCursor(imageTemp, new Point(0, 0), "none");
			canvas.setCursor(noCursor);
			
			//this code draws all text on the screen
			font = new Font("Cooper Black", Font.BOLD, 50);
			renderer = new TextRenderer(font);
			renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
			renderer.setColor(1.0f, 0.2f, 0.2f, 0.8f);
			renderer.draw("Game Begins in:", 170, 500);
			renderer.endRendering();
			font = new Font("Cooper Black", Font.BOLD, 200);
			renderer = new TextRenderer(font);
			renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
			renderer.setColor(1.0f, 0.2f, 0.2f, 0.8f);
			renderer.draw(""+(timerEnd - System.currentTimeMillis()/1000), 340, 320);
			renderer.endRendering();
			
			/*this code checks the timer and if it has expired starts the game*/
			if((timerEnd - System.currentTimeMillis()/1000) <= 0 && selectedLevel == ScreenView.DUCKS){
				currentView = ScreenView.DUCKS;
				  timerStart = System.currentTimeMillis()/1000;
				  timerEnd = (timerStart + 30);
			}
			if((timerEnd - System.currentTimeMillis()/1000) <= 0 && selectedLevel == ScreenView.BALLOON){
				currentView = ScreenView.BALLOON;
				  timerStart = System.currentTimeMillis()/1000;
				  timerEnd = (timerStart + 30);
			}
			if((timerEnd - System.currentTimeMillis()/1000) <= 0 && selectedLevel == ScreenView.WATERGUN){
				currentView = ScreenView.WATERGUN;
				  timerStart = System.currentTimeMillis()/1000;
				  timerEnd = (timerStart + 30);
			}
			
		}
	
		/*this is the start screen of the game*/
		public void beginscreen(GLAutoDrawable drawable){
			canvas.setCursor(Cursor.getDefaultCursor());/*this line restores the cursor*/
			if(play){/*this code plays the opening music*/
				sound("theme.wav");
				play = false;
			}
			/*the code below restores the initial values of the games so that they can be played again*/
			for(int i = 0; i<50; i++){
				balloonPopped[i]=false;
				balloonPosY[i] = 0;
			}
			counter = 0;
			for(int i = 0; i < ducksKilled.length; i++){
				ducksKilled[i] = false;
			}
			
			for(int i = 0; i < ducksKilledSmall.length; i++){
				ducksKilledSmall[i] = false;
			}
			
			score = 0;
			bonus  = 0;
			allDucksKilled = false;
			/*this code below is used to draw the selection boxes for each level*/
			gl.glDisable(gl.GL_LIGHTING);			   
			   if(currentMouseX > 200 && currentMouseX < 600 && currentMouseY > 280 && currentMouseY < 395){
				   if(mouseClick == true){
					  currentView = ScreenView.DIFFICULTY;
					  selectedLevel = ScreenView.DUCKS;
					  timerStart = System.currentTimeMillis()/1000;
					  timerEnd = (timerStart + 3);
					  mouseClick = false;
				   }
				   else{
					      gl.glColor3f(.8f,.8f,.8f);
						  gl.glRectf(.5f, .25f, -.5f, -.05f);
				   }
			   }
			   else{
				   gl.glColor3f(.85f,.85f,.85f);
				   gl.glRectf(.5f, .25f, -.5f, -.05f);

			   }
			   
			   if(currentMouseX > 200 && currentMouseX < 600 && currentMouseY > 415 && currentMouseY < 535){
				   if(mouseClick == true){
					  currentView = ScreenView.DIFFICULTY; 
					  selectedLevel = ScreenView.BALLOON;
					  timerStart = System.currentTimeMillis()/1000;
					  timerEnd = (timerStart + 3);
					  
					  mouseClick = false;
				   }
				   else{
					   	  gl.glColor3f(.8f,.8f,.8f);
						  gl.glRectf(.5f, -.1f, -.5f, -.4f);
				   }
			   }
			   else{
				   gl.glColor3f(.85f,.85f,.85f);
				   gl.glRectf(.5f, -.1f, -.5f, -.4f);
			   }
			   if(currentMouseX > 200 && currentMouseX < 600 && currentMouseY > 540 && currentMouseY < 660){
				   if(mouseClick == true){
					  currentView = ScreenView.COUNTDOWN;
					  selectedLevel = ScreenView.WATERGUN;
					  timerStart = System.currentTimeMillis()/1000;
					  timerEnd = (timerStart + 3);

					  
				   }
				   else{
					       gl.glColor3f(.8f,.8f,.8f);
						   gl.glRectf(.5f, -.45f, -.5f, -.75f);
				   }
			   }
			   else{
				   gl.glColor3f(.85f,.85f,.85f);
				   gl.glRectf(.5f, -.45f, -.5f, -.75f);
			   }
				gl.glEnable(gl.GL_LIGHTING);
				
				/*this code renders the text on the screen*/
			   mouseClick = false;
			   font = new Font("Cooper Black", Font.BOLD, 47);
			   renderer = new TextRenderer(font);
			   renderer.setColor(1.0f, 0.2f, 0.2f, 0.8f);
			   renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
			   renderer.draw("Welcome to Carnival Games", 20, 550);
			   renderer.endRendering();
			   
			   font = new Font("Cooper Black", Font.BOLD, 35);
			   renderer = new TextRenderer(font);
		
			   renderer.setColor(1.0f, 0.2f, 0.2f, 0.8f);
			   renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
			   renderer.draw("Bird Shooter", 265, 400);
			   renderer.draw("Balloon Popper", 240, 270);
			   renderer.draw("Water Shooter", 250, 140);
			   renderer.endRendering();
				waterScore = 0;
		}
	
		/*this is one of the games of the three and it is the balloon popper*/
		public void balloon(GLAutoDrawable drawable){

			previousView = ScreenView.BALLOON;
			float mat_diffuse[] = { 1f, 1f, 0f, 1 };
			/*this will give random colors to the balloons*/
			if(balloonPosX==null){
				balloonPosX = new float[50];
				for(int i = 0; i<50; i++){
					double x = Math.random()*2;
					if(x<1) balloonPosX[i] = (float)-x;
					else balloonPosX[i] = (float)x-1;
					balloonColor[i][0] = (float)Math.random();
					balloonColor[i][1] = (float)Math.random();
					balloonColor[i][2] = (float)Math.random();
					balloonColor[i][3] = 1;
					
				}
			}
			//this renders the score text
			font = new Font("Cooper Black", Font.BOLD, 50);
			renderer = new TextRenderer(font);
			renderer.setColor(1.0f, 0.2f, 0.2f, 0.8f);
			renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
			renderer.draw(""+(timerEnd - System.currentTimeMillis()/1000), 700, 660);
			renderer.draw("Score: " + score, 30, 660);
			renderer.endRendering();
			
			if((timerEnd - System.currentTimeMillis()/1000) <= 0){
				currentView = ScreenView.ENDSCREEN;
			}
		    //this code handles the cross hairs
		    gl.glDisable( GL.GL_LIGHTING );
		    gl.glDisable( GL.GL_LIGHT0 );
		    gl.glDisable( GL.GL_LIGHT1 );
		    gl.glDisable( GL.GL_LIGHT2 );
		    
			gl.glPushMatrix();	// push the current matrix to stack
			mat_diffuse[0] = 255/255f;
			mat_diffuse[1] = 215/255f;
			mat_diffuse[2] = 0/255f;
			mat_diffuse[3] = 1f;
		    gl.glMaterialfv( GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse, 0);
			float x_pos = (2.0f * currentMouseX) / winW - 1.0f;
			float y_pos = 1.0f - (2.0f * currentMouseY) / winH;
			float z_pos = 1f;
			
			if(mouseClick == false){
				lastMouseX = x_pos;
				lastMouseY = y_pos;
				lastMouseZ = z_pos;
			}
			gl.glTranslatef(x_pos, y_pos, z_pos);
			
			//this draws the lines in the crosshairs
			gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(0,0.065f,0f);
				gl.glVertex3f(0,-.065f,0f);
				gl.glEnd();
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(-.065f,0f,0f);
				gl.glVertex3f(.065f,0f,0f);
				gl.glEnd();
	
		    //this draws the circle
		    gl.glScalef(.2f,.2f,.2f);
			gl.glBegin(GL.GL_LINE_LOOP);
			for(int i =0; i <= 300; i++){
			double angle = 2 * Math.PI * i / 300;
			double x = Math.cos(angle)/3;
			double y = Math.sin(angle)/3;
			gl.glVertex2d(x,y);
			}
			gl.glEnd();
			
			gl.glEnable( GL.GL_LIGHTING );
		    gl.glEnable( GL.GL_LIGHT0 );
		    gl.glEnable( GL.GL_LIGHT1 );
		    gl.glEnable( GL.GL_LIGHT2 );
	
			gl.glPopMatrix();
			
			
			
			
			//this is used for calculating the trajectory of the dart
			double h = Math.sqrt((Math.pow(lastMouseX, 2)+Math.pow((lastMouseY+.85), 2)));
	    	float angleOfRotate = (float)((float) (lastMouseY+.85)/Math.sqrt((Math.pow(lastMouseX, 2)+Math.pow((lastMouseY+.85), 2))));
	    	float launchAngle = (float)((float) (h)/Math.sqrt((Math.pow(h, 2)+Math.pow(1, 2))));
	    	launchAngle = (float) ((float) Math.acos(launchAngle)*(180/Math.PI));	
	    	if(lastMouseX < 0){
	    		angleOfRotate = (float) ((float) -Math.acos(angleOfRotate)*(180/Math.PI));
	    	}
	    	else{
	    		angleOfRotate = (float) ((float) Math.acos(angleOfRotate)*(180/Math.PI));	
	    	}

			if(!mouseClick){
				gl.glPushMatrix();
				//gl.glScalef(1f, .5f, .5f);

				gl.glTranslatef(0,-.85f,3);
		    	gl.glRotatef(-90, 1, 0 ,0);
		    	gl.glRotatef(angleOfRotate,0,1,0);
		    	gl.glRotatef(-launchAngle,1,0,0);
				glut.glutSolidCone(.05,.35,32,32);
				gl.glPopMatrix();
			
			}
			//Draw dart
			if(mouseClick){
				gl.glPushMatrix();
				gl.glTranslatef(0,-.85f,3);

				//this moves the dart
				gl.glTranslatef((lastMouseX/20)*move,((float)(lastMouseY+0.85)/20)*move,0);

		    	gl.glRotatef(-90, 1, 0 ,0);
				gl.glRotatef(angleOfRotate,0,1,0);
				gl.glRotatef(-launchAngle,1,0,0);
				gl.glScalef(1f-move/50, 1f-move/50, 1f-move/50);
				glut.glutSolidCone(.05, .35, 32, 32);
				move = (float) (move + 2);
				//this is the collision detection
				if(move > 20){
					for(int i = 0; i<50; i++){
						if(lastMouseX>(balloonPosX[i]-0.15) && lastMouseX<(balloonPosX[i]+0.15) && lastMouseY>(((balloonPosY[i]/80)-0.85)-0.15) && lastMouseY<(((balloonPosY[i]/80)-0.85)+0.15) 
								&& balloonPopped[i] == false && balloonPosY[i] < 100){
							balloonPopped[i] = true;
							sound("balloon.wav");
							score+=10;
						}
					}
					move = 0;
					mouseClick = false;
				}
				gl.glPopMatrix();		
			}
			
			//draw balloons
			for(int i = 0; i<50; i++){
				if(counter>=(i*20)){
					if(!balloonPopped[i] && balloonPosY[i] < 100)
					{
						gl.glMaterialfv( GL.GL_FRONT, GL.GL_DIFFUSE, balloonColor[i], 0);
						gl.glPushMatrix();
						gl.glTranslatef(balloonPosX[i], -0.70f, 0);
						if(currentDifficulty == Difficulty.EASY){
							gl.glTranslatef(0, (float)(balloonPosY[i]/80), 0);
							balloonPosY[i]++;
						}
						else if(currentDifficulty == Difficulty.MEDIUM){//mbl & mel 53v@
							gl.glTranslatef((float)Math.cos((((2*Math.PI)/40)*counter)+i)/10, (float)(balloonPosY[i]/80), 0);
							balloonPosY[i]++;
						}
						else if(currentDifficulty == Difficulty.HARD){
							gl.glTranslatef((float)Math.cos((((2*Math.PI)/40)*counter)+i)/10, (float)(balloonPosY[i]/80), 0);
							balloonPosY[i]+=2;
						}
						else if(currentDifficulty == Difficulty.IMPOSSIBLE){
							gl.glTranslatef((float)Math.cos((((2*Math.PI)/40)*counter*2)+i)/5, (float)(balloonPosY[i]/80), 0);
							balloonPosY[i]+=2;
						}
						glut.glutSolidSphere(.1, 50, 50);
						gl.glPopMatrix();
						
					}
				}
			}
			counter++;
			
		
		}

		//this handles the watergun game
		public void watergun(GLAutoDrawable drawable){
			onlyOnePrize = false;
			//this sets the color of the gun
			float mat_diffuse[] = { 1f, 1f, 0f, 1 };
			mat_diffuse[0] = .93359f;
			mat_diffuse[1] = .16406f;
			mat_diffuse[2] = .55078f;
			mat_diffuse[3] = 1f;
		    gl.glMaterialfv( GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse, 0);
		    gl.glPushMatrix();
				//gl.glScalef(1f, .5f, .5f);

		    	//this puts the gun in the correct location on the screen
	    		gl.glTranslatef(0,-.85f,3);
		    	gl.glRotatef(-90, 1, 0 ,0);
		    	
		    	//this is used to rotate the gun based off of the mouse
		    	float angleOfRotate = (float) ((float) (lastMouseY+.85)/Math.sqrt((Math.pow(lastMouseX, 2)+Math.pow((lastMouseY+.85), 2))));
		    	double h = Math.sqrt((Math.pow(lastMouseX, 2)+Math.pow((lastMouseY+.85), 2)));
		    	float launchAngle = (float)((float) (h)/Math.sqrt((Math.pow(h, 2)+Math.pow(1, 2))));
		    	launchAngle = (float) ((float) Math.acos(launchAngle)*(180/Math.PI));
		    	if(lastMouseX < 0){
		    		angleOfRotate = (float) ((float) -Math.acos(angleOfRotate)*(180/Math.PI));
		    	}
		    	else{
		    		angleOfRotate = (float) ((float) Math.acos(angleOfRotate)*(180/Math.PI));		    		
		    	}
		    	gl.glRotatef(angleOfRotate,0,1,0);
		    	gl.glRotatef(-launchAngle,1,0,0);
				glut.glutSolidCone(.05,.35,32,32);
			gl.glPopMatrix();
			
			/*the code below is used for drawing the target and having it move randomly on the screen*/
			gl.glPushMatrix();
				float randomSwitch = randomTargetPosition.nextFloat();
				if(randomSwitch > .5f && (waterGunTimer - System.currentTimeMillis()) <= 0){
					waterGunTimer = (long) (System.currentTimeMillis() + 1250);
					currentRandomXPosition = (randomTargetPosition.nextFloat() - .5f)*1.5f;
					currentRandomYPosition = randomTargetPosition.nextFloat() - .6f;
					if(currentRandomYPosition < -.35){
						currentRandomYPosition = -.35f;
					}
				}
				gl.glTranslatef(currentRandomXPosition, currentRandomYPosition, 0);
				double angleWater = 2*  Math.PI/100 ;
			    gl.glDisable(GL.GL_LIGHTING);	
				gl.glTranslatef(0,0,1);
			    renderer.setColor(1.0f, .2f, .2f, 1f);
			    renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
			    renderer.endRendering();
			    //the code below draws each circle in the target on top of one another
			   gl.glPolygonMode(gl.GL_FRONT, gl.GL_FILL );
			   gl.glBegin(gl.GL_POLYGON);
			       double angle1=0.0;
			       gl.glVertex2d( .165f * Math.cos(0.0) , .165 * Math.sin(0.0));
			       for (int j=0 ; j< 100;j++)
			       {
			           gl.glVertex2d(.165f * Math.cos(angle1), .165f *Math.sin(angle1));
			           angle1 += angleWater;
			       }
				    
				gl.glEnd();
			    renderer.setColor(1.0f, 1f, 1f, 1f);
			    renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
			    renderer.endRendering();
			    gl.glTranslatef(0,0,.01f);
			   gl.glPolygonMode(gl.GL_FRONT, gl.GL_FILL );
			   gl.glBegin(gl.GL_POLYGON);
			       angle1=0.0;
			       gl.glVertex2d( .13f * Math.cos(0.0) , .13 * Math.sin(0.0));
			       for (int j=0 ; j< 100;j++)
			       {
			           gl.glVertex2d(.13f * Math.cos(angle1), .13f *Math.sin(angle1));
			           angle1 += angleWater;
			       }
				    
				gl.glEnd();
				renderer.setColor(1.0f, .2f, .2f, 1f);
			    renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
			    renderer.endRendering();
			    gl.glTranslatef(0,0,.01f);
			   gl.glPolygonMode(gl.GL_FRONT, gl.GL_FILL );
			   gl.glBegin(gl.GL_POLYGON);
			       angle1=0.0;
			       gl.glVertex2d( .095f * Math.cos(0.0) , .095 * Math.sin(0.0));
			       for (int j=0 ; j< 100;j++)
			       {
			           gl.glVertex2d(.095f * Math.cos(angle1), .095f *Math.sin(angle1));
			           angle1 += angleWater;
			       }
				    
				gl.glEnd();
				renderer.setColor(1.0f, 1f, 1f, 1f);
			    renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
			    renderer.endRendering();
			    gl.glTranslatef(0,0,.01f);
			   gl.glPolygonMode(gl.GL_FRONT, gl.GL_FILL );
			   gl.glBegin(gl.GL_POLYGON);
			       angle1=0.0;
			       gl.glVertex2d( .06f * Math.cos(0.0) , .06 * Math.sin(0.0));
			       for (int j=0 ; j< 100;j++)
			       {
			           gl.glVertex2d(.06f * Math.cos(angle1), .06f *Math.sin(angle1));
			           angle1 += angleWater;
			       }
				    
				gl.glEnd();
				renderer.setColor(1.0f, .2f, .2f, 1f);
			    renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
			    renderer.endRendering();
			    gl.glTranslatef(0,0,.01f);
			   gl.glPolygonMode(gl.GL_FRONT, gl.GL_FILL );
			   gl.glBegin(gl.GL_POLYGON);
			       angle1=0.0;
			       gl.glVertex2d( .025f * Math.cos(0.0) , .025 * Math.sin(0.0));
			       for (int j=0 ; j< 100;j++)
			       {
			           gl.glVertex2d(.025f * Math.cos(angle1), .025f *Math.sin(angle1));
			           angle1 += angleWater;
			       }
				    
				gl.glEnd();
				gl.glEnable(GL.GL_LIGHTING);
				gl.glTranslatef(0,0,-.01f);
				gl.glTranslatef(0,0,-.01f);
				gl.glTranslatef(0,0,-.01f);
				gl.glTranslatef(0,0,-1);	
			gl.glPopMatrix();//end drawing of target
			
			//this draws the timer text
			font = new Font("Cooper Black", Font.BOLD, 50);
			renderer = new TextRenderer(font);
			renderer.setColor(1.0f, 0.2f, 0.2f, 0.8f);
			renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
			renderer.draw(""+(timerEnd - System.currentTimeMillis()/1000), 700, 650);
			renderer.endRendering();
			if((timerEnd - System.currentTimeMillis()/1000) <= 0){
		    	AudioPlayer.player.stop(stream);
				currentView = ScreenView.ENDSCREEN;
			}
		
		    gl.glDisable( GL.GL_LIGHTING );
		    gl.glDisable( GL.GL_LIGHT0 );
		    gl.glDisable( GL.GL_LIGHT1 );
		    gl.glDisable( GL.GL_LIGHT2 );
		    
			gl.glPushMatrix();
				gl.glScalef(.3f, .3f, .3f);
				gl.glTranslatef(-3.3f, 3.3f, 0);
			gl.glPopMatrix();
			//this draws the crosshairs
			gl.glPushMatrix();	// push the current matrix to stack
			mat_diffuse[0] = .93359f;
			mat_diffuse[1] = .16406f;
			mat_diffuse[2] = .55078f;
			mat_diffuse[3] = 1f;
		    gl.glMaterialfv( GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse, 0);
			float x_pos = (2.0f * currentMouseX) / winW - 1.0f;
			float y_pos = 1.0f - (2.0f * currentMouseY) / winH;
			float z_pos = 2f;
			
				lastMouseX = x_pos;
				lastMouseY = y_pos;
				lastMouseZ = z_pos;
				gl.glTranslatef(x_pos, y_pos, z_pos);
			    renderer.setColor(0.0f, 0f, 0f, 1f);
			    renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
			    renderer.endRendering();			
		    gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(0,0.065f,0f);
				gl.glVertex3f(0,-.065f,0f);
				gl.glEnd();
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(-.065f,0f,0f);
				gl.glVertex3f(.065f,0f,0f);
	        gl.glEnd();
	
		    
		    gl.glScalef(.2f,.2f,.2f);
			gl.glBegin(GL.GL_LINE_LOOP);
			for(int i =0; i <= 300; i++){
			double angle = 2 * Math.PI * i / 300;
			double x = Math.cos(angle)/3;
			double y = Math.sin(angle)/3;
			gl.glVertex2d(x,y);
			}
			gl.glEnd();
	
			gl.glPopMatrix();
			//check to see if mouse click corresponds to target
		    gl.glPushMatrix();
		    renderer.setColor(.93359f, .16406f, .55078f, 1f);
		    renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
		    renderer.endRendering();	
		    gl.glPolygonMode(gl.GL_FRONT, gl.GL_FILL );

		       gl.glTranslatef(0, -.85f, 3);
			gl.glBegin(gl.GL_POLYGON);
			   double angle = 2*  Math.PI/100 ;
		       double angle2=0.0;
		       gl.glVertex2d( .05f * Math.cos(0.0) , .05 * Math.sin(0.0));
		       for (int i = 0; i < 100; i++)
		       {
		           gl.glVertex2d(.05f * Math.cos(angle2), .05f *Math.sin(angle2));
		           angle2 += angle;
		       }
		    
		   gl.glEnd();
		   gl.glPopMatrix();
		    renderer.setColor(0,0,0, 1f);
		    renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
		    renderer.endRendering();	
			/*water*/
			if(mousePressed){
				//this determines if you are hitting the target
				if(lastMouseX>currentRandomXPosition-.155 && lastMouseX<currentRandomXPosition+.155 &&
						lastMouseY>currentRandomYPosition-.155 && lastMouseY<currentRandomYPosition+.155){
					waterScore += .005;
				}
				else{
					if(waterScore > 0){
						waterScore -= .003;
					}
				}
				gl.glPushMatrix();
				gl.glTranslatef(0, 0, 2);
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(lastMouseX,lastMouseY,0f);
				gl.glVertex3f(0f,-.85f,0f);
				gl.glEnd();
				gl.glPopMatrix();	
			}
			else{
				if(waterScore > 0){
					waterScore -= .002;
				}
			}
			//this draws the rectangle at the top of the screen which is your score
			   gl.glColor3f(.85f,.85f,.85f);
			   gl.glRectf(-.75f, .65f, .75f, .85f);
			   gl.glPushMatrix();
				gl.glTranslatef(0, 0, 2);
			   gl.glColor3f(1f,.2f,.2f);
			   if(waterScore > 0){
				   gl.glRectf(-.7f, .675f, (float) ((float)waterScore-.7), .825f);
			   }
			   gl.glPopMatrix();
			gl.glEnable( GL.GL_LIGHTING );
		    gl.glEnable( GL.GL_LIGHT0 );
		    gl.glEnable( GL.GL_LIGHT1 );
		    gl.glEnable( GL.GL_LIGHT2 );
		    //if your score reaches this value you win
		    if(waterScore >= 1.4){
		    	AudioPlayer.player.stop(stream);
		    	currentView = ScreenView.ENDSCREEN;
		    }

	    	previousView = ScreenView.WATERGUN;
			
		}
		//this is our third and final game duck hunter
		public void ducks(GLAutoDrawable drawable){
			bonusScore = true;
			float mat_diffuse[] = { 1f, 1f, 0f, 1 };
			previousView = ScreenView.DUCKS;
			gl.glMaterialfv( GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse, 0);//yellow ducks
			//draw ducks
			//the code below handles the movement of the ducks and collision detection
			for(int i = 1; i < 11; i++){
				gl.glPushMatrix();
	
				gl.glScalef((float).2,(float).2,(float) .2);
				//this makes the ducks in the outer ring rotate
					gl.glTranslatef((float)(-2.5f + -3*Math.cos((example_rotateT*2.5f*speed -i*36)/360*2*Math.PI)), 
							(float) (-3*Math.sin((example_rotateT*2.5f*speed -i*36)/360*2*Math.PI)), -2f);
					
					if(switchDirection == false){
						gl.glTranslatef(example_rotateT%100/20, 0,  0);
						if(move == -20){
							//this checks for collision between the ducks and the bullet
							if(lastMouseX > ((example_rotateT%100/20 + -2.5f + -3*Math.cos((example_rotateT*2.5f*speed -i*36)/360*2*Math.PI))/5 -.1f) &&
									lastMouseX < ((example_rotateT%100/20 - 2.5f + -3*Math.cos((example_rotateT*2.5f*speed -i*36)/360*2*Math.PI))/5 +.1f) &&
									lastMouseY >  ((-3*Math.sin((example_rotateT*2.5f*speed -i*36)/360*2*Math.PI)/5) -.1f)&&
									lastMouseY < ((-3*Math.sin((example_rotateT*2.5f*speed -i*36)/360*2*Math.PI)/5) + .1f)){
								if(ducksKilled[i - 1] == false){
									score = score + 10;
									ducksKilled[i - 1] = true;
									sound("duck_quack.wav");
								}
							}
						}
					}
					else{
						gl.glTranslatef((100 - example_rotateT%100)/20, 0,  0);
						if(move == -20){
							if(lastMouseX > (((100 - example_rotateT%100)/20 + -2.5f + -3*Math.cos((example_rotateT*2.5f*speed -i*36)/360*2*Math.PI))/5 -.1f) &&
									lastMouseX < (((100 - example_rotateT%100)/20 -2.5f + -3*Math.cos((example_rotateT*2.5f*speed -i*36)/360*2*Math.PI))/5 +.1f) &&
									lastMouseY >  ((-3*Math.sin((example_rotateT*2.5f*speed -i*36)/360*2*Math.PI)/5) -.1f)&&
									lastMouseY < ((-3*Math.sin((example_rotateT*2.5f*speed -i*36)/360*2*Math.PI)/5) + .1f)){
								if(ducksKilled[i - 1] == false){
									score = score + 10;
									ducksKilled[i - 1] = true;
									sound("duck_quack.wav");
								}
							}
						}
					}
					//this makes the ducks switch direction on the screen
					if(example_rotateT%100 == 99 && switchDirection == false && i == 10){
						switchDirection = true;
		
					}
					else if(example_rotateT%100 == 99 && switchDirection == true && i == 10){
						switchDirection = false;
					}
					//this draws the targets on the ducks
					if(ducksKilled[i-1] == false){
						double angle = 2*  Math.PI/100 ;
					    gl.glDisable(GL.GL_LIGHTING);
						gl.glScalef((float)3,(float)3,(float) 3);	
						gl.glTranslatef(0,0,1);
					    renderer.setColor(1.0f, 1f, 1f, 1f);
					    renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
					    renderer.endRendering();
					   gl.glPolygonMode(gl.GL_FRONT, gl.GL_FILL );
					   gl.glBegin(gl.GL_POLYGON);
					       double angle1=0.0;
					       gl.glVertex2d( .07f * Math.cos(0.0) , .07 * Math.sin(0.0));
					       for (int j=0 ; j< 100;j++)
					       {
					           gl.glVertex2d(.07f * Math.cos(angle1), .07f *Math.sin(angle1));
					           angle1 += angle ;
					       }
						    
						gl.glEnd();
					    renderer.setColor(1.0f, .2f, .2f, 1f);
					    renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
					    renderer.endRendering();
					    gl.glTranslatef(0,0,.01f);
					   gl.glPolygonMode(gl.GL_FRONT, gl.GL_FILL );
					   gl.glBegin(gl.GL_POLYGON);
					       angle1=0.0;
					       gl.glVertex2d( .055f * Math.cos(0.0) , .055 * Math.sin(0.0));
					       for (int j=0 ; j< 100;j++)
					       {
					           gl.glVertex2d(.055f * Math.cos(angle1), .055f *Math.sin(angle1));
					           angle1 += angle ;
					       }
						    
						gl.glEnd();
						renderer.setColor(1.0f, 1f, 1f, 1f);
					    renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
					    renderer.endRendering();
					    gl.glTranslatef(0,0,.01f);
					   gl.glPolygonMode(gl.GL_FRONT, gl.GL_FILL );
					   gl.glBegin(gl.GL_POLYGON);
					       angle1=0.0;
					       gl.glVertex2d( .04f * Math.cos(0.0) , .04 * Math.sin(0.0));
					       for (int j=0 ; j< 100;j++)
					       {
					           gl.glVertex2d(.04f * Math.cos(angle1), .04f *Math.sin(angle1));
					           angle1 += angle ;
					       }
						    
						gl.glEnd();
						renderer.setColor(1.0f, .2f, .2f, 1f);
					    renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
					    renderer.endRendering();
					    gl.glTranslatef(0,0,.01f);
					   gl.glPolygonMode(gl.GL_FRONT, gl.GL_FILL );
					   gl.glBegin(gl.GL_POLYGON);
					       angle1=0.0;
					       gl.glVertex2d( .025f * Math.cos(0.0) , .025 * Math.sin(0.0));
					       for (int j=0 ; j< 100;j++)
					       {
					           gl.glVertex2d(.025f * Math.cos(angle1), .025f *Math.sin(angle1));
					           angle1 += angle ;
					       }
						    
						gl.glEnd();
						gl.glEnable(GL.GL_LIGHTING);
						gl.glTranslatef(0,0,-.01f);
						gl.glTranslatef(0,0,-.01f);
						gl.glTranslatef(0,0,-.01f);
						gl.glTranslatef(0,0,-1);
						gl.glScalef((float)(.333333),(float)(.333333),(float) (.333333));	
						bird.Draw();
					}
				gl.glPopMatrix();
			}
			/*this code works the same as the above code but is used for drawing the inner circle of ducks*/
			for(int i = 1; i < 7; i++){
				gl.glPushMatrix();
	
				gl.glScalef((float).2,(float).2,(float) .2);	
					gl.glTranslatef((float)(-2.5f + -1.3*Math.cos((-example_rotateT*2.5f*speed -i*60)/360*2*Math.PI)), 
							(float) (-1.3*Math.sin((-example_rotateT*2.5f*speed -i*60)/360*2*Math.PI)), -2f);
					
					if(switchDirectionSmall == false){
						gl.glTranslatef(example_rotateT%100/20, 0,  0);
						if(move == -20){
							if(lastMouseX > ((example_rotateT%100/20 + -2.5f + -1*Math.cos((-example_rotateT*2.5f*speed -i*60)/360*2*Math.PI))/5 -.1f) &&
									lastMouseX < ((example_rotateT%100/20 - 2.5f + -1*Math.cos((-example_rotateT*2.5f*speed -i*60)/360*2*Math.PI))/5 +.1f) &&
									lastMouseY >  ((-1.3*Math.sin((-example_rotateT*2.5f*speed -i*60)/360*2*Math.PI)/5) -.1f)&&
									lastMouseY < ((-1.3*Math.sin((-example_rotateT*2.5f*speed -i*60)/360*2*Math.PI)/5) + .1f)){
								if(ducksKilledSmall[i - 1] == false){
									score = score + 10;
									ducksKilledSmall[i - 1] = true;
									sound("duck_quack.wav");
								}
							}
						}
					}
					else{
						gl.glTranslatef((100 - example_rotateT%100)/20, 0,  0);
						if(move == -20){
							if(lastMouseX > (((100 - example_rotateT%100)/20 + -2.5f + -1.3*Math.cos((-example_rotateT*2.5f*speed -i*60)/360*2*Math.PI))/5 -.1f) &&
									lastMouseX < (((100 - example_rotateT%100)/20 -2.5f + -1.3*Math.cos((-example_rotateT*2.5f*speed -i*60)/360*2*Math.PI))/5 +.1f) &&
									lastMouseY >  ((-1.3*Math.sin((-example_rotateT*2.5f*speed -i*60)/360*2*Math.PI)/5) -.1f)&&
									lastMouseY < ((-1.3*Math.sin((-example_rotateT*2.5f*speed -i*60)/360*2*Math.PI)/5) + .1f)){
								if(ducksKilledSmall[i - 1] == false){
									score = score + 10;
									ducksKilledSmall[i - 1] = true;
									sound("duck_quack.wav");
								}
							}
						}
					}
		
					if(example_rotateT%100 == 99 && switchDirectionSmall == false && i == 6){
						switchDirectionSmall = true;
		
					}
					else if(example_rotateT%100 == 99 && switchDirectionSmall == true && i == 6){
						switchDirectionSmall = false;
					}
					if(ducksKilledSmall[i-1] == false){
					   double angle = 2*  Math.PI/100 ;
					    gl.glDisable(GL.GL_LIGHTING);
						gl.glScalef((float)3,(float)3,(float) 3);	
						gl.glTranslatef(0,0,1);
					    renderer.setColor(1.0f, 1f, 1f, 1f);
					    renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
					    renderer.endRendering();
					   gl.glPolygonMode(gl.GL_FRONT, gl.GL_FILL );
					   gl.glBegin(gl.GL_POLYGON);
					       double angle1=0.0;
					       gl.glVertex2d( .07f * Math.cos(0.0) , .07 * Math.sin(0.0));
					       for (int j=0 ; j< 100;j++)
					       {
					           gl.glVertex2d(.07f * Math.cos(angle1), .07f *Math.sin(angle1));
					           angle1 += angle ;
					       }
						    
						gl.glEnd();
					    renderer.setColor(1.0f, .2f, .2f, 1f);
					    renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
					    renderer.endRendering();
					    gl.glTranslatef(0,0,.01f);
					   gl.glPolygonMode(gl.GL_FRONT, gl.GL_FILL );
					   gl.glBegin(gl.GL_POLYGON);
					       angle1=0.0;
					       gl.glVertex2d( .055f * Math.cos(0.0) , .055 * Math.sin(0.0));
					       for (int j=0 ; j< 100;j++)
					       {
					           gl.glVertex2d(.055f * Math.cos(angle1), .055f *Math.sin(angle1));
					           angle1 += angle ;
					       }
						    
						gl.glEnd();
						renderer.setColor(1.0f, 1f, 1f, 1f);
					    renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
					    renderer.endRendering();
					    gl.glTranslatef(0,0,.01f);
					   gl.glPolygonMode(gl.GL_FRONT, gl.GL_FILL );
					   gl.glBegin(gl.GL_POLYGON);
					       angle1=0.0;
					       gl.glVertex2d( .04f * Math.cos(0.0) , .04 * Math.sin(0.0));
					       for (int j=0 ; j< 100;j++)
					       {
					           gl.glVertex2d(.04f * Math.cos(angle1), .04f *Math.sin(angle1));
					           angle1 += angle ;
					       }
						    
						gl.glEnd();
						renderer.setColor(1.0f, .2f, .2f, 1f);
					    renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
					    renderer.endRendering();
					    gl.glTranslatef(0,0,.01f);
					   gl.glPolygonMode(gl.GL_FRONT, gl.GL_FILL );
					   gl.glBegin(gl.GL_POLYGON);
					       angle1=0.0;
					       gl.glVertex2d( .025f * Math.cos(0.0) , .025 * Math.sin(0.0));
					       for (int j=0 ; j< 100;j++)
					       {
					           gl.glVertex2d(.025f * Math.cos(angle1), .025f *Math.sin(angle1));
					           angle1 += angle ;
					       }
						    
						gl.glEnd();
						gl.glEnable(GL.GL_LIGHTING);
						gl.glTranslatef(0,0,-.01f);
						gl.glTranslatef(0,0,-.01f);
						gl.glTranslatef(0,0,-.01f);
						gl.glTranslatef(0,0,-1);
						gl.glScalef((float)(.333333),(float)(.333333),(float) (.333333));	
						bird.Draw();
					}
				gl.glPopMatrix();
			}//end draw ducks

			//this draws the score and timer text
			font = new Font("Cooper Black", Font.BOLD, 50);
			renderer = new TextRenderer(font);
			renderer.setColor(1.0f, 0.2f, 0.2f, 0.8f);
			renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
			renderer.draw(""+(timerEnd - System.currentTimeMillis()/1000), 700, 660);
			renderer.draw("Score: " + score, 30, 660);
			renderer.endRendering();
			
			//this checks to see if you have won the game by killing all of the ducks
		    allDucksKilled = true;
			for(int i = 0; i < ducksKilled.length; i++){
				if(ducksKilled[i] == false){
					allDucksKilled = false;
				}
			}
			for(int i = 0; i < ducksKilledSmall.length; i++){
				if(ducksKilledSmall[i] == false){
					allDucksKilled = false;
				}
			}
			
			if((timerEnd - System.currentTimeMillis()/1000) <= 0 || allDucksKilled == true){
				bonus = ((int) (timerEnd - System.currentTimeMillis()/1000))*10;
				score = score + bonus;
				currentView = ScreenView.ENDSCREEN;
			}
		
		    gl.glDisable( GL.GL_LIGHTING );
		    gl.glDisable( GL.GL_LIGHT0 );
		    gl.glDisable( GL.GL_LIGHT1 );
		    gl.glDisable( GL.GL_LIGHT2 );
		    
		    //this code draws the crosshairs
			gl.glPushMatrix();	// push the current matrix to stack
			mat_diffuse[0] = .93359f;
			mat_diffuse[1] = .16406f;
			mat_diffuse[2] = .55078f;
			mat_diffuse[3] = 1f;
		    gl.glMaterialfv( GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse, 0);
		//		gl.glTranslatef(0, 0, -10);
			float x_pos = (2.0f * currentMouseX) / winW - 1.0f;
			float y_pos = 1.0f - (2.0f * currentMouseY) / winH;
			float z_pos = 1f;
			
			if(mouseClick == false){
				lastMouseX = x_pos;
				lastMouseY = y_pos;
				lastMouseZ = z_pos;
			}
				gl.glTranslatef(x_pos, y_pos, z_pos);
			
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(0,0.065f,0f);
				gl.glVertex3f(0,-.065f,0f);
				gl.glEnd();
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(-.065f,0f,0f);
				gl.glVertex3f(.065f,0f,0f);
				gl.glEnd();
	
		    
		    gl.glScalef(.2f,.2f,.2f);
			gl.glBegin(GL.GL_LINE_LOOP);
			for(int i =0; i <= 300; i++){
			double angle = 2 * Math.PI * i / 300;
			double x = Math.cos(angle)/3;
			double y = Math.sin(angle)/3;
			gl.glVertex2d(x,y);
			}
			gl.glEnd();
	
			gl.glPopMatrix();
			gl.glEnable( GL.GL_LIGHTING );
		    gl.glEnable( GL.GL_LIGHT0 );
		    gl.glEnable( GL.GL_LIGHT1 );
		    gl.glEnable( GL.GL_LIGHT2 );
//			/*draw ball*/
			if(mouseClick){
				gl.glPushMatrix();
				gl.glTranslatef(lastMouseX, lastMouseY, lastMouseZ );
				gl.glScalef(.3f+move/100, .3f+move/100, .3f+move/100);
				GLUquadric quadratic = glu.gluNewQuadric();
				gl.glEnable(gl.GL_NORMALIZE);
				glu.gluSphere(quadratic, .25, 16, 16);
				//glut.glutSolidSphere(.25, 32, 32);
				//sphere.Draw();
				move = (float) (move - 2);
				
				if(move < -20){
					move = 0;
					mouseClick = false;
				}
				gl.glPopMatrix();		
			}
			
		}
		//this is the final view we have which is the score screen
		public void endscreen(GLAutoDrawable drawable){
			//this restores the cursor
			canvas.setCursor(Cursor.getDefaultCursor());
			//this code draws the return to home button
			gl.glDisable(gl.GL_LIGHTING);
			   if(currentMouseX > 100 && currentMouseX < 700 && currentMouseY > 540 && currentMouseY < 660){
				   if(mouseClick == true){
					  currentView = ScreenView.BEGINNINGSCREEN;
					  mouseClick = false;
				   }
				   else{

					   gl.glColor3f(.8f,.8f,.8f);
					  gl.glRectf(.75f, -.45f, -.75f, -.75f);
				   }
			   }
			   else{
				   gl.glColor3f(.85f,.85f,.85f);
				   gl.glRectf(.75f, -.45f, -.75f, -.75f);
			   }
			   gl.glEnable(gl.GL_LIGHTING);
			   mouseClick = false;
			   //this determines which score screen to display
			   if(previousView != ScreenView.WATERGUN){/*game isn't watergun so just display the score that you got at the end of the game*/
				   if(previousView == ScreenView.DUCKS && bonusScore == true){
				    if(currentDifficulty == Difficulty.MEDIUM){
				        score = score + 50;
				    }
				    if(currentDifficulty == Difficulty.HARD){
				        score = score + 100;
				    }
				    if(currentDifficulty == Difficulty.IMPOSSIBLE){
				        score = score + 200;
				    }
				    bonusScore = false;
				   }
				    font = new Font("Cooper Black", Font.BOLD, 47);
				    renderer = new TextRenderer(font);
				    renderer.setColor(1.0f, 0.2f, 0.2f, 0.8f);
				    renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
				    renderer.draw("Your High Score Was", 100, 550);
				    renderer.draw("Back To Main Menu", 140, 130);
			   		renderer.endRendering();
			   		font = new Font("Cooper Black", Font.BOLD, 120);
			   		renderer = new TextRenderer(font);
			   		renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
			   		renderer.setColor(1.0f, 0.2f, 0.2f, 0.8f);
			   		renderer.draw(""+score, 280, 360);
			   		renderer.endRendering();
			   }
			   else{/*this is only for the watergun game and it draws a random prize if you win*/
				    if(waterScore >= 1.4){
				    	if(onlyOnePrize == false){
				    		example_rotateT = 0;
					    	newPrize = randomPrize.nextDouble();
					    	if(newPrize >= 0 && newPrize < .2){
					    		prize = new objModel("dragon.obj");
					    	}
					    	else if(newPrize >= .2 && newPrize < .4){
					    		prize = new objModel("bunny.obj");
					    	}
					    	else if(newPrize >= .4 && newPrize < .6){
					    		prize = new objModel("bunny2.obj");
					    	}
							else if(newPrize >= .6 && newPrize < .8){
								prize = new objModel("buddha.obj");
	 						}
							else {
								prize = new objModel("computer.obj");
							}
					    	onlyOnePrize = true;
				    	}
				    	if(newPrize >= 0 && newPrize < .2){
				    		float mat_diffuse[] = { 1f, 0f, 0f, 1 };
							gl.glMaterialfv( GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse, 0);
				    		gl.glPushMatrix();
				    		gl.glRotatef(example_rotateT, 0,1,0);
					    	gl.glScalef(1.1f, 1.1f, 1.1f);
					    	prize.Draw();
					    	gl.glPopMatrix();
				    	}
				    	else if(newPrize >= .2 && newPrize < .4){
				    		float mat_diffuse[] = { 1f, 1f, 1f, 1 };
							gl.glMaterialfv( GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse, 0);
				    		gl.glPushMatrix();
				    		gl.glRotatef(example_rotateT, 0,1,0);
					    	gl.glScalef(.7f, .7f, .7f);
					    	prize.Draw();
					    	gl.glPopMatrix();
				    	}
				    	else if(newPrize >= .4 && newPrize < .6){
				    		float mat_diffuse[] = { 1f, 1f, 1f, 1 };
							gl.glMaterialfv( GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse, 0);
				    		gl.glPushMatrix();
				    		gl.glRotatef(10, 1, 0,0);
				    		gl.glRotatef(example_rotateT, 0,1,0);
					    	gl.glScalef(.7f, .7f, .7f);
					    	prize.Draw();
					    	gl.glPopMatrix();
				    	}
						else if(newPrize >= .6 && newPrize < .8){
							float mat_diffuse[] = { 11/255f, 218/255f, 81/255f, 1 };
							gl.glMaterialfv( GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse, 0);
							gl.glPushMatrix();
							gl.glRotatef(example_rotateT, 0,1,0);
							gl.glScalef(.9f, .9f, .9f);
					    	prize.Draw();
					    	gl.glPopMatrix();
 						}
						else {
							float mat_diffuse[] = { .5f, .5f, .5f, 1 };
							gl.glMaterialfv( GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse, 0);
							gl.glPushMatrix();
							gl.glRotatef(example_rotateT, 0,1,0);
					    	gl.glScalef(.7f, .7f, .7f);
					    	prize.Draw();
					    	gl.glPopMatrix();
						}
				    	
					    font = new Font("Cooper Black", Font.BOLD, 60);
					    renderer = new TextRenderer(font);
					    renderer.setColor(1.0f, 0.2f, 0.2f, 0.8f);
					    renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
				 	    renderer.draw("You win a Prize!!!", 100, 610);
				   		renderer.endRendering();
				   		font = new Font("Cooper Black", Font.BOLD, 47);
					    renderer = new TextRenderer(font);
					    renderer.setColor(1.0f, 0.2f, 0.2f, 0.8f);
					    renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
					    renderer.draw("Back To Main Menu", 140, 130);
				   		renderer.endRendering();
				    }
				    else{
				    	font = new Font("Cooper Black", Font.BOLD, 90);
					    renderer = new TextRenderer(font);
					    renderer.setColor(1.0f, 0.2f, 0.2f, 0.8f);
					    renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
				 	    renderer.draw("You Lose", 170, 450);
				   		renderer.endRendering();
				   		font = new Font("Cooper Black", Font.BOLD, 47);
					    renderer = new TextRenderer(font);
					    renderer.setColor(1.0f, 0.2f, 0.2f, 0.8f);
					    renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
					    renderer.draw("Back To Main Menu", 140, 130);
				   		renderer.endRendering();
				    }
			   }
			
		}

	//this method is used to play our audio files	
	public void sound(String file){
			
		File audio = new File(file);
		try {
			InputStream input = new FileInputStream(file);				
			stream  = new AudioStream(input);
			AudioPlayer.player.start(stream);
			if(soundOff) AudioPlayer.player.stop(stream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public FinalProject(String[] args){
		super("Carnival Games");
		/*try {
			background = ImageIO.read(new File(args[0]));
		} catch (IOException e) {
			System.out.println(e);
		}		*/
		canvas = new GLCanvas();
		canvas.addGLEventListener(this);
		canvas.addKeyListener(this);
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
		animator = new FPSAnimator(canvas, 30);	// create a 30 fps animator
		getContentPane().add(canvas);
		setSize(winW, winH);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		animator.start();
		canvas.requestFocus();
	}
	
	public static void main(String[] args) {

		
		new FinalProject(args);
	}
	
	public void init(GLAutoDrawable drawable) {
		gl = drawable.getGL();

		initViewParameters();
		gl.glClearColor(.1f, .1f, .1f, 1f);
		gl.glClearDepth(1.0f);

	    // white light at the eye
	    float light0_position[] = { 0, 0, 1, 0 };
	    float light0_diffuse[] = { 1, 1, 1, 1 };
	    float light0_specular[] = { 1, 1, 1, 1 };
	    gl.glLightfv( GL.GL_LIGHT0, GL.GL_POSITION, light0_position, 0);
	    gl.glLightfv( GL.GL_LIGHT0, GL.GL_DIFFUSE, light0_diffuse, 0);
	    gl.glLightfv( GL.GL_LIGHT0, GL.GL_SPECULAR, light0_specular, 0);

	    //red light
	    float light1_position[] = { -.1f, .1f, 0, 0 };
	    float light1_diffuse[] = { .6f, .05f, .05f, 1 };
	    float light1_specular[] = { .6f, .05f, .05f, 1 };
	    gl.glLightfv( GL.GL_LIGHT1, GL.GL_POSITION, light1_position, 0);
	    gl.glLightfv( GL.GL_LIGHT1, GL.GL_DIFFUSE, light1_diffuse, 0);
	    gl.glLightfv( GL.GL_LIGHT1, GL.GL_SPECULAR, light1_specular, 0);

	    //blue light
	    float light2_position[] = { .1f, .1f, 0, 0 };
	    float light2_diffuse[] = { .05f, .05f, .6f, 1 };
	    float light2_specular[] = { .05f, .05f, .6f, 1 };
	    gl.glLightfv( GL.GL_LIGHT2, GL.GL_POSITION, light2_position, 0);
	    gl.glLightfv( GL.GL_LIGHT2, GL.GL_DIFFUSE, light2_diffuse, 0);
	    gl.glLightfv( GL.GL_LIGHT2, GL.GL_SPECULAR, light2_specular, 0);

	    //material
	    float mat_ambient[] = { 0, 0, 0, 1 };
	    float mat_specular[] = { .8f, .8f, .8f, 1 };
	    float mat_diffuse[] = { .4f, .4f, .4f, 1 };
	    float mat_shininess[] = { 128 };
	    gl.glMaterialfv( GL.GL_FRONT, GL.GL_AMBIENT, mat_ambient, 0);
	    gl.glMaterialfv( GL.GL_FRONT, GL.GL_SPECULAR, mat_specular, 0);
	    gl.glMaterialfv( GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse, 0);
	    gl.glMaterialfv( GL.GL_FRONT, GL.GL_SHININESS, mat_shininess, 0);

	    float bmat_ambient[] = { 0, 0, 0, 1 };
	    float bmat_specular[] = { 0, .8f, .8f, 1 };
	    float bmat_diffuse[] = { 0, .4f, .4f, 1 };
	    float bmat_shininess[] = { 128 };
	    gl.glMaterialfv( GL.GL_BACK, GL.GL_AMBIENT, bmat_ambient, 0);
	    gl.glMaterialfv( GL.GL_BACK, GL.GL_SPECULAR, bmat_specular, 0);
	    gl.glMaterialfv( GL.GL_BACK, GL.GL_DIFFUSE, bmat_diffuse, 0);
	    gl.glMaterialfv( GL.GL_BACK, GL.GL_SHININESS, bmat_shininess, 0);

	    float lmodel_ambient[] = { 0, 0, 0, 1 };
	    gl.glLightModelfv( GL.GL_LIGHT_MODEL_AMBIENT, lmodel_ambient, 0);
	    gl.glLightModeli( GL.GL_LIGHT_MODEL_TWO_SIDE, 1 );

	    gl.glEnable( GL.GL_NORMALIZE );
	    gl.glEnable( GL.GL_LIGHTING );
	    gl.glEnable( GL.GL_LIGHT0 );
	    gl.glEnable( GL.GL_LIGHT1 );
	    gl.glEnable( GL.GL_LIGHT2 );

	    gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LESS);
		gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		gl.glCullFace(GL.GL_BACK);
		gl.glEnable(GL.GL_CULL_FACE);
		gl.glShadeModel(GL.GL_SMOOTH);		
	}
	
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		winW = width;
		winH = height;

		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glLoadIdentity();
//			if(currentView == ScreenView.BEGINNINGSCREEN){
//				glu.gluPerspective(45.f, (float)width/(float)height, znear, zfar);
//			}
//			else{
				gl.glOrtho(-1, 1, -1, 1, znear, zfar);
//			}
			gl.glMatrixMode(GL.GL_MODELVIEW);
	}
	
	public void mousePressed(MouseEvent e) {	
		mouseClick = true;
		mouseX = e.getX();
		mouseY = e.getY();
		mouseButton = e.getButton();
		canvas.display();
		soundOff = false;
		if(currentView == ScreenView.WATERGUN){
			sound("water.wav");
		}
		if(e.getButton() == MouseEvent.BUTTON1){
			mousePressed = true;
		}
	}
	
	public void mouseReleased(MouseEvent e) {
		mousePressed = false;
		if(currentView == ScreenView.WATERGUN) AudioPlayer.player.stop(stream);
		mouseButton = MouseEvent.NOBUTTON;
		canvas.display();
	}	
	
	public void mouseDragged(MouseEvent e) {
		currentMouseX = e.getX();
		currentMouseY = e.getY();
	}

	
	/* computes optimal transformation parameters for OpenGL rendering.
	 * this is based on an estimate of the scene's bounding box
	 */	
	void initViewParameters()
	{
		roth = rotv = 0;

		float ball_r = (float) Math.sqrt((xmax-xmin)*(xmax-xmin)
							+ (ymax-ymin)*(ymax-ymin)
							+ (zmax-zmin)*(zmax-zmin)) * 0.707f;

		centerx = (xmax+xmin)/2.f;
		centery = (ymax+ymin)/2.f;
		centerz = (zmax+zmin)/2.f;
		xpos = centerx;
		ypos = centery;
		zpos = ball_r/(float) Math.sin(45.f*Math.PI/180.f)+centerz;

		znear = 0.01f;
		zfar  = 1000.f;

		motionSpeed = 0.002f * ball_r;
		rotateSpeed = 0.1f;

	}	
	
	// these event functions are not used for this assignment
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) { }
	public void keyTyped(KeyEvent e) { }
	public void keyReleased(KeyEvent e) { }
	public void mouseMoved(MouseEvent e) {
		currentMouseX = e.getX();
		currentMouseY = e.getY();
		//currentBunnyX = (float)((float)(400 - e.getX())/winW*10f);
		//currentBunnyY = (float)((float)(400 - e.getY())/winH*10f);
	}
	public void actionPerformed(ActionEvent e) { }
	public void mouseClicked(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) {	}	
}

