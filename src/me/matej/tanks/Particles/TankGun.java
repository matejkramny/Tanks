package me.matej.tanks.Particles;

import me.matej.tanks.Main;
import org.lwjgl.input.Mouse;

/**
 *
 * @author matejkramny
 */
public class TankGun extends Object {
	
	private float wantedRot; // wanted rotation angle of the gun. the gun can move at certain speed...
	
	public TankGun () {
		this.centeredRot = false;
	}
	
	@Override
	public void update (int delta) {
		// Handle rotation of gun
		int mx = Mouse.getX(), my = Main.getInstance().getOpenGL().getDisplayMode().getHeight() - Mouse.getY();
		double xDiff = mx - loc.x, yDiff = my - loc.y;
		
		float actual = (float)Math.abs(Math.toDegrees(Math.atan(yDiff / xDiff)));
		
		if (xDiff > 0f && yDiff < 0f)
			actual = 360f - actual;
		else if (xDiff < 0f && yDiff < 0f)
			actual += 180f;
		else if (xDiff < 0f && yDiff > 0f)
			actual = 90f + (90f - actual);
		
		wantedRot = actual;
		
		// Move the gun slowly - to be improved.. Works but not flawless.
		
		double curRot = rot;
		if (wantedRot > 180f && curRot > 0f && curRot < 90f)
			curRot += 360f;
		else if (wantedRot < 90f && curRot > 270f)
			curRot -= 360f;
		
		double centeredWant = wantedRot - curRot;
		
		if (centeredWant > 1f) {
			rot += 0.15f * delta;
		} else if (centeredWant < -1f) {
			rot -= 0.15f * delta;
		} else
			rot = wantedRot;
		
		if (rot > 360f)
			rot -= 360f;
		if (rot < 0f)
			rot = 360f - rot * -1;
	}
	
	@Override
	public void draw () {
		super.draw();
	}
	
}
