package com.sudosoftware.drunkenarcher.util;

import java.util.Random;

import com.sudosoftware.drunkenarcher.world.BlockType;
import com.sudosoftware.drunkenarcher.world.Chunk;
import com.sudosoftware.drunkenarcher.world.World;

public class ChunkManager {

	// Used for randomizing the landscapes.
	private static final Random random = new Random();

	public static Chunk generateAirChunk(float chunkX, float chunkY, float chunkZ) {
		Chunk chunk = new Chunk(chunkX, chunkY, chunkZ);
		for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
			for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
				for (int y = 0; y < Chunk.CHUNK_HEIGHT; y++) {
					chunk.blocks[x][y][z] = (byte)(BlockType.AIR.id);
				}
			}
		}

		return chunk;
	}

	public static Chunk generateFlatChunk(float chunkX, float chunkY, float chunkZ) {
		Chunk chunk = generateAirChunk(chunkX, chunkY, chunkZ);
		for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
			for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
				for (int y = 0; y <= 0; y++) {
					chunk.blocks[x][y][z] = (byte)(BlockType.GRASS.id);
				}
			}
		}

		return chunk;
	}

	public static Chunk generateRandomChunk(float chunkX, float chunkY, float chunkZ, double[][] noiseMap) {
		Chunk chunk = generateAirChunk(chunkX, chunkY, chunkZ);
		for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
			for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
				// Get the height here.
				double height = (Chunk.CHUNK_HEIGHT * World.WORLD_HEIGHT) * noiseMap[(int) (chunkX * Chunk.CHUNK_SIZE + x)][(int) (chunkZ * Chunk.CHUNK_SIZE + z)];

				// Place blocks for this height.
				int y = 0;
				while (y + (chunkY * Chunk.CHUNK_HEIGHT) < height) {
					// Don't go out of this chunk.
					if (y >= Chunk.CHUNK_HEIGHT) break;

					// Pick a random block type (other than air).
					chunk.blocks[x][y][z] = (byte)(random.nextInt(BlockType.values().length - 1) + 1);

					// Up we go.
					y++;
				}
			}
		}

		return chunk;
	}

	public static Chunk generateSolidChunk(float chunkX, float chunkY, float chunkZ) {
		Chunk chunk = generateAirChunk(chunkX, chunkY, chunkZ);
		for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
			for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
				for (int y = 0; y < Chunk.CHUNK_HEIGHT; y++) {
					chunk.blocks[x][y][z] = (byte)(random.nextInt(BlockType.values().length - 1) + 1);
				}
			}
		}

		return chunk;
	}
}
