package me.matej.Tanks.Particles;

import me.matej.Tanks.GameState;
import me.matej.Tanks.Main;
import me.matej.Tanks.gameStates.Game;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

/**
 *
 * @author matejkramny
 */
public class Tank extends Object {
	public Object gun; // Rotating independant of body
	private float wantedRot; // wanted rotation angle of the gun. the gun can move at certain speed...
	private Object frontIdentifier; // Small different-colored square that marks the fron of the tank
	
	private float dx, dy; // Last directions.
	
	private volatile boolean statsVisible = false;
	
	public Tank () {
		this.init(50, 50, 50, 25, 1, 1, 1);
		gun = new Object().init(75, 8, 35, 4, 0.5f, 0.5f, 0.5f);
		frontIdentifier = new Object().init(0, 0, 3, 25, 0, 1, 0);
		this.rot = 0f; // Rotation
		frontIdentifier.centeredRot = false;
		gun.centeredRot = false;
		this.setGunPos();
		x = 150;
		y = 150;
	}
	
	@Override
	public void keyPressed (int key) {
		// Only key presses inside the event queue
		if (key == Keyboard.KEY_R) {
			this.resetRot();
			this.resetX();
			this.resetY();
			this.dy = 0; this.dx = 0;
		} else if (key == Keyboard.KEY_V) {
			statsVisible = !statsVisible;
		}
	}
	
	@Override
	public void mouseButtonPressed (int button) {
		Game game = (Game)GameState.Game.getStateInstance();
		if (button == 0) {
			game.objects.add(new Object().init(this.x + this.w/2, this.y + this.h / 2, 10, 10, 1, 1, 0));
		} else if (button == 1) {
			game.objects.add(new Object().init(this.x + this.w/2, this.y + this.h / 2, 10, 10, 1, 0, 1));
		}
	}
	
	@Override
	public void update (int delta) {
		// Keys
		boolean wantsToMove = false, moveForward = true;
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
				rot = rot - 360f;
		}
		
		// Handle rotation of gun
		int mx = Mouse.getX(), my = Main.getInstance().getOpenGL().getDisplayMode().getHeight() - Mouse.getY();
		float xDiff = mx - gun.x, yDiff = my - gun.y;
		
		float actual = (float)Math.abs(Math.toDegrees(Math.atan(yDiff / xDiff)));
		
		if (xDiff > 0f && yDiff < 0f)
			actual = 360f - actual;
		else if (xDiff < 0f && yDiff < 0f)
			actual += 180f;
		else if (xDiff < 0f && yDiff > 0f)
			actual = 90f + (90f - actual);
		
		wantedRot = actual;
		
		// Move the gun slowly - to be improved.. Works but not flawless.
		
		float curRot = gun.rot;
		if (wantedRot > 180f && curRot > 0f && curRot < 90f)
			curRot += 360f;
		else if (wantedRot < 90f && curRot > 270f)
			curRot -= 360f;
		
		float centeredWant = wantedRot - curRot;
		
		if (centeredWant > 1f) {
			gun.rot += 0.15f * delta;
		} else if (centeredWant < -1f) {
			gun.rot -= 0.15f * delta;
		} else {
			gun.rot = wantedRot;
		}
		
		if (gun.rot > 360f) {
			gun.rot -= 360f;
		}
		if (gun.rot < 0f) {
			gun.rot = 360f - gun.rot * -1;
		}
		
		// Movement of the tank
		float angle = this.rot;
		while (angle > 90f)
			angle -= 90f;
		
		if (dx != 0f) {
			this.x = x + dx * delta;
			dx *= 0.85f;
			
			if (dx < 0.02f && dx > -0.02f)
				dx = 0f;
		}
		if (dy != 0f) {
			this.y = y + dy * delta;
			dy *= 0.85f;
			
			if (dy < 0.02f && dy > -0.02f)
				dy = 0f;
		}
		
		if (wantsToMove) {
			double rotRadians = Math.toRadians(this.rot);
			double newDx = Math.cos(rotRadians) * 0.25f;
			double newDy = Math.sin(rotRadians) * 0.25f;
			
			if (!moveForward) {
				newDx *= -1; // Reverse direction..
				newDy *= -1;
			}
			
			this.dx = (float)newDx;
			this.dy = (float)newDy;
		}
		
		xdff = xDiff; ydff = yDiff;
		
		setGunPos();
		setFrontIdentifierPos();
	}
	
	float xdff = 0f, ydff = 0f;
	
	private void setGunPos () {
		gun.x = this.x + this.w/2;
		gun.y = this.y + this.h/2 - gun.h/2;
	}
	
	private void setFrontIdentifierPos () {
		frontIdentifier.rot = rot;
		frontIdentifier.x = this.x + this.w - frontIdentifier.w;
		frontIdentifier.y = this.y;
	}
	
	@Override
	public void draw () {
		super.draw();
		super.drawBegin();
		super.rotate();
		GL11.glTranslatef(this.w-frontIdentifier.w, 0, 0);
		frontIdentifier.color();
		frontIdentifier.drawVerts();
		super.drawEnd();
		
		gun.draw();
		
		if (statsVisible) {
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			TrueTypeFont font = Main.getInstance().getOpenGL().getFont();
			font.drawString(20, 20, "Gun Rot: "+gun.rot, Color.white);
			font.drawString(20, 50, "Gun XDiff: "+xdff, Color.white);
			font.drawString(20, 80, "Gun YDiff: "+ydff, Color.white);
			font.drawString(20, 110, "Tank X:"+this.x, Color.white);
			font.drawString(20, 140, "Tank Y:"+this.y, Color.white);
			font.drawString(20, 170, "Tank Rot:"+this.rot, Color.white);
			
			float rotatedX = 0f, rotatedY = 0f;
			double rotRadians = Math.toRadians(this.rot);
			double newDx = Math.cos(rotRadians);
			double newDy = Math.sin(rotRadians);
			
			
			font.drawString(20, 200, "Tank RX:"+newDx, Color.white);
			font.drawString(20, 230, "Tank RY:"+newDy, Color.white);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
		}
	}
}
