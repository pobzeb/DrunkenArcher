package com.sudosoftware.game.utils;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class BufferTools {
	public static FloatBuffer asFlippedFloatBuffer(float... values) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(values.length);
		buffer.put(values);
		buffer.flip();
		return buffer;
	}	
}