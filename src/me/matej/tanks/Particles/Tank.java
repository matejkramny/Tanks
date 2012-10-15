package me.matej.tanks.Particles;

import java.io.IOException;
import java.text.DecimalFormat;
import me.matej.tanks.GameState;
import me.matej.tanks.Main;
import me.matej.tanks.gameStates.Game;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

/**
 *
 * @author matejkramny
 */
public class Tank extends Object {
	public Object gun; // Rotating independant of body
	private Object frontIdentifier; // Small different-colored square that marks the fron of the tank
	private float dx, dy; // Last directions.
	private Vector lastLoc;
	private boolean statsVisible = false;
	private short speedMultiplier = 1;
	private double lastCollision;
	
	public Tank () {
		this.init(50, 50, 50, 25, 1, 1, 1);
		gun = new TankGun().init(75, 8, 35, 4, 0.5f, 0.5f, 0.5f);
		
		frontIdentifier = new Object().init(0, 0, 3, 25, 0, 1, 0);
		frontIdentifier.centeredRot = false;
		
		this.setGunPos();
		loc.x = 150;
		loc.y = 150;
		
		// Load tank texture
		super.loadTexture("me/matej/tanks/resources/TankBody.png");
	}
	
	@Override
	public void keyPressed (int key) {
		// Only key presses inside the event queue
		if (key == Keyboard.KEY_R) {
			this.resetRot();
			this.resetX();
			this.resetY();
			((Game)GameState.Game.getStateInstance()).reset();
			this.dy = 0; this.dx = 0;
		} else if (key == Keyboard.KEY_V) {
			statsVisible = !statsVisible;
		} else if (key == Keyboard.KEY_I) {
			this.speedMultiplier++;
		} else if (key == Keyboard.KEY_K) {
			this.speedMultiplier--;
		}
	}
	
	@Override
	public void mouseButtonPressed (int button) {
		Game game = (Game)GameState.Game.getStateInstance();
		if (button == 0) {
			game.objects.add(new Object().init(loc.x + this.w/2, loc.y + this.h / 2, 10, 10, 1, 1, 0));
		} else if (button == 1) {
			game.objects.add(new Object().init(loc.x + this.w/2, loc.y + this.h / 2, 10, 10, 1, 0, 1));
		}
	}
	
	@Override
	public void update (int delta) {
		boolean wantsToMove = false, moveForward = true;
		boolean isMoving = false;
		if (dx != 0f || dy != 0f) {
			isMoving = true;
		}
		
		// Repeated keys
		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			wantsToMove = true; moveForward = false;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			wantsToMove = true; moveForward = true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			rot -= 0.15f * delta;
			if (rot < 0f)
				rot = 360 - (rot * -1);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			rot += 0.15f * delta;
			if (rot > 360f)
				rot = rot - 360f; // Prevent over rotating
		}
		
		if (this.lastCollision < delta && this.lastCollision > 0) {
			this.lastCollision -= delta;
		} else if (this.lastCollision > delta) {
			wantsToMove = false;
			this.lastCollision = 0d;
		}
		
		gun.update(delta);
		
		// Movement of the tank
		double angle = this.rot;
		while (angle > 90f)
			angle -= 90f;
		
		if (wantsToMove) {
			double rotRadians = Math.toRadians(this.rot);
			double newDx = Math.cos(rotRadians) * 0.05f * this.speedMultiplier;
			double newDy = Math.sin(rotRadians) * 0.05f * this.speedMultiplier;
			
			if (!moveForward) {
				newDx *= -1; // Reverse direction..
				newDy *= -1;
			}
			
			this.dx = (float)newDx;
			this.dy = (float)newDy;
			
			loc.x += dx * delta;
			loc.y += dy * delta;
		} else {
			double rotRadians = Math.toRadians(this.rot);
			
			if (dx != 0f) {
				double newDx = Math.cos(rotRadians) * 0.05f;
				loc.x += dx * delta;
				dx *= 0.90f;
			
				if (dx < 0.02f && dx > -0.02f)
					dx = 0f;
			}
			if (dy != 0f) {
				double newDy = Math.sin(rotRadians) * 0.05f;
				loc.y += dy * delta;
				dy *= 0.90f;
			
				if (dy < 0.02f && dy > -0.02f)
					dy = 0f;
			}
		}
		
		lastLoc = loc;
		
		setGunPos();
		setFrontIdentifierPos();
	}
	
	private void setGunPos () {
		gun.loc.x = loc.x + this.w/2;
		gun.loc.y = loc.y + this.h/2 - gun.h/2;
	}
	
	private void setFrontIdentifierPos () {
		frontIdentifier.rot = rot;
		frontIdentifier.loc.x = loc.x + this.w - frontIdentifier.w;
		frontIdentifier.loc.y = loc.y;
	}
	
	@Override
	public void draw () {
		super.draw();
		
		gun.draw();
		
		if (statsVisible) {
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			
			TrueTypeFont font = Main.getInstance().getOpenGL().getFont();
			DecimalFormat format = new DecimalFormat("##.00");
			
			Color color = Color.black;
			font.drawString(20, 20, "Gun Rot: ", color); font.drawString(150, 20, format.format(gun.rot), color);
			font.drawString(20, 50, "Tank X:", color); font.drawString(150, 50, format.format(loc.x), color);
			font.drawString(20, 80, "Tank Y:", color); font.drawString(150, 80, format.format(loc.y), color);
			font.drawString(20, 110, "Tank Rot:", color); font.drawString(150, 110, format.format(this.rot), color);
			font.drawString(20, 140, "FPS:", color); font.drawString(150, 140, ""+Main.getInstance().getOpenGL().getFps(), color);
			font.drawString(20, 170, "Tank Speed Multiplier: ", color); font.drawString(300, 170, ""+this.speedMultiplier, color);
			
			GL11.glDisable(GL11.GL_TEXTURE_2D);
		}
	}
}
