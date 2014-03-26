package com.sudosoftware.game;

import org.lwjgl.opengl.GL11;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import com.sudosoftware.game.input.InputHandler;

public abstract class Game {
	// Default screen dimensions.
	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;

	// Game instance.
	private static Game instance = null;

	// The game title.
	protected String gameTitle = "";

	// Screen width and height.
	protected int screenWidth, screenHeight;
	protected DisplayMode windowMode;

	// Input handler.
	protected InputHandler input;
	protected boolean captureMouse = false;

	// Metrics.
	private long lastFrame;
	private int fps;
	private long lastFPS;
	protected boolean showFPS = false;

	// VSync Stuff.
	protected boolean vSyncEnabled = true;
	protected int vSyncFPS = 60;

	public Game() {
		// Set the instance.
		instance = this;

		// Set the width and height defaults.
		this.screenWidth = WIDTH;
		this.screenHeight = HEIGHT;
	}

	protected void start() {
		try {
			System.setProperty("org.lwjgl.opengl.Display.enableHighDPI", "false");

			// Try to set the display mode.
			Display.create();
			Display.setTitle(this.gameTitle + (showFPS ? "  -  FPS: " + 0 : ""));
			setDisplayMode(new DisplayMode(this.screenWidth, this.screenHeight));
			Display.setVSyncEnabled(vSyncEnabled);
			Display.setResizable(true);

			System.out.println("OpenGL: " + GL11.glGetString(GL11.GL_VERSION));

			// Initialize the input handler.
			this.input = new InputHandler(this.captureMouse);

			// Set the focus.
			this.input.hasFocus = true;

			// Start the game loop.
			gameLoop();
		}
		catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public static int getScreenWidth() {
		return instance.screenWidth;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	public static int getScreenHeight() {
		return instance.screenHeight;
	}

	protected void setTitle(String title) {
		this.gameTitle = title;
	}

	protected String getTitle() {
		return this.gameTitle;
	}

	protected void toggleFullscreen() {
		setFullscreen(!Display.isFullscreen());
	}

	protected void setFullscreen(boolean fullscreen) {
		DisplayMode mode = fullscreen ? Display.getDesktopDisplayMode() : windowMode;
		setDisplayMode(mode.getWidth(), mode.getHeight(), fullscreen);
	}

	protected boolean setDisplayMode(DisplayMode mode) {
		return setDisplayMode(mode, false);
	}

	protected boolean setDisplayMode(int width, int height) {
		return setDisplayMode(width, height, false);
	}

	protected boolean setDisplayMode(DisplayMode mode, boolean fullscreen) {
		return setDisplayMode(mode.getWidth(), mode.getHeight(), fullscreen);
	}

	protected boolean setDisplayMode(int width, int height, boolean fullscreen) {
		// Exit if we are already using this width and height and full screen.
		if ((Display.getDisplayMode().getWidth() == width) &&
			(Display.getDisplayMode().getHeight() == height) &&
			(Display.isFullscreen() == fullscreen))
			return true;

		try {
			// Target mode.
			DisplayMode targetMode = null;

			// Check for full screen mode.
			if (fullscreen) {
				// Get the list of available display modes.
				DisplayMode[] modes = Display.getAvailableDisplayModes();
				int freq = 0;

				// Loop through the modes.
				for (DisplayMode current : modes) {
					// Make sure the width and height match.
					if ((current.getWidth() == width) &&
						(current.getHeight() == height)) {
						// Look for a higher frequency.
						if (targetMode == null ||
							current.getFrequency() >= freq) {
							// Check for a mode with better bits per pixel.
							if (targetMode == null ||
								current.getBitsPerPixel() > targetMode.getBitsPerPixel()) {
								// Set the target.
								targetMode = current;
								freq = targetMode.getFrequency();
							}
						}

						// Make sure this is a good fit.
						if (current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel() &&
							current.getFrequency() == Display.getDesktopDisplayMode().getFrequency()) {
							targetMode = current;
							break;
						}
					}
				}
			}
			else {
				// Use the width and height.
				targetMode = new DisplayMode(width, height);
			}

			if (targetMode == null) {
				System.out.println("Failed to find display mode: " + width + "x" + height + " [fullscreen: " + fullscreen + "]");
				return false;
			}

			// Set the display mode.
			Display.setDisplayMode(targetMode);
			if (!fullscreen) windowMode = targetMode;
			Display.setFullscreen(fullscreen);

			System.out.println("Display Mode: " + targetMode.toString());

			// Resized.
			instance.resized();

			// Mode set.
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	private void gameLoop() {
		// Start timing.
		computeDelta();
		this.lastFPS = getTime();

		// Do the initialization.
		init();

		// Start the loop.
		int delta;
		while (!Display.isCloseRequested()) {
			// Update the delta.
			delta = computeDelta();

			// Get any input.
			this.input.pollInput();

			// Update and render.
			update(delta);
			render();
			updateFPS();

			// Update the display.
			Display.update();

			// Check for resizing.
			if (Display.wasResized())
				resized();

			if (vSyncEnabled)
				// Sync to requested frames per second.
				Display.sync(vSyncFPS);
		}

		// End the game.
		end();
	}

	public static void end() {
		instance.dispose();
		Display.destroy();
		Keyboard.destroy();
		Mouse.destroy();
		instance = null;
		System.exit(0);
	}

	private int computeDelta() {
		long time = getTime();
		int delta = (int) (time - this.lastFrame);
		this.lastFrame = time;

		return delta;
	}

	private long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}

	private void updateFPS() {
		if (getTime() - this.lastFPS > 1000) {
			Display.setTitle(this.gameTitle + (this.showFPS ? "  -  FPS: " + this.fps : ""));
			this.fps = 0;
			this.lastFPS += 1000;
		}
		this.fps++;
	}

	public abstract void init();

	public abstract void update(long delta);

	public abstract void render();

	public abstract void resized();

	public abstract void dispose();
}
