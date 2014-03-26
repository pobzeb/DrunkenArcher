package com.sudosoftware.game.utils;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.Display;

public class OrthoCamera extends Camera {
	// Default camera properties.
	public static final float LEFT = 0f;
	public static final float RIGHT = (float) Display.getWidth();
	public static final float BOTTOM = (float) Display.getHeight();
	public static final float TOP = 0f;
	public static final float Z_NEAR = 1f;
	public static final float Z_FAR = 100f;

	// Scaling (Zooming).
	private float scale = 1f;
	private float maxScale = 1f;
	private float minScale = 1f;

	public OrthoCamera() {
		this(LEFT, RIGHT, BOTTOM, TOP, Z_NEAR, Z_FAR);
	}

	public OrthoCamera(float left, float right, float bottom, float top, float zNear, float zFar) {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(left, right, bottom, top, zNear, zFar);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
	}

	@Override
	public void apply() {
		glTranslatef(-this.position.x, -this.position.y, this.position.z);
		glScalef(this.scale, this.scale, 0);
		System.out.println("Position: " + this.position);
	}

	public void scale(float scale) {
		this.scale += scale;

		// Don't scale past the min or max allowed.
		if (this.scale < this.minScale) this.scale = this.minScale;
		else if (this.scale > this.maxScale) this.scale = this.maxScale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public float getScale() {
		return this.scale;
	}

	public void setMinScale(float minScale) {
		this.minScale = minScale;
	}

	public float getMinScale() {
		return this.minScale;
	}

	public void setMaxScale(float maxScale) {
		this.maxScale = maxScale;
	}

	public float getMaxScale() {
		return this.maxScale;
	}
}
