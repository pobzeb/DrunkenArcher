package com.sudosoftware.drunkenarcher.world;

import static org.lwjgl.opengl.GL11.*;

import java.nio.FloatBuffer;

public class Block {

	// Define the block sizes.
	public static final float BLOCK_SIZE = 2.0f;

	// Define the texture rows/columns.
	public static final int TEXTURES_PER_ROW_COL = 16;

	// Define the vertex count, texture coord count.
	public static final int VERTEX_SIZE = 3;
	public static final int TEXTURE_COORD_SIZE = 2;

	public static void build(byte blockTypeID, float x, float y, float z, FloatBuffer buffer) {
		// Get the block type.
		BlockType blockType = BlockType.findByID(blockTypeID);

		// Set the offset to half of the block size. This puts
		// x, y and z in the center of the block.
		float offset = BLOCK_SIZE / 2;

		// Get the side texture mapping.
		UVMap side = new UVMap(blockType.textureIDSide);
		UVMap top = new UVMap(blockType.textureIDTop);
		UVMap bottom = new UVMap(blockType.textureIDBottom);

		// Vertices and texture mapping.
		// Front Face.
		if (buffer == null) glNormal3f(0.0f, 0.0f, 1.0f);
		doVertex(x-offset, y-offset, z+offset, side.u1, side.v1, buffer);
		doVertex(x+offset, y-offset, z+offset, side.u0, side.v1, buffer);
		doVertex(x+offset, y+offset, z+offset, side.u0, side.v0, buffer);
		doVertex(x-offset, y+offset, z+offset, side.u1, side.v0, buffer);

		// Back Face.
		if (buffer == null) glNormal3f(0.0f, 0.0f, -1.0f);
		doVertex(x-offset, y-offset, z-offset, side.u0, side.v1, buffer);
		doVertex(x-offset, y+offset, z-offset, side.u0, side.v0, buffer);
		doVertex(x+offset, y+offset, z-offset, side.u1, side.v0, buffer);
		doVertex(x+offset, y-offset, z-offset, side.u1, side.v1, buffer);

		// Left Face.
		if (buffer == null) glNormal3f(-1.0f, 0.0f, 0.0f);
		doVertex(x-offset, y-offset, z-offset, side.u1, side.v1, buffer);
		doVertex(x-offset, y-offset, z+offset, side.u0, side.v1, buffer);
		doVertex(x-offset, y+offset, z+offset, side.u0, side.v0, buffer);
		doVertex(x-offset, y+offset, z-offset, side.u1, side.v0, buffer);

		// Right Face.
		if (buffer == null) glNormal3f(1.0f, 0.0f, 0.0f);
		doVertex(x+offset, y-offset, z-offset, side.u0, side.v1, buffer);
		doVertex(x+offset, y+offset, z-offset, side.u0, side.v0, buffer);
		doVertex(x+offset, y+offset, z+offset, side.u1, side.v0, buffer);
		doVertex(x+offset, y-offset, z+offset, side.u1, side.v1, buffer);

		// Top Face.
		if (buffer == null) glNormal3f(0.0f, 1.0f, 0.0f);
		doVertex(x-offset, y+offset, z-offset, top.u0, top.v1, buffer);
		doVertex(x-offset, y+offset, z+offset, top.u0, top.v0, buffer);
		doVertex(x+offset, y+offset, z+offset, top.u1, top.v0, buffer);
		doVertex(x+offset, y+offset, z-offset, top.u1, top.v1, buffer);

		// Bottom Face.
		if (buffer == null) glNormal3f(0.0f, -1.0f, 0.0f);
		doVertex(x-offset, y-offset, z-offset, bottom.u1, bottom.v1, buffer);
		doVertex(x+offset, y-offset, z-offset, bottom.u0, bottom.v1, buffer);
		doVertex(x+offset, y-offset, z+offset, bottom.u0, bottom.v0, buffer);
		doVertex(x-offset, y-offset, z+offset, bottom.u1, bottom.v0, buffer);
	}

	private static void doVertex(float x, float y, float z, float u, float v, FloatBuffer buffer) {
		if (buffer == null) {
			glTexCoord2f(u, v);
			glVertex3f(x, y, z);
		}
		else {
			buffer.put(x).put(y).put(z).put(u).put(v);
		}
	}

	private static class UVMap {
		public float u0, u1, v0, v1;

		public UVMap(float[] textureID) {
			float textureDim = (1.0f / TEXTURES_PER_ROW_COL);

			// Calculate the uv points.
			u0 = textureID[0] / TEXTURES_PER_ROW_COL;
			v0 = textureID[1] / TEXTURES_PER_ROW_COL;
			u1 = u0 + textureDim;
			v1 = v0 + textureDim;
		}
	}
}
