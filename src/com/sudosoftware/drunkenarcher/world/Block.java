package com.sudosoftware.drunkenarcher.world;

import java.nio.FloatBuffer;

public class Block {

	// Define the block sizes.
	public static final float BLOCK_SIZE = 2.0f;

	// Define the texture dimensions.
	public static final float TEXTURE_DIM = 1.0f / 8.0f;

	// Define the vertex count, texture coord count.
	public static final int VERTEX_SIZE = 3;
	public static final int TEXTURE_COORD_SIZE = 2;

	public static void addToVBO(byte blockTypeID, float x, float y, float z, FloatBuffer buffer) {
		// Get the block type.
		BlockType blockType = BlockType.findByID(blockTypeID);

		// Set the offset to half of the block size. This puts
		// x, y and z in the center of the block.
		float offset = BLOCK_SIZE / 2;

		// Vertices, color and texture mapping.
		// Front Face.
		buffer.put(x-offset).put(y-offset).put(z+offset).put((0 + blockType.uv[0]) * TEXTURE_DIM).put((1 + blockType.uv[1]) * TEXTURE_DIM);
		buffer.put(x+offset).put(y-offset).put(z+offset).put((1 + blockType.uv[0]) * TEXTURE_DIM).put((1 + blockType.uv[1]) * TEXTURE_DIM);
		buffer.put(x+offset).put(y+offset).put(z+offset).put((1 + blockType.uv[0]) * TEXTURE_DIM).put((0 + blockType.uv[1]) * TEXTURE_DIM);
//		buffer.put(x-offset).put(y-offset).put(z+offset).put((0 + blockType.uv[0]) * TEXTURE_DIM).put((1 + blockType.uv[1]) * TEXTURE_DIM);
//		buffer.put(x+offset).put(y+offset).put(z+offset).put((1 + blockType.uv[0]) * TEXTURE_DIM).put((0 + blockType.uv[1]) * TEXTURE_DIM);
		buffer.put(x-offset).put(y+offset).put(z+offset).put((0 + blockType.uv[0]) * TEXTURE_DIM).put((0 + blockType.uv[1]) * TEXTURE_DIM);

		// Back Face.
		buffer.put(x+offset).put(y-offset).put(z-offset).put((0 + blockType.uv[0]) * TEXTURE_DIM).put((1 + blockType.uv[1]) * TEXTURE_DIM);
		buffer.put(x-offset).put(y-offset).put(z-offset).put((1 + blockType.uv[0]) * TEXTURE_DIM).put((1 + blockType.uv[1]) * TEXTURE_DIM);
		buffer.put(x-offset).put(y+offset).put(z-offset).put((1 + blockType.uv[0]) * TEXTURE_DIM).put((0 + blockType.uv[1]) * TEXTURE_DIM);
//		buffer.put(x+offset).put(y-offset).put(z-offset).put((0 + blockType.uv[0]) * TEXTURE_DIM).put((1 + blockType.uv[1]) * TEXTURE_DIM);
//		buffer.put(x-offset).put(y+offset).put(z-offset).put((1 + blockType.uv[0]) * TEXTURE_DIM).put((0 + blockType.uv[1]) * TEXTURE_DIM);
		buffer.put(x+offset).put(y+offset).put(z-offset).put((0 + blockType.uv[0]) * TEXTURE_DIM).put((0 + blockType.uv[1]) * TEXTURE_DIM);

		// Left Face.
		buffer.put(x-offset).put(y-offset).put(z-offset).put((0 + blockType.uv[0]) * TEXTURE_DIM).put((1 + blockType.uv[1]) * TEXTURE_DIM);
		buffer.put(x-offset).put(y-offset).put(z+offset).put((1 + blockType.uv[0]) * TEXTURE_DIM).put((1 + blockType.uv[1]) * TEXTURE_DIM);
		buffer.put(x-offset).put(y+offset).put(z+offset).put((1 + blockType.uv[0]) * TEXTURE_DIM).put((0 + blockType.uv[1]) * TEXTURE_DIM);
//		buffer.put(x-offset).put(y-offset).put(z-offset).put((0 + blockType.uv[0]) * TEXTURE_DIM).put((1 + blockType.uv[1]) * TEXTURE_DIM);
//		buffer.put(x-offset).put(y+offset).put(z+offset).put((1 + blockType.uv[0]) * TEXTURE_DIM).put((0 + blockType.uv[1]) * TEXTURE_DIM);
		buffer.put(x-offset).put(y+offset).put(z-offset).put((0 + blockType.uv[0]) * TEXTURE_DIM).put((0 + blockType.uv[1]) * TEXTURE_DIM);

		// Right Face.
		buffer.put(x+offset).put(y-offset).put(z+offset).put((0 + blockType.uv[0]) * TEXTURE_DIM).put((1 + blockType.uv[1]) * TEXTURE_DIM);
		buffer.put(x+offset).put(y-offset).put(z-offset).put((1 + blockType.uv[0]) * TEXTURE_DIM).put((1 + blockType.uv[1]) * TEXTURE_DIM);
		buffer.put(x+offset).put(y+offset).put(z-offset).put((1 + blockType.uv[0]) * TEXTURE_DIM).put((0 + blockType.uv[1]) * TEXTURE_DIM);
//		buffer.put(x+offset).put(y-offset).put(z+offset).put((0 + blockType.uv[0]) * TEXTURE_DIM).put((1 + blockType.uv[1]) * TEXTURE_DIM);
//		buffer.put(x+offset).put(y+offset).put(z-offset).put((1 + blockType.uv[0]) * TEXTURE_DIM).put((0 + blockType.uv[1]) * TEXTURE_DIM);
		buffer.put(x+offset).put(y+offset).put(z+offset).put((0 + blockType.uv[0]) * TEXTURE_DIM).put((0 + blockType.uv[1]) * TEXTURE_DIM);

		// Top Face.
		buffer.put(x-offset).put(y+offset).put(z+offset).put((0 + blockType.uv[0]) * TEXTURE_DIM).put((1 + blockType.uv[1]) * TEXTURE_DIM);
		buffer.put(x+offset).put(y+offset).put(z+offset).put((1 + blockType.uv[0]) * TEXTURE_DIM).put((1 + blockType.uv[1]) * TEXTURE_DIM);
		buffer.put(x+offset).put(y+offset).put(z-offset).put((1 + blockType.uv[0]) * TEXTURE_DIM).put((0 + blockType.uv[1]) * TEXTURE_DIM);
//		buffer.put(x-offset).put(y+offset).put(z+offset).put((0 + blockType.uv[0]) * TEXTURE_DIM).put((1 + blockType.uv[1]) * TEXTURE_DIM);
//		buffer.put(x+offset).put(y+offset).put(z+offset).put((1 + blockType.uv[0]) * TEXTURE_DIM).put((0 + blockType.uv[1]) * TEXTURE_DIM);
		buffer.put(x-offset).put(y+offset).put(z-offset).put((0 + blockType.uv[0]) * TEXTURE_DIM).put((0 + blockType.uv[1]) * TEXTURE_DIM);

		// Bottom Face.
		buffer.put(x-offset).put(y-offset).put(z-offset).put((0 + blockType.uv[0]) * TEXTURE_DIM).put((1 + blockType.uv[1]) * TEXTURE_DIM);
		buffer.put(x+offset).put(y-offset).put(z-offset).put((1 + blockType.uv[0]) * TEXTURE_DIM).put((1 + blockType.uv[1]) * TEXTURE_DIM);
		buffer.put(x+offset).put(y-offset).put(z+offset).put((1 + blockType.uv[0]) * TEXTURE_DIM).put((0 + blockType.uv[1]) * TEXTURE_DIM);
//		buffer.put(x-offset).put(y-offset).put(z-offset).put((0 + blockType.uv[0]) * TEXTURE_DIM).put((1 + blockType.uv[1]) * TEXTURE_DIM);
//		buffer.put(x+offset).put(y-offset).put(z-offset).put((1 + blockType.uv[0]) * TEXTURE_DIM).put((0 + blockType.uv[1]) * TEXTURE_DIM);
		buffer.put(x-offset).put(y-offset).put(z+offset).put((0 + blockType.uv[0]) * TEXTURE_DIM).put((0 + blockType.uv[1]) * TEXTURE_DIM);
	}
}
