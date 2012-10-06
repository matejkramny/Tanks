package me.matej.Tanks.Particles;

import org.lwjgl.opengl.GL11;

/**
 *
 * @author matejkramny
 */
public class CollisionSquare {
	
	int x, y;
	final int w = 20, h = 20;
	public float red, green, blue;
	
	public CollisionSquare (int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void draw () {
		GL11.glLoadIdentity();
		
		GL11.glTranslatef(x, y, 0);
		GL11.glColor3f(red, green, blue);
		
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glVertex2f(0, 0);
			GL11.glVertex2f(0, h);
			GL11.glVertex2f(w, h);
			GL11.glVertex2f(w, 0);
		}
		GL11.glEnd();
	}
	
}
