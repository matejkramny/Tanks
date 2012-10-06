package me.matej.Tanks.Particles;

import java.awt.Rectangle;
import me.matej.Tanks.Main;
import me.matej.Tanks.gameStates.GameStateInterface;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author matejkramny
 */
public class Object implements GameStateInterface {
	public float x, y;
	public int w, h; // coordinates, width and height
	public float rot; // Rotation angle
	public float r, g, b; // Color
	public boolean centeredRot = true;
	private Rectangle me;
	private Rectangle him;
	
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
			GL11.glTranslatef(x+w/2, y+h/2, 0);
			GL11.glRotatef(rot, 0f, 0f, 1f);
			GL11.glTranslatef(-w/2, -h/2, 0);
		} else {
			// Rotation from x
			GL11.glTranslatef(x, y+h/2, 0);
			GL11.glRotatef(rot, 0, 0, 1f);
			GL11.glTranslatef(0, -h/2, 0);
		}
	}
	protected void color () {
		GL11.glColor3f(r, g, b);
	}
	protected void drawVerts () {
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glVertex2f(0, 0);
			GL11.glVertex2f(0, h);
			GL11.glVertex2f(w, h);
			GL11.glVertex2f(w, 0);
		}
		GL11.glEnd();
	}
	
	public boolean collidesWith (Object other) {
		me.setBounds((int)x, (int)y, w, h);
		him.setBounds((int)other.x, (int)other.y, other.w, other.h);
		
		if (me.intersects(him))
			return true;
		
		return false;
	}
	
	public Object () {
		me = new Rectangle();
		him = new Rectangle();
	}
	
	public Object init (float x, float y, int w, int h, float r, float g, float b) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.r = r;
		this.g = g;
		this.b = b;
		this.resetRot();
		
		return this;
	}
	
	public void resetY () {
		this.y = (int)((float)Main.getInstance().getOpenGL().getDisplayMode().getHeight() / 2f - (float)h / 2f);
	}
	
	public void resetX () {
		this.x = (int)((float)Main.getInstance().getOpenGL().getDisplayMode().getWidth() / 2f - (float)w / 2f);
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
