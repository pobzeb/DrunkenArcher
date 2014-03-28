package com.sudosoftware.drunkenarcher.world;

import static org.lwjgl.opengl.GL11.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Random;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import com.sudosoftware.drunkenarcher.entity.Player;
import com.sudosoftware.drunkenarcher.util.ChunkManager;
import com.sudosoftware.drunkenarcher.util.ImageWriter;
import com.sudosoftware.drunkenarcher.util.SimplexNoise;

public class World {
	// World size in chunks.
	public static final int WORLD_SIZE = 10;
	public static final int WORLD_HEIGHT = 1;

	// Used for random things...
	public static final Random random = new Random();

	// Define the chunks that make up this world.
	private HashMap<String, Chunk> chunks = new HashMap<String, Chunk>();

	// Singleton instance.
	private static World instance;
	private static Object lock = new Object();

	// The main world texture.
	public Texture texture;

	// Define the player.
	public Player player;

	private World() {
		// Create the player and place them on the ground.
		this.player = new Player();
		this.player.setFieldOfView(45.0f);
		this.player.setNearPlane(0.3f);
		this.player.setFarPlane(300.0f);
		this.player.setPosition(-((WORLD_SIZE * Chunk.CHUNK_SIZE) / 2), 0, -((WORLD_SIZE * Chunk.CHUNK_SIZE) / 2));
		this.player.setRotation(0f, 0f, 0f);

		// Load textures.
		try {
			this.texture = TextureLoader.getTexture("PNG", new FileInputStream(new File("res/textures/blocks.png")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static World getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new World();
				}
			}
		}

		return instance;
	}

	public void generate() {
		// First, generate a 2D noise map.
		double[][] noiseMap = new double[WORLD_SIZE * Chunk.CHUNK_SIZE][WORLD_SIZE * Chunk.CHUNK_SIZE];
		for (int x = 0; x < WORLD_SIZE * Chunk.CHUNK_SIZE; x++) {
			for (int z = 0; z < WORLD_SIZE * Chunk.CHUNK_SIZE; z++) {
				noiseMap[x][z] = (1 + SimplexNoise.calculateNoiseHeight(x, z, 0.008, 5, 0.05)) * 0.5f;
//				if (noiseMap[x][z] > 1) noiseMap[x][z] = 1;
			}
		}
		// Save off the image for this map (just for fun reference later).
		ImageWriter.greyWriteImage(noiseMap);

		// Now, use the noise map to render the chunks.
		Chunk chunk;
		for (int chunkX = 0; chunkX < WORLD_SIZE; chunkX++) {
			for (int chunkZ = 0; chunkZ < WORLD_SIZE; chunkZ++) {
				for (int chunkY = 0; chunkY < WORLD_HEIGHT; chunkY++) {
					chunk = ChunkManager.generateRandomChunk(chunkX, chunkY, chunkZ, noiseMap);
					addChunk(String.valueOf(chunkX+","+chunkY+","+chunkZ), chunk);
					chunk.buildVBOs();
				}
			}
		}

		// Set the player position.
		String currentChunk = "";
		chunk = null;
		byte block;
		for (int y = 0; y < WORLD_HEIGHT * Chunk.CHUNK_HEIGHT; y++) {
			// Get the chunk at the players position.
			int chunkX = (int)Math.abs((player.getPosition().x) / Chunk.CHUNK_SIZE);
			int chunkY = (int)Math.abs((y) / Chunk.CHUNK_HEIGHT);
			int chunkZ = (int)Math.abs((player.getPosition().z) / Chunk.CHUNK_SIZE);
			String key = chunkX+","+chunkY+","+chunkZ;
			if (!key.equalsIgnoreCase(currentChunk)) {
				System.out.println("Getting chunk: " + key);
				currentChunk = new String(key);
				chunk = getChunk(key);
			}

			// Check to see if we have hit the ground.
			int blockX = (int)Math.abs((player.getPosition().x) - (chunkX * Block.BLOCK_SIZE * Chunk.CHUNK_SIZE));
			int blockY = (int)Math.abs((y) - (chunkY * Block.BLOCK_SIZE * Chunk.CHUNK_HEIGHT));
			int blockZ = (int)Math.abs((player.getPosition().z) - (chunkZ * Block.BLOCK_SIZE * Chunk.CHUNK_SIZE));
			block = chunk.getBlockAt(blockX, blockY, blockZ);
			System.out.println("Checking Block ["+blockX+","+blockY+","+blockZ+"]: "+block);
			if (block != BlockType.AIR.id) {
				player.setPosition(player.getPosition().x, y + 2, player.getPosition().z);
				break;
			}
		}
	}

	public void addChunk(String key, Chunk chunk) {
		this.chunks.put(key, chunk);
	}

	public Chunk getChunk(String key) {
		return this.chunks.get(key);
	}

	public void update(long delta) {
		
	}

	public void render() {
		// Move and rotate the player.
		this.player.render();

		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_TEXTURE_COORD_ARRAY);

		// Render the chunks.
		for (Chunk chunk : this.chunks.values()) {
			chunk.render();
		}

		glDisableClientState(GL_VERTEX_ARRAY);
		glDisableClientState(GL_TEXTURE_COORD_ARRAY);
	}

	public void dispose() {
		for (Chunk chunk : this.chunks.values()) {
			chunk.dispose();
			chunk = null;
		}

		if (this.texture != null)
			this.texture.release();
		this.chunks = null;
	}
}
