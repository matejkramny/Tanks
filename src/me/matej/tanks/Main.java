package me.matej.Tanks;

/**
 *
 * @author matejkramny
 */
public class Main {
	
	private static Main singleton; // Singleton instance
	
	private OpenGL openGL;
	
	public boolean gamePaused = true;
	
	protected Main () { } // Prevents instantiation
	
	public static Main getInstance () {
		if (singleton == null)
			singleton = new Main();
		
		return singleton;
	}
	
	private void run () {
		openGL = new OpenGL();
		
		//GameState.Splash.getStateInstance().init();
		//GameState.Splash.getStateInstance().active = true;
		
		GameState.Game.getStateInstance().init();
		GameState.Game.getStateInstance().active = true;
		
		openGL.startLoop();
	}
	
	public void draw () {
		for (int i = 0; i < GameState.values().length; i++) {
			GameState state = GameState.values()[i];
			if (state.getStateInstance().active) {
				state.getStateInstance().draw();
				break;
			}
		}
	}
	
	public void update (int delta) {
		for (int i = 0; i < GameState.values().length; i++) {
			GameState state = GameState.values()[i];
			if (state.getStateInstance().active) {
				state.getStateInstance().update(delta);
				break;
			}
		}
	}
	
	public OpenGL getOpenGL () {
		return openGL;
	}
	
	public static void main (String[] args) {
		Main.getInstance().run(); // Creates singleton and starts
	}
}
