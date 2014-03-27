package com.sudosoftware.drunkenarcher;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import com.sudosoftware.drunkenarcher.world.Block;
import com.sudosoftware.drunkenarcher.world.Chunk;
import com.sudosoftware.drunkenarcher.world.World;
import com.sudosoftware.game.Game;
import com.sudosoftware.game.utils.BufferTools;

public class DrunkenArcher extends Game {
	// Game name.
	private static final String TITLE = "Drunken Archer";

	// Default screen size.
	private static final int WIDTH = 1080;
	private static final int HEIGHT = WIDTH * 9 / 16;

	// Fog settings.
	private static final float Z_FOG_NEAR = 40f;
	private static final float Z_FOG_FAR = 350f;

	// Flag to determine if we want to render wire frame.
	private boolean renderWireframe = false;

	public DrunkenArcher() {
		// Set the title and screen dimensions.
		this.setTitle(TITLE);
		this.setScreenWidth(WIDTH);
		this.setScreenHeight(HEIGHT);

		// We want to capture the mouse in the window.
		this.captureMouse = true;

		// Show FPS in the title.
		this.showFPS = true;

		// Update vSync stuff.
		this.vSyncEnabled = false;

		// Start the game.
		start();
	}

	@Override
	public void init() {
		// Initialize OpenGL.
		initOpenGL();

		// Create the world.
		World.getInstance().generate();
	}

	private void initOpenGL() {
		// Set up openGL.
		glShadeModel(GL_SMOOTH);
		glClearDepth(1.0f);
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		glCullFace(GL_BACK);
		glEnable(GL_CULL_FACE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
//		glEnable(GL_BLEND);
//		glEnable(GL_ALPHA_TEST);
//		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
//		glLight(GL_LIGHT1, GL_AMBIENT, BufferTools.asFlippedFloatBuffer(new float[]{0.75f, 0.75f, 0.75f, 0.5f}));
//		glLight(GL_LIGHT1, GL_DIFFUSE, BufferTools.asFlippedFloatBuffer(new float[]{1.0f, 1.0f, 1.0f, 1.0f}));
//		glLight(GL_LIGHT1, GL_POSITION, BufferTools.asFlippedFloatBuffer(new float[]{0.0f, 10.0f, 0.0f, 0.3f}));
//		glEnable(GL_LIGHT1);
//		glEnable(GL_LIGHTING);

		// Create fog.
		glEnable(GL_FOG); {
			FloatBuffer fogColors = BufferUtils.createFloatBuffer(4);
			fogColors.put(new float[] { 0.3f, 0.6f, 0.6f, 1.0f });
			glClearColor(0.1f, 0.1f, 0.1f, 1f);
			fogColors.flip();

			glFog(GL_FOG_COLOR, fogColors);
			glFogi(GL_FOG_MODE, GL_LINEAR);
			glHint(GL_FOG_HINT, GL_NICEST);
			glFogf(GL_FOG_START, Z_FOG_NEAR);
			glFogf(GL_FOG_END, Z_FOG_FAR);
			glFogf(GL_FOG_DENSITY, 0.03f);
		}

		// Clear the background.
		glClearColor(0.39f, 0.58f, 0.929f, 1.0f);
	}

	@Override
	public void update(long delta) {
		// Check to see if we have focus.
		if (this.input.hasFocus) {
			// Check for escape key.
			if (this.input.key[Keyboard.KEY_ESCAPE]) {
				// Drop focus.
				this.input.dropFocus();
			}

			// Check for F11 key.
			if (this.input.key[Keyboard.KEY_P]) {
				// Turn off the key.
				this.input.key[Keyboard.KEY_P] = false;

				// Toggle full screen.
				this.toggleFullscreen();
			}

			// Check for tab key.
			if (this.input.key[Keyboard.KEY_TAB]) {
				// Turn off the key.
				this.input.key[Keyboard.KEY_TAB] = false;

				// Toggle wire frame.
				this.renderWireframe = !this.renderWireframe;
			}

			// Check for f key to toggle flying.
			if (this.input.key[Keyboard.KEY_F]) {
				// Turn off the key.
				this.input.key[Keyboard.KEY_F] = false;

				// Toggle flying.
				World.getInstance().player.setFlyingAllowed(!World.getInstance().player.isFlyingAllowed());
			}

			// Do player input.
			World.getInstance().player.input(this.input);

			// Update the world.
			World.getInstance().update(delta);
		}
	}

	@Override
	public void render() {
		// Clear the screen and depth buffer.
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		// Load the identity for a base point to start.
		glLoadIdentity();

		// Allow wire frame rendering.
		if (this.renderWireframe) {
			glLineWidth(2.0f);
			glPolygonMode(GL_FRONT, GL_LINE);
		}
		else {
			glLineWidth(1.0f);
			glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		}

		// Render the world.
		World.getInstance().render();

		// Display player position.
		Vector3f playerPos = World.getInstance().player.getPosition();
		setTitle(TITLE + "  -  Player: [x: " + (int)Math.abs(playerPos.x / Block.BLOCK_SIZE) + ", y: " + (int)Math.abs(playerPos.y / Block.BLOCK_SIZE) + ", z: " + (int)Math.abs(playerPos.z / Block.BLOCK_SIZE) + ", yaw: " + World.getInstance().player.getYaw() + ", pitch: " + World.getInstance().player.getPitch() + "]");

		// Render the GUI.
//		renderGUI();
	}

	public void renderGUI() {
		glDisable(GL_DEPTH_TEST);
		glMatrixMode(GL_PROJECTION);
		glPushMatrix();
		glLoadIdentity();
		glOrtho(-1, 1, 1, -1, -1, 1);
		glMatrixMode(GL_MODELVIEW);
		glPushMatrix();
		glLoadIdentity();

//		glColor3f(1.0f, 0.0f, 0.0f);
//		glBegin(GL_QUADS); {
//			glVertex2f(0.0f, 0.0f);
//			glVertex2f(0.5f, 0.0f);
//			glVertex2f(0.5f, 0.5f);
//			glVertex2f(0.0f, 0.5f);
//		}
//		glEnd();

		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);
		glPopMatrix();
		glEnable(GL_DEPTH_TEST);
	}

	@Override
	public void resized() {
		glViewport(0, 0, (int)(Display.getWidth() * Display.getPixelScaleFactor()), (int)(Display.getHeight() * Display.getPixelScaleFactor()));
	}

	@Override
	public void dispose() {
		// Discard any resources.
		World.getInstance().dispose();
	}

	public static void main(String[] args) {
		new DrunkenArcher();
	}
}
