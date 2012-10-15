package me.matej.tanks;

import java.awt.Font;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.TrueTypeFont;

/**
 *
 * @author matejkramny
 */
final public class OpenGL {
	private long lastFrame; // UNIX Time at last frame
	private int fps; // Frames per Second
	private int FPS;
	private long lastFPS; // Last UNIX Time FPS was refreshed
	private boolean vsync = true; // VSync enabled/disabled
	private boolean fullscreen = false; // Fullscreen switch
	private boolean running = true; // App shuts down if false
	private DisplayMode displayMode; // Current display mode
	private TrueTypeFont font; // Font used to display text
	private Main main = Main.getInstance();
	
	// Getters for encapsulation
	public int getFps () {
		return FPS;
	}
	public boolean isVsync () {
		return vsync;
	}
	public boolean isFullscreen () {
		return fullscreen;
	}
	public boolean isRunning () {
		return running;
	}
	public DisplayMode getDisplayMode () {
		return displayMode;
	}
	public TrueTypeFont getFont () {
		return font;
	}
	
	public OpenGL () {
		try {
			this.setDisplayMode(new DisplayMode(800, 600), fullscreen);
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace(System.err);
			System.exit(0);
		}
		
		Font awtFont = new Font("Arial", Font.BOLD, 24);
		font = new TrueTypeFont (awtFont, true);
		
		this.initGL();
		this.getDelta();
		lastFPS = this.getTime();
	}
	
	public void startLoop () {
		// Run loop
		while (!Display.isCloseRequested() && running) {
			int delta = this.getDelta();
			
			this.update(delta);
			this.drawGL();
			
			this.updateFPS();
			Display.update();
			Display.sync(100);
		}
		
		Display.destroy();
	}
	
	private void initGL () {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glViewport(0,0,displayMode.getWidth(),displayMode.getHeight());
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, displayMode.getWidth(), displayMode.getHeight(), 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
		Keyboard.enableRepeatEvents(true);
		
		Display.setVSyncEnabled(vsync);
		
		GL11.glClearColor(1f, 1f, 1f, 1.0f);
		GL11.glClearDepth(1);
	}
	
	private void update (int delta) {
		while (Keyboard.next() && Keyboard.getEventKeyState()) {
			int k = Keyboard.getEventKey();
			
			if (k == Keyboard.KEY_ESCAPE)
				running = false;
			/*
			else if (k == Keyboard.KEY_V) {
				vsync = !vsync;
				
				Display.setVSyncEnabled(vsync);
			}
			
			else if (k == Keyboard.KEY_F) { //  main.getState().Splash.done && false
				fullscreen = !fullscreen;
				
				try {
					// Toggle fullscreen mode
					if (fullscreen)
						this.setDisplayMode(Display.getDesktopDisplayMode(), fullscreen);
					else
						this.setDisplayMode(new DisplayMode(800, 600), fullscreen);
				} catch (LWJGLException e) {
					e.printStackTrace(System.err);
				}
			}*/
			else {
				for (int i = 0; i < GameState.values().length; i++) {
					GameState state = GameState.values()[i];
					if (state.getStateInstance().active) {
						state.getStateInstance().keyPressed(k);
					}
				}
			}
		}
		
		while (Mouse.next() && Mouse.getEventButtonState()) {
			int button = Mouse.getEventButton();
			
			for (int i = 0; i < GameState.values().length; i++) {
				GameState state = GameState.values()[i];
				if (state.getStateInstance().active) {
					state.getStateInstance().mouseButtonPressed(button);
				}
			}
		}
		
		main.update (delta);
	}
	
	private void drawGL () {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		main.draw();
	}
	
	private void setDisplayMode (DisplayMode target, boolean fs) throws LWJGLException {
		// DisplayModes are equal - no work necessary
		if (Display.isFullscreen() == fs && this.compareDisplayModes(target, displayMode))
			return;
		
		fullscreen = fs;
		
		Display.setDisplayMode(target);
		Display.setFullscreen(fs);
		
		displayMode = target;
	}
	
	// Returns true when display modes are the same
	public boolean compareDisplayModes (DisplayMode dp1, DisplayMode dp2) {
		if (dp1 == null || dp2 == null)
			return false;
		
		if (dp1.getWidth() == dp2.getWidth() && dp1.getHeight() == dp2.getHeight() && dp1.getBitsPerPixel() == dp2.getBitsPerPixel() && dp1.getFrequency() == dp2.getFrequency())
			return true;
		
		return false;
	}
	
	// Returns time in milliseconds
	public long getTime () {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	// Calculates fps and updates the fps variable
	public void updateFPS () {
		if (getTime() - lastFPS > 1000) {
			FPS = fps;
			fps = 0;
			lastFPS += 1000;
		}
		
		fps++;
	}
	
	// Returns number of milliseconds since last update
	public int getDelta () {
		long time = getTime();
		int delta = (int)(time-lastFrame);
		
		lastFrame = time;
		
		return delta;
	}
}
