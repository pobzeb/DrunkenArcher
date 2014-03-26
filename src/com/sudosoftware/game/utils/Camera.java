package com.sudosoftware.game.utils;

import org.lwjgl.util.vector.Vector3f;

public abstract class Camera {

	protected Vector3f position = new Vector3f();

	public abstract void apply();

	public void addPosition(float x, float y, float z) {
		// Add the position amounts.
		position.x += x;
		position.y += y;
		position.z += z;
	}

	public void addPosition(Vector3f position) {
		addPosition(position.x, position.y, position.z);
	}

	public void setPosition(float x, float y, float z) {
		position.x = x;
		position.y = y;
		position.z = z;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Vector3f getPosition() {
		return position;
	}
}
