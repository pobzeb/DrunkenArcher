package com.sudosoftware.drunkenarcher.world;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class Chunk {

	// Define the chunk metrics.
	public static final int CHUNK_SIZE = 24;
	public static final int CHUNK_HEIGHT = 16;

	// Define constant values.
	public static final int FACE_DIVISIONS = 1;
	public static final int FACE_COUNT = 6;
	public static final int FLOAT_SIZE = 4;

	// Chunks coordinates in the world.
	public float x, y, z;

	// Define the blocks in this chunk.
	public byte[][][] blocks;

	// Block rendering ID's.
	public int vcHandle = -1;

	// Counts for vertices and textures.
	private int verticesPerCube = (Block.VERTEX_SIZE * FACE_DIVISIONS) * FACE_COUNT;
	private int textureCoordsPerCube = (Block.TEXTURE_COORD_SIZE * FACE_DIVISIONS) * FACE_COUNT;

	// Get data locations for rendering.
	private int stride = (Block.VERTEX_SIZE + Block.TEXTURE_COORD_SIZE) * FLOAT_SIZE;
	private int textureCoordOffset = (Block.VERTEX_SIZE) * FLOAT_SIZE;

	public Chunk(float x, float y, float z) {
		// Set this chunk's coordinates.
		this.x = x;
		this.y = y;
		this.z = z;

		// Create the new blocks for this chunk.
		this.blocks = new byte[CHUNK_SIZE][CHUNK_HEIGHT][CHUNK_SIZE];
	}

	public void buildVBOs() {
		// Check to see if we already built this.
		if (this.vcHandle < 0) {
			// Build the buffers (vertex count * FloatSize).
			FloatBuffer vcBuffer = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_HEIGHT * CHUNK_SIZE) * ((verticesPerCube + textureCoordsPerCube) * FLOAT_SIZE));

			// Determine which blocks can be drawn.
			int surroundingBlockCount = 0;
			int renderableBlockCount = 0;
			for (int blockX = 0; blockX < CHUNK_SIZE; blockX++) {
				for (int blockZ = 0; blockZ < CHUNK_SIZE; blockZ++) {
					for (int blockY = CHUNK_HEIGHT - 1; blockY >= 0; blockY--) {
						try {
							// Skip the rest if this is air.
							if (this.blocks[blockX][blockY][blockZ] == BlockType.AIR.id) continue;

							// Determine how many blocks are in front of faces for this block.
							surroundingBlockCount = 0;
							if (blockX > 0)
								surroundingBlockCount += (this.blocks[blockX - 1][blockY][blockZ] != BlockType.AIR.id ? 1 : 0);
							if (blockX < CHUNK_SIZE - 1)
								surroundingBlockCount += (this.blocks[blockX + 1][blockY][blockZ] != BlockType.AIR.id ? 1 : 0);
							if (blockZ > 0)
								surroundingBlockCount += (this.blocks[blockX][blockY][blockZ - 1] != BlockType.AIR.id ? 1 : 0);
							if (blockZ < CHUNK_SIZE - 1)
								surroundingBlockCount += (this.blocks[blockX][blockY][blockZ + 1] != BlockType.AIR.id ? 1 : 0);

							if (blockY > 0 && blockY < CHUNK_HEIGHT - 1) {
								surroundingBlockCount += (this.blocks[blockX][blockY + 1][blockZ] != BlockType.AIR.id ? 1 : 0);
								surroundingBlockCount += (this.blocks[blockX][blockY - 1][blockZ] != BlockType.AIR.id ? 1 : 0);
							}

							if (blockX == 0 || blockX == CHUNK_SIZE - 1 || blockZ == 0 || blockZ == CHUNK_SIZE - 1) {
								surroundingBlockCount++;
								// Check for neighboring chunks.
								Chunk chunkWest = World.getInstance().getChunk((this.x - 1)+","+this.y+","+this.z);
								if (chunkWest != null) {
									surroundingBlockCount += (chunkWest.blocks[0][blockY][blockZ] != BlockType.AIR.id ? 1 : 0);
								}
								Chunk chunkEast = World.getInstance().getChunk((this.x + 1)+","+this.y+","+this.z);
								if (chunkEast != null) {
									surroundingBlockCount += (chunkEast.blocks[blockX + CHUNK_SIZE - 1][blockY][blockZ] != BlockType.AIR.id ? 1 : 0);
								}
								Chunk chunkNorth = World.getInstance().getChunk(this.x+","+this.y+","+(this.z - 1));
								if (chunkNorth != null) {
									surroundingBlockCount += (chunkNorth.blocks[blockX][blockY][0] != BlockType.AIR.id ? 1 : 0);
								}
								Chunk chunkSouth = World.getInstance().getChunk(this.x+","+this.y+","+(this.z + 1));
								if (chunkSouth != null) {
									surroundingBlockCount += (chunkSouth.blocks[blockX][blockY][blockZ + CHUNK_SIZE - 1] != BlockType.AIR.id ? 1 : 0);
								}
							}

							// If there are six blocks around this one, it's not visible.
							if (surroundingBlockCount >= 6) {
								continue;
							}

							// Allow this block to be rendered.
							Block.addToVBO(this.blocks[blockX][blockY][blockZ], (this.x * CHUNK_SIZE) + blockX * Block.BLOCK_SIZE, (this.y * CHUNK_HEIGHT) + blockY * Block.BLOCK_SIZE, (this.z * CHUNK_SIZE) + blockZ * Block.BLOCK_SIZE, vcBuffer);

							// Increment the count for renderable blocks in this chunk.
							renderableBlockCount++;
							break;
						}
						catch (Exception e) {
							System.err.println("Error getting block: ["+blockX+","+blockY+","+blockZ+"]");
							e.printStackTrace();
						}
					}
				}
			}

			if (renderableBlockCount == 0) return;

			// Flip the buffers so they are ready to go.
			vcBuffer.flip();
	
			// Get the buffer handle.
			this.vcHandle = glGenBuffers();

			// Load the buffers into OpenGL
			glBindBuffer(GL_ARRAY_BUFFER, this.vcHandle);
			glBufferData(GL_ARRAY_BUFFER, vcBuffer, GL_STATIC_DRAW);
		}
	}

	public void render() {
		if (this.vcHandle >= 0) {
			try {
				// Bind the texture for this block.
				glEnable(GL_TEXTURE_2D);
				World.getInstance().texture.bind();

				// Bind the buffers.
				glBindBuffer(GL_ARRAY_BUFFER, this.vcHandle);
				glVertexPointer(Block.VERTEX_SIZE, GL_FLOAT, stride, 0L);
				glTexCoordPointer(Block.TEXTURE_COORD_SIZE, GL_FLOAT, stride, textureCoordOffset);

				// Use the shader.
				World.getInstance().shader.use();

				// Render the chunk.
				glDrawArrays(GL_QUADS, 0, (CHUNK_SIZE * CHUNK_HEIGHT * CHUNK_SIZE) * verticesPerCube);

				// Release the shader.
				World.getInstance().shader.release();

				// Remove the buffer binding.
				glBindBuffer(GL_ARRAY_BUFFER, 0);
			}
			catch (Exception e) {
				// Exceptions will be thrown for blocks without VBOs.
				e.printStackTrace();
			}
		}
	}

	public byte getBlockAt(int x, int y, int z) {
		try {
			return this.blocks[x][y][z];
		}
		catch (Exception e) {}

		return 0;
	}

	public void dispose() {
		glDeleteBuffers(this.vcHandle);
		this.vcHandle = -1;
		this.blocks = null;
	}
}
