package me.matej.tanks.gameStates;

/**
 *
 * @author matejkramny
 */
public interface GameStateInterface {
	public void draw ();
	public void update (int delta);
	public void keyPressed (int key);
	public void mouseButtonPressed (int index);
	public void init ();
}
