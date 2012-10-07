package me.matej.tanks.Particles;

import java.text.DecimalFormat;
import me.matej.tanks.GameState;
import me.matej.tanks.Main;
import me.matej.tanks.gameStates.Game;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

/**
 *
 * @author matejkramny
 */
public class Tank extends Object {
	public Object gun; // Rotating independant of body
	
	private Object frontIdentifier; // Small different-colored square that marks the fron of the tank
	
	private float dx, dy; // Last directions.
	
	private volatile boolean statsVisible = false;
	
	private volatile short speedMultiplier = 1;
	
	public Tank () {
		this.init(50, 50, 50, 25, 1, 1, 1);
		gun = new TankGun().init(75, 8, 35, 4, 0.5f, 0.5f, 0.5f);
		
		frontIdentifier = new Object().init(0, 0, 3, 25, 0, 1, 0);
		frontIdentifier.centeredRot = false;
		
		this.setGunPos();
		loc.x = 150;
		loc.y = 150;
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
		
		gun.update(delta);
		
		// Movement of the tank
		double angle = this.rot;
		while (angle > 90f)
			angle -= 90f;
		
		if (dx != 0f) {
			loc.x = loc.x + dx * delta;
			dx *= 0.85f;
			
			if (dx < 0.02f && dx > -0.02f)
				dx = 0f;
		}
		if (dy != 0f) {
			loc.y = loc.y + dy * delta;
			dy *= 0.85f;
			
			if (dy < 0.02f && dy > -0.02f)
				dy = 0f;
		}
		
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
		}
		
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
		super.drawBegin();
		super.rotate();
		GL11.glTranslated(this.w-frontIdentifier.w, 0, 0);
		frontIdentifier.color();
		frontIdentifier.drawVerts();
		super.drawEnd();
		
		gun.draw();
		
		if (statsVisible) {
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			
			TrueTypeFont font = Main.getInstance().getOpenGL().getFont();
			DecimalFormat format = new DecimalFormat("##.00");
			
			font.drawString(20, 20, "Gun Rot: ", Color.white); font.drawString(150, 20, format.format(gun.rot), Color.white);
			font.drawString(20, 50, "Tank X:", Color.white); font.drawString(150, 50, format.format(loc.x), Color.white);
			font.drawString(20, 80, "Tank Y:", Color.white); font.drawString(150, 80, format.format(loc.y), Color.white);
			font.drawString(20, 110, "Tank Rot:", Color.white); font.drawString(150, 110, format.format(this.rot), Color.white);
			font.drawString(20, 140, "FPS:", Color.white); font.drawString(150, 140, ""+Main.getInstance().getOpenGL().getFps(), Color.white);
			
			
			GL11.glDisable(GL11.GL_TEXTURE_2D);
		}
	}
}
