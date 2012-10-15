package me.matej.tanks.gameStates;

import org.lwjgl.opengl.GL11;

/**
 *
 * @author matejkramny
 */
public class MainMenu extends GameStateClass {
	
	@Override
	public void draw () {
		GL11.glLoadIdentity();
		GL11.glColor3f(1, 0, 0);
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glVertex2f(0, 0);
			GL11.glVertex2f(10, 0);
			GL11.glVertex2f(10, 10);
			GL11.glVertex2f(0, 10);
		}
		GL11.glEnd();
	}
	
	@Override
	public void update (int delta) {
	}

	@Override
	public void init() {
	}

	@Override
	public void keyPressed(int key) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void mouseButtonPressed(int index) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
}
