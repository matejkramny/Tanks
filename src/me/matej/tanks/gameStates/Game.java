package me.matej.Tanks.gameStates;

import java.awt.Rectangle;
import java.util.ArrayList;
import me.matej.Tanks.Main;
import me.matej.Tanks.Particles.CollisionSquare;
import me.matej.Tanks.Particles.Object;
import me.matej.Tanks.Particles.Tank;
import me.matej.Tanks.Particles.Wall;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author matejkramny
 */
public class Game extends GameStateClass {
	public ArrayList<Object> objects;
	public CollisionSquare[][] squares;
	
	public Game () {
		DisplayMode dm = Main.getInstance().getOpenGL().getDisplayMode();
		
		objects = new ArrayList<Object>();
		squares = new CollisionSquare[dm.getWidth()/20][dm.getHeight()/20];
		
		for (int x = 0; x < squares.length; x++) {
			for (int y = 0; y < squares[x].length; y++) {
				squares[x][y] = new CollisionSquare(x*20, y*20);
				squares[x][y].red = (float)x / (float)squares.length;
				squares[x][y].green = (float)y / (float)squares[x].length;
			}
		}
		
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
		/*for (int x = 0; x < squares.length; x++) {
			int rX = x * 20;
			
			for (int y = 0; y < squares[x].length; y++) {
				squares[x][y].draw();
			}
		}*/
		
		Object[] os = objects.toArray(new Object[objects.size()]);
		for (int i = 0; i < os.length; i++) {
			GL11.glLoadIdentity();
			os[i].draw();
			
			for (int ii = 0; ii < os.length; ii++) {
				if (ii == i || i < 5) continue;
				if (os[i].collidesWith(os[ii])) {
					squares[10][10].draw();
				}
			}
		}
	}
	
	public Object[] getObjects () {
		return objects.toArray(new Object[objects.size()]);
	}
	
	@Override
	public void update (int delta) {
		Object[] os = objects.toArray(new Object[objects.size()]);
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
