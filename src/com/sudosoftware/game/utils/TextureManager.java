package com.sudosoftware.game.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/**
 * Texture Manager to load textures
 * 
 * @author Tim Biedert
 */
public class TextureManager {

	// Num textures loaded
	private static int numTotalLoaded = 0;
	private static int numLoaded = 0;

	// Textures loaded
	private static HashMap<String, Number> loadedTextures = new HashMap<String, Number>();

	/***************************************************************************************************************************************************************************************************
	 * loadTexture() - Load a texture in OpenGL
	 **************************************************************************************************************************************************************************************************/
	public static int loadTexture(String name, boolean flip) {
		// Is Texture already loaded?
		if (loadedTextures.get(name) != null) {
			return loadedTextures.get(name).intValue();
		}
		else {
			BufferedImage image = null;
			try {
				image = ImageIO.read(new FileInputStream(new File("res/textures/" + name + ".png")));
			} catch (Exception e) {
				System.out.println("[TextureManager] Failed to load texture '" + name + "'!");
				e.printStackTrace();
				return -1;
			}

			// Load the image into a pixel array.
			int[] pixels = new int[image.getWidth() * image.getHeight()];
			image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

			// Load the image into a byte buffer.
			ByteBuffer imageData = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);
			for (int y = 0; y < image.getHeight(); y++) {
				for (int x = 0; x < image.getWidth(); x++) {
					int pixel = pixels[y * image.getWidth() + x];
					imageData.put((byte) ((pixel >> 16) & 0xFF));
					imageData.put((byte) ((pixel >> 8) & 0xFF));
					imageData.put((byte) ((pixel) & 0xFF));
					imageData.put((byte) ((pixel >> 24) & 0xFF));
				}
			}
			imageData.flip();

			// Get a new texture id.
			int texID = GL11.glGenTextures();

			// Bind this texture
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texID);

			// No Texture Filtering
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imageData);
			GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 4);

			// Reset OpenGL texture state
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

			// Add texture to Hashmap
			loadedTextures.put(name, texID);

			// Increment counter
			numTotalLoaded++;
			numLoaded++;

			return texID;
		}
	}

	/***************************************************************************************************************************************************************************************************
	 * getNumTotalLoadedTextures() - Returns the number of successfully loaded
	 * textures
	 * 
	 * @return The total number of textures the TextureManager ever loaded.
	 **************************************************************************************************************************************************************************************************/
	public static int getNumTotalLoadedTextures() {
		return numTotalLoaded;
	}

	/***************************************************************************************************************************************************************************************************
	 * getNumLoadedTextures() - Returns the number of successfully loaded
	 * textures since last counter reset
	 * 
	 * @return The number of loaded textures since last counter reset.
	 **************************************************************************************************************************************************************************************************/
	public static int getNumLoadedTextures() {
		return numLoaded;
	}

	/***************************************************************************************************************************************************************************************************
	 * resetLoadCounter() - Sets the counter of loaded textures to zero.
	 **************************************************************************************************************************************************************************************************/
	public static void resetLoadCounter() {
		numLoaded = 0;
	}
}
