package com.sudosoftware.drunkenarcher.entity;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import com.sudosoftware.game.input.InputHandler;
import com.sudosoftware.game.utils.PerspectiveCamera;

public class Player extends PerspectiveCamera {
	// Movement and rotation speeds.
	public static final float MOVE_SPEED = 1.19f;
	public static final float ROTATION_SPEED = 0.3f;

	public Player() {
		super(FOV, ASPECT, Z_NEAR, Z_FAR);
	}

	public Player(Vector3f position) {
		super(FOV, ASPECT, Z_NEAR, Z_FAR);

		// Set up the initial position.
		this.setPosition(position);
	}

	public Player(Vector3f position, Vector3f rotation) {
		super(FOV, ASPECT, Z_NEAR, Z_FAR);

		// Set up the initial position and rotation.
		this.setPosition(position);
		this.setRotation(rotation);
	}

	public float getPitch() {
		return this.getRotation().x;
	}

	public float getYaw() {
		return this.getRotation().y;
	}

	public float getRoll() {
		return this.getRotation().z;
	}

	public void jump(float moveSpeed) {
		if (this.isFlyingAllowed()) {
			this.addPosition(0, -moveSpeed, 0);
		}
	}

	public void duck(float moveSpeed) {
		if (this.isFlyingAllowed()) {
			this.addPosition(0, moveSpeed, 0);
		}
	}

	public void input(InputHandler input) {
		// Check for input we care about.
		boolean moveForward = input.key[Keyboard.KEY_W];
		boolean moveBackward = input.key[Keyboard.KEY_S];
		boolean strafeLeft = input.key[Keyboard.KEY_A];
		boolean strafeRight = input.key[Keyboard.KEY_D];
		boolean lookUp = input.key[Keyboard.KEY_UP];
		boolean lookDown = input.key[Keyboard.KEY_DOWN];
		boolean turnLeft = input.key[Keyboard.KEY_LEFT];
		boolean turnRight = input.key[Keyboard.KEY_RIGHT];
		boolean jump = input.key[Keyboard.KEY_SPACE];
		boolean duck = input.key[Keyboard.KEY_LCONTROL];

		// Look for mouse movement.
		if (input.mouseDX < 0) {
			turnLeft = true;
			turnRight = false;
		}
		if (input.mouseDX > 0) {
			turnLeft = false;
			turnRight = true;
		}
		if (input.mouseDY > 0) {
			lookUp = true;
			lookDown = false;
		}
		if (input.mouseDY < 0) {
			lookUp = false;
			lookDown = true;
		}

		// Update the mouse movement speed.
		float mouseSpeedX = Math.abs(input.mouseDX);
		float mouseSpeedY = Math.abs(input.mouseDY);

		// Update the rotation speed.
		float rotationSpeedX = ROTATION_SPEED;
		rotationSpeedX = (mouseSpeedX != 0.0f ? 0.15f * mouseSpeedX : (rotationSpeedX * (input.key[Keyboard.KEY_LSHIFT] ? 2.0f : 1.0f)));
		float rotationSpeedY = ROTATION_SPEED;
		rotationSpeedY = (mouseSpeedY != 0.0f ? 0.15f * mouseSpeedY : (rotationSpeedY * (input.key[Keyboard.KEY_LSHIFT] ? 2.0f : 1.0f)));

		// Update the move speed.
		float moveSpeed = calculateMoveSpeed(input.key[Keyboard.KEY_LSHIFT]);

		// Move based on the input.
		if (moveForward) {
			move(moveSpeed, 1);
		}
		else if (moveBackward) {
			move(-moveSpeed, 1);
		}
		if (strafeLeft) {
			move(moveSpeed, 0);
		}
		else if (strafeRight) {
			move(-moveSpeed, 0);
		}
		if (jump) jump(moveSpeed);
		else if (duck) duck(moveSpeed);

		// Do rotation.
		if (lookUp) addRotation(-rotationSpeedY, 0, 0);
		else if (lookDown) addRotation(rotationSpeedY, 0, 0);
		if (turnLeft) addRotation(0, -rotationSpeedX, 0);
		else if (turnRight) addRotation(0, rotationSpeedX, 0);
	}

	private float calculateMoveSpeed(boolean pressingShift) {
		// Calculate the move speed.
		return MOVE_SPEED * (pressingShift ? 2.0f : 1.0f);
	}

	public void render() {
		// Apply position change and rotation.
		this.apply();
	}
}
