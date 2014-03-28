package com.sudosoftware.drunkenarcher.world;

import java.nio.FloatBuffer;

public class Block {

	// Define the block sizes.
	public static final float BLOCK_SIZE = 2.0f;

	// Define the texture rows/columns.
	public static final int TEXTURES_PER_ROW_COL = 16;

	// Define the vertex count, texture coord count.
	public static final int VERTEX_SIZE = 3;
	public static final int TEXTURE_COORD_SIZE = 2;

	public static void addToVBO(byte blockTypeID, float x, float y, float z, FloatBuffer buffer) {
		// Get the block type.
		BlockType blockType = BlockType.findByID(blockTypeID);

		// Set the offset to half of the block size. This puts
		// x, y and z in the center of the block.
		float offset = BLOCK_SIZE / 2;

		// Get the side texture mapping.
		UVMap side = new UVMap(blockType.textureIDSide);
		UVMap top = new UVMap(blockType.textureIDTop);
		UVMap bottom = new UVMap(blockType.textureIDBottom);

		// Vertices, color and texture mapping.
		// Front Face.
		buffer.put(x-offset).put(y-offset).put(z+offset).put(side.u0).put(side.v1);
		buffer.put(x+offset).put(y-offset).put(z+offset).put(side.u1).put(side.v1);
		buffer.put(x+offset).put(y+offset).put(z+offset).put(side.u1).put(side.v0);
		buffer.put(x-offset).put(y+offset).put(z+offset).put(side.u0).put(side.v0);

		// Back Face.
		buffer.put(x+offset).put(y-offset).put(z-offset).put(side.u0).put(side.v1);
		buffer.put(x-offset).put(y-offset).put(z-offset).put(side.u1).put(side.v1);
		buffer.put(x-offset).put(y+offset).put(z-offset).put(side.u1).put(side.v0);
		buffer.put(x+offset).put(y+offset).put(z-offset).put(side.u0).put(side.v0);

		// Left Face.
		buffer.put(x-offset).put(y-offset).put(z-offset).put(side.u0).put(side.v1);
		buffer.put(x-offset).put(y-offset).put(z+offset).put(side.u1).put(side.v1);
		buffer.put(x-offset).put(y+offset).put(z+offset).put(side.u1).put(side.v0);
		buffer.put(x-offset).put(y+offset).put(z-offset).put(side.u0).put(side.v0);

		// Right Face.
		buffer.put(x+offset).put(y-offset).put(z+offset).put(side.u0).put(side.v1);
		buffer.put(x+offset).put(y-offset).put(z-offset).put(side.u1).put(side.v1);
		buffer.put(x+offset).put(y+offset).put(z-offset).put(side.u1).put(side.v0);
		buffer.put(x+offset).put(y+offset).put(z+offset).put(side.u0).put(side.v0);

		// Top Face.
		buffer.put(x-offset).put(y+offset).put(z+offset).put(top.u0).put(top.v1);
		buffer.put(x+offset).put(y+offset).put(z+offset).put(top.u1).put(top.v1);
		buffer.put(x+offset).put(y+offset).put(z-offset).put(top.u1).put(top.v0);
		buffer.put(x-offset).put(y+offset).put(z-offset).put(top.u0).put(top.v0);

		// Bottom Face.
		buffer.put(x-offset).put(y-offset).put(z-offset).put(bottom.u0).put(bottom.v1);
		buffer.put(x+offset).put(y-offset).put(z-offset).put(bottom.u1).put(bottom.v1);
		buffer.put(x+offset).put(y-offset).put(z+offset).put(bottom.u1).put(bottom.v0);
		buffer.put(x-offset).put(y-offset).put(z+offset).put(bottom.u0).put(bottom.v0);
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
