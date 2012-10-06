package me.matej.Tanks;

import me.matej.Tanks.gameStates.*;

/**
 *
 * @author matejkramny
 */
public enum GameState {
	Splash (new SplashScreen()), MainMenu(new MainMenu()), Game(new Game()), GameMenu(new GameMenu()), Options(new Options());
	
	private GameStateClass stateInstance;
	
	GameState () { }
	GameState (GameStateClass stateInstance) {
		this.stateInstance = stateInstance;
	}
	
	public void setStateInstance (GameStateClass stateInstance) {
		this.stateInstance = stateInstance;
	}
	public GameStateClass getStateInstance () {
		return stateInstance;
	}
}
