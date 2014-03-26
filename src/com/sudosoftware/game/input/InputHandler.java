package com.sudosoftware.game.input;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.sudosoftware.game.Game;

public class InputHandler implements FocusListener {
	// Do we have focus?
	public boolean hasFocus = false;

	// Allow mouse capture so it can't leave the screen?
	public boolean captureMouse = false;

	// Input holders.
	public boolean[] key = new boolean[68836];
	public int mouseDX, mouseDY, mouseDScroll;

	public InputHandler() {
		this(false);
	}

	public InputHandler(boolean captureMouse) {
		// Create the mouse.
		try {
			Mouse.create();
			Keyboard.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		// Bind to the window.
		Mouse.setClipMouseCoordinatesToWindow(captureMouse);
		Mouse.setCursorPosition(Game.getScreenWidth() / 2, Game.getScreenHeight() / 2);
		Mouse.setGrabbed(captureMouse);
	}

	public void pollInput() {
		// Check to see if we need to regain focus.
		if (Mouse.isButtonDown(0) && !hasFocus) {
			hasFocus = true;

			// Bind to the window.
			Mouse.setClipMouseCoordinatesToWindow(captureMouse);
			Mouse.setCursorPosition(Game.getScreenWidth() / 2, Game.getScreenHeight() / 2);
			Mouse.setGrabbed(captureMouse);
		}

		if (hasFocus) {
			// Get the mouse movement.
			this.mouseDX = Mouse.getDX();
			this.mouseDY = Mouse.getDY();

			// Look for mouse scrolling.
			this.mouseDScroll = Mouse.getDWheel();

			// Look for key press/releases.
			while (Keyboard.next()) {
				this.key[Keyboard.getEventKey()] = Keyboard.getEventKeyState();
			}
		}
	}

	@Override
	public void focusLost(FocusEvent event) {
		dropFocus();
	}

	@Override
	public void focusGained(FocusEvent event) {}

	public void dropFocus() {
		this.hasFocus = false;
		for (int i = 0; i < this.key.length; i++) {
			this.key[i] = false;
		}
		this.mouseDX = 0;
		this.mouseDY = 0;
		this.mouseDScroll = 0;
	
		// Unbind from window.
		Mouse.setClipMouseCoordinatesToWindow(false);
		Mouse.setGrabbed(false);
	}
}
