package me.matej.tanks.Particles;

import java.awt.Rectangle;
import me.matej.tanks.GameState;
import me.matej.tanks.Main;
import me.matej.tanks.gameStates.Game;
import me.matej.tanks.gameStates.GameStateInterface;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author matejkramny
 */
public class Object implements GameStateInterface {
	public Vector loc; // location
	public double w, h; // coordinates, width and height
	public double rot; // Rotation angle
	public float r, g, b, a; // Color
	private int count = 0;
	
	public boolean centeredRot = true;
	private Rectangle me;
	private Rectangle him;
	
	public Vector[] rotatedVertices;
	
	@Override
	public void draw () {
		this.drawBegin();
		this.rotate();
		this.color();
		this.drawVerts();
		this.drawEnd();
	}
	
	protected void drawBegin () {
		GL11.glLoadIdentity();
		GL11.glPushMatrix();
	}
	protected void drawEnd() {
		GL11.glPopMatrix();
	}
	protected void rotate () {
		// Centered rotation
		if (centeredRot) {
			GL11.glTranslated(loc.x+w/2, loc.y+h/2, 0);
			GL11.glRotated(rot, 0d, 0d, 1d);
			GL11.glTranslated(-w/2, -h/2, 0);
		} else {
			// Rotation from x
			GL11.glTranslated(loc.x, loc.y+h/2, 0);
			GL11.glRotated(rot, 0, 0, 1);
			GL11.glTranslated(0, -h/2, 0);
		}
	}
	protected void color () {
		GL11.glColor4f(r, g, b, a);
	}
	protected void drawVerts () {
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glVertex2d(0, 0);
			GL11.glVertex2d(0, h);
			GL11.glVertex2d(w, h);
			GL11.glVertex2d(w, 0);
		}
		GL11.glEnd();
	}
	
	// Returns true when collision happens
	public boolean checkCollision (Object o) {
		this.calcRotatedVertices();
		o.calcRotatedVertices();
		
		Game g = (Game)GameState.Game.getStateInstance();
		
		Vector rots[] = this.rotatedVertices;
		Vector orots[] = o.rotatedVertices;
		
		// test separation axes of current polygon
		for(int j = rots.length-1, i = 0; i < rots.length; j = i, i++)
		{
			Vector v0 = rots[j];
			Vector v1 = rots[i];

			Vector edge = new Vector(0,0);
			edge.x = v1.x - v0.x; // edge
			edge.y = v1.y - v0.y; // edge
			
			Vector axis = edge.perp(); // Separate axis is perpendicular to the edge

			if(separatedByAxis(axis, o))
				return false;
		}

		// test separation axes of other polygon
		for(int j = orots.length-1, i = 0; i < orots.length; j = i, i++)
		{
			Vector v0 = orots[j];
			Vector v1 = orots[i];

			Vector edge2 = new Vector(0,0);
			edge2.x = v1.x - v0.x; // edge
			edge2.y = v1.y - v0.y; // edge
			
			Vector axis = edge2.perp(); // Separate axis is perpendicular to the edge

			if(separatedByAxis(axis, o)) {
				
				return false;
			}
		}
		
		g.didCollide = true;
		
		return true;
	}
	
	float min,max;
	
	public void calculateInterval(Vector axis) {
		this.min = this.max = (float) this.rotatedVertices[0].dot(axis);

		for (int i = 1; i < rotatedVertices.length; i++) {
			float d = (float) rotatedVertices[i].dot(axis);
			if (d < this.min)
				this.min = d;
			else if (d > this.max)
				this.max = d;
		}
	}
	
	float mina, maxa;
	float minb, maxb;

	public boolean separatedByAxis(Vector axis, Object poly) {
		calculateInterval(axis);
		mina = min;
		maxa = max;
		poly.calculateInterval(axis);
		minb = poly.min;
		maxb = poly.max;
		return intervalsSeparated(mina, maxa, minb, maxb);
	}
	
	public boolean intervalsSeparated(float mina, float maxa, float minb,
			float maxb) {
		return (mina > maxb) || (minb > maxa);
	}
	
	protected void calcRotatedVertices () {
		rotatedVertices = new Vector[4];
		
		if (this.rot == 0) {
			rotatedVertices[0] = new Vector(loc.x, loc.y);
			rotatedVertices[1] = new Vector(loc.x+w, loc.y);
			rotatedVertices[2] = new Vector(loc.x+w, loc.y+h);
			rotatedVertices[3] = new Vector(loc.x, loc.y+h);
			
			return;
		}
		
		// The Calculations have 0 degree facing the bottom, and anti-clockwise. Rotating by 90 degrees allows accurate rotation relative to the tank
		double actRot = this.rot + 90f;
		// Prevents going over >360 and <0
		if (actRot > 360d)
			actRot = actRot - 360d;
		else if (actRot < 0d)
			actRot = (360d - actRot) * -1;
		
		// Now to the calculation.. Top left corner coordinates relative to center
		double newX = w/2, newY = h/2;
		double angle = Math.toDegrees(Math.atan2(newY, newX)); // The angle of the coordinates rel. to center
		double distance = Math.sqrt(newX*newX + newY*newY); // Distance (hypotenuse).. thanks pythagoras 
		angle += actRot; // Add the calc. angle with real rotation of the tank to match current rotated point
		if (angle > 360f) // Again prevents >360 and <0
			angle = angle - 360f;
		else if (angle < 0f)
			angle = (360 - angle) * -1;
		angle *= -1;
		double r1X = Math.sin(Math.toRadians(angle))*distance; // Returns X from the angle.
		double r1Y = Math.cos(Math.toRadians(angle))*distance; // Returns Y form the angle.
		
		angle += 180f; // Opposite point.. +180 degrees
		double r3X = Math.sin(Math.toRadians(angle))*distance;
		double r3Y = Math.cos(Math.toRadians(angle))*distance;
		
		newX = -w/2; newY = h/2; // Top bottom corner coordinates
		angle = Math.toDegrees(Math.atan2(newY, newX));
		distance = Math.sqrt(newX*newX + newY*newY);
		angle += actRot;
		if (angle > 360f)
			angle = angle - 360f;
		else if (angle < 0f)
			angle = (360 - angle) * -1;
		angle *= -1;
		double r2X = Math.sin(Math.toRadians(angle))*distance;
		double r2Y = Math.cos(Math.toRadians(angle))*distance;
		
		angle -= 180f; // 180 degrees give us opposite angle
		double r4X = Math.sin(Math.toRadians(angle))*distance;
		double r4Y = Math.cos(Math.toRadians(angle))*distance;
		
		double relX = this.loc.x + this.w/2, relY = this.loc.y + this.h/2;
		rotatedVertices[0] = new Vector(r1X+relX, r1Y+relY);
		rotatedVertices[1] = new Vector(r2X+relX, r2Y+relY);
		rotatedVertices[2] = new Vector(r3X+relX, r3Y+relY);
		rotatedVertices[3] = new Vector(r4X+relX, r4Y+relY);
	}
	
	public boolean collidesWith (Object other) {
		me.setBounds((int)loc.x, (int)loc.y, (int)w, (int)h);
		him.setBounds((int)other.loc.x, (int)other.loc.y, (int)other.w, (int)other.h);
		
		if (me.intersects(him))
			return true;
		
		return false;
	}
	
	public Object () {
		me = new Rectangle();
		him = new Rectangle();
		this.loc = new Vector();
		
	}
	
	public Object init (double x, double y, double w, double h) {
		this.init(x, y, w, h, 0, 0, 0);
		return this;
	}
	public Object init (double x, double y, double w, double h, float r, float g, float b) {
		this.init(x, y, w, h, r, g, b, 1);
		return this;
	}
	public Object init (double x, double y, double w, double h, float r, float g, float b, float a) {
		loc.x = x;
		loc.y = y;
		this.w = w;
		this.h = h;
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		
		this.resetRot();
		
		return this;
	}
	
	public void resetY () {
		loc.y = (int)((float)Main.getInstance().getOpenGL().getDisplayMode().getHeight() / 2f - (float)h / 2f);
	}
	
	public void resetX () {
		loc.x = (int)((float)Main.getInstance().getOpenGL().getDisplayMode().getWidth() / 2f - (float)w / 2f);
	}
	
	public void resetRot () {
		this.rot = 0f;
	}
	
	@Override
	public void update (int delta) {/* Checks for collisions? */}
	
	@Override
	public void keyPressed(int key) {}

	@Override
	public void mouseButtonPressed(int index) {}

	@Override
	public void init() {}
	
}
