package me.matej.tanks.Particles;

import java.util.Random;

/**
 *
 * @author matejkramny
 */
public class Vector {
	public double x, y;
	
	public Vector () {
		
	}
	
	public Vector (double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector scale(double scale) {
		return new Vector(x * scale, y * scale);
	}

	public Vector multiply(Vector other) {
		return new Vector(x * other.x, y * other.y);
	}

	public Vector perp() {
		Vector vect = new Vector(-y, x);
		return vect;// y = -y;
	}

	public void randomize(Vector max, Vector min) { //randomize position
		Random r = new Random();
		x = (r.nextFloat() * (max.x - min.x) + min.x);
		y = (r.nextFloat() * (max.y - min.y) + min.y);
	}

	public double dot(Vector axis) { //dot product
		return x * axis.x + y * axis.y;
	}
}
