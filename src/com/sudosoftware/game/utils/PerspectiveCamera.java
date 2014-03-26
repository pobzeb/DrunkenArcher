package com.sudosoftware.game.utils;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;

public class PerspectiveCamera extends Camera {
	// Default camera properties.
	public static final float FOV = 70f;
	public static final float ASPECT = (float) Display.getWidth() / (float) Display.getHeight();
	public static final float Z_NEAR = 1f;
	public static final float Z_FAR = 100f;

	// Default max and min pitch.
	public static final float MAX_PITCH = 80;
	public static final float MIN_PITCH = -80;

	// The max and min pitch.
	protected float maxPitch;
	protected float minPitch;

	// Define a rotation vector.
	protected Vector3f rotation = new Vector3f();

	// Define the camera properties.
	protected float fov;	// Field of view
	protected float aspect;	// Aspect ratio (w / h)
	protected float zNear;	// Near clipping plane
	protected float zFar;	// Far clipping plane

	// Allow flying?
	private boolean flyingAllowed = false;

	public PerspectiveCamera() {
		this(FOV, ASPECT, Z_NEAR, Z_FAR);
	}

	public PerspectiveCamera(float fov, float aspect, float zNear, float zFar) {
		// Set the camera properties.
		this.fov = fov;
		this.aspect = aspect;
		this.zNear = zNear;
		this.zFar = zFar;

		// Update the camera.
		updateCameraProperties();

		// Set the max and min pitch to the defaults.
		this.maxPitch = MAX_PITCH;
		this.minPitch = MIN_PITCH;

		// Enable depth testing.
		glEnable(GL_DEPTH_TEST);
	}

	private void updateCameraProperties() {
		// Load the projection and model views.
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
        GLU.gluPerspective(fov, aspect, zNear, zFar);
        glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
	}

	@Override
	public void apply() {
		// Rotate the camera.
		glRotatef(rotation.x, 1, 0, 0);
		glRotatef(rotation.y, 0, 1, 0);
		glRotatef(rotation.z, 0, 0, 1);

		// Move the camera.
		glTranslatef(position.x, position.y, position.z);
	}

	public void move(float amount, float angle) {
		// Move according to both the rotation and amount.
		position.z += amount * Math.sin(Math.toRadians(rotation.y + 90 * angle));
		if (flyingAllowed) {
			position.y += amount * Math.tan(Math.toRadians(rotation.x * angle));
		}
		position.x += amount * Math.cos(Math.toRadians(rotation.y + 90 * angle));
	}

	public void addRotation(float rx, float ry, float rz) {
		// Add the rotation amounts.
		rotation.x += rx;
		rotation.y += ry;
		rotation.z += rz;

		// We only want the horizontal rotation to be between 0 and 360 degrees.
		rotation.y = rotation.y % 360f;
		if (rotation.y < 0) rotation.y = 360 + rotation.y;

		// Don't allow the vertical rotation to move past the min and max.
		if (rotation.x > maxPitch) rotation.x = maxPitch;
		else if (rotation.x < minPitch) rotation.x = minPitch;
	}

	public void addRotation(Vector3f rotation) {
		addRotation(rotation.x, rotation.y, rotation.z);
	}

	public void setRotation(float rx, float ry, float rz) {
		rotation.x = rx;
		rotation.y = ry;
		rotation.z = rz;
	}

	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public float getFieldOfView() {
		return fov;
	}

	public void setFieldOfView(float fov) {
		this.fov = fov;

		// Update the camera.
		updateCameraProperties();
	}

	public float getAspectRatio() {
		return aspect;
	}

	public void setAspectRatio(float aspect) {
		this.aspect = aspect;

		// Update the camera.
		updateCameraProperties();
	}

	public float getNearPlane() {
		return zNear;
	}

	public void setNearPlane(float zNear) {
		this.zNear = zNear;

		// Update the camera.
		updateCameraProperties();
	}

	public float getFarPlane() {
		return zFar;
	}

	public void setFarPlane(float zFar) {
		this.zFar = zFar;

		// Update the camera.
		updateCameraProperties();
	}

	public float getMaxPitch() {
		return maxPitch;
	}

	public void setMaxPitch(float maxPitch) {
		this.maxPitch = maxPitch;
	}

	public float getMinPitch() {
		return minPitch;
	}

	public void setMinPitch(float minPitch) {
		this.minPitch = minPitch;
	}

	public boolean isFlyingAllowed() {
		return flyingAllowed;
	}

	public void setFlyingAllowed(boolean flyingAllowed) {
		this.flyingAllowed = flyingAllowed;
	}
}
