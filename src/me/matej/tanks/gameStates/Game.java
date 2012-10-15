package me.matej.tanks.gameStates;

import java.util.ArrayList;
import me.matej.tanks.Main;
import me.matej.tanks.Particles.Object;
import me.matej.tanks.Particles.Tank;
import me.matej.tanks.Particles.Wall;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author matejkramny
 */
public class Game extends GameStateClass {
	public ArrayList<Object> objects;
	public boolean didCollide;
	
	public Game () {
		DisplayMode dm = Main.getInstance().getOpenGL().getDisplayMode();
		
		objects = new ArrayList<Object>();
		
		// Walls
		// we have 4 walls, surrounding the game area.
		objects.add(new Wall().init(0, 0, 2, dm.getHeight()).setCollision());
		objects.add(new Wall().init(dm.getWidth() - 2, 0, 2, dm.getHeight()).setCollision());
		objects.add(new Wall().init(0, 0, dm.getWidth(), 2).setCollision());
		objects.add(new Wall().init(0, dm.getHeight() - 2, dm.getWidth(), 2).setCollision());
		
		objects.add(new Tank());
	}
	
	@Override
	public void draw () {
		Object[] os = objects.toArray(new Object[objects.size()]);
		for (int i = 0; i < os.length; i++) {
			GL11.glLoadIdentity();
			os[i].draw();
		}
		
		if (didCollide) {
			GL11.glClearColor(1f, 0f, 0f, 1f);
		} else {
			GL11.glClearColor(1f, 1f, 1f, 1f);
		}
	}
	
	public Object[] getObjects () {
		return objects.toArray(new Object[objects.size()]);
	}
	
	public void reset () {
		while (objects.size() > 5) {
			objects.remove(5);
		}
	}
	
	@Override
	public void update (int delta) {
		didCollide = false;
		Object[] os = objects.toArray(new Object[objects.size()]);
		
		for (int i = 0; i < os.length; i++) {
			for (int ii = 0; ii < os.length; ii++) {
				if (os[i] instanceof Wall || os[i] == os[ii]) continue; // allows only tank and other objects to be checked for collision, however doesn't prevent it. Tank can check for collision with a wall
				
				if (os[i].checkCollision(os[ii])) {
					
				}
			}
		}
		
		for (int i = 0; i < os.length; i++) {
			os[i].update(delta);
		}
	}

	@Override
	public void init() {
	}

	@Override
	public void keyPressed(int key) {
		Object[] os = objects.toArray(new Object[objects.size()]);
		for (int i = 0; i < os.length; i++) {
			os[i].keyPressed(key);
		}
	}

	@Override
	public void mouseButtonPressed(int index) {
		Object[] os = objects.toArray(new Object[objects.size()]);
		for (int i = 0; i < os.length; i++) {
			os[i].mouseButtonPressed(index);
		}
	}
	
}
