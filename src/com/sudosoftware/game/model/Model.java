package com.sudosoftware.game.model;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;

public class Model {
	private List<Vector3f> vertices = new ArrayList<Vector3f>();
	private List<Vector2f> texCoords = new ArrayList<Vector2f>();
	private List<Vector3f> normals = new ArrayList<Vector3f>();
	private List<Face> faces = new ArrayList<Face>();
	private HashMap<String, Material> materials = new HashMap<String, Material>();
	private boolean enableSmoothShading = true;
	private int vboVertexHandle, vboNormalHandle, vboTexCoordHandle;

	public void enableStates() {
		if (hasTexCoords()) {
			glEnable(GL_TEXTURE_2D);
		}
		if (isSmoothShadingEnabled()) {
			glShadeModel(GL_SMOOTH);
		}
		else {
			glShadeModel(GL_FLAT);
		}
	}

	public boolean hasTexCoords() {
		return getTexCoords().size() > 0;
	}

	public boolean hasNormals() {
		return getNormals().size() > 0;
	}

	public List<Vector3f> getVertices() {
		return vertices;
	}

	public List<Vector2f> getTexCoords() {
		return texCoords;
	}

	public List<Vector3f> getNormals() {
		return normals;
	}

	public List<Face> getFaces() {
		return faces;
	}

	public boolean isSmoothShadingEnabled() {
		return enableSmoothShading;
	}

	public void setSmoothShadingEnabled(boolean smoothShadingEnabled) {
		this.enableSmoothShading = smoothShadingEnabled;
	}

	public int getVBOVertexHandle() {
		return vboVertexHandle;
	}

	public void setVBOVertexHandle(int vboVertexHandle) {
		this.vboVertexHandle = vboVertexHandle;
	}

	public int getVBONormalHandle() {
		return vboNormalHandle;
	}

	public void setVBONormalHandle(int vboNormalHandle) {
		this.vboNormalHandle = vboNormalHandle;
	}

	public int getVBOTexCoordHandle() {
		return vboTexCoordHandle;
	}

	public void setVBOTexCoordHandle(int vboTexCoordHandle) {
		this.vboTexCoordHandle = vboTexCoordHandle;
	}

	public HashMap<String, Material> getMaterials() {
		return materials;
	}

	public static class Material {
		@Override
		public String toString() {
			return "Material {" +
					"specularCoefficient=" + specularCoefficient +
					", ambientColor=" + ambientColor + 
					", diffuseColor=" + diffuseColor + 
					", specularColor=" + specularColor +
					"}";
		}

		/** Between 0 and 1000. */
		public float specularCoefficient = 100;
		public float[] ambientColor = {0.2f, 0.2f, 0.2f};
		public float[] diffuseColor = {0.3f, 1, 1};
		public float[] specularColor = {1, 1, 1};
		public Texture texture;
	}

	public static class Face {
		private final int[] vertexIndices = {-1, -1, -1};
		private final int[] normalIndices = {-1, -1, -1};
		private final int[] texCoordIndices = {-1, -1, -1};
		private Material material;

		public Material getMaterial() {
			return material;
		}

		public boolean hasNormals() {
			return normalIndices[0] != -1;
		}

		public boolean hasTexCoords() {
			return texCoordIndices[0] != -1;
		}

		public int[] getVertexIndices() {
			return vertexIndices;
		}

		public int[] getTexCoordIndices() {
			return texCoordIndices;
		}

		public int[] getNormalIndices() {
			return normalIndices;
		}

		public Face(int[] vertexIndices) {
			this.vertexIndices[0] = vertexIndices[0];
			this.vertexIndices[1] = vertexIndices[1];
			this.vertexIndices[2] = vertexIndices[2];
		}

		public Face(int[] vertexIndices, int[] normalIndices) {
			this.vertexIndices[0] = vertexIndices[0];
			this.vertexIndices[1] = vertexIndices[1];
			this.vertexIndices[2] = vertexIndices[2];
			this.normalIndices[0] = normalIndices[0];
			this.normalIndices[1] = normalIndices[1];
			this.normalIndices[2] = normalIndices[2];
		}

		public Face(int[] vertexIndices, int[] normalIndices, int[] texCoordIndices, Material material) {
			this.vertexIndices[0] = vertexIndices[0];
			this.vertexIndices[1] = vertexIndices[1];
			this.vertexIndices[2] = vertexIndices[2];
			this.texCoordIndices[0] = texCoordIndices[0];
			this.texCoordIndices[1] = texCoordIndices[1];
			this.texCoordIndices[2] = texCoordIndices[2];
			this.normalIndices[0] = normalIndices[0];
			this.normalIndices[1] = normalIndices[1];
			this.normalIndices[2] = normalIndices[2];
			this.material = material;
		}
	}

	public void draw() {
		// Bind the model buffers.
		glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
		glVertexPointer(3, GL_FLOAT, 0, 0L);
		glBindBuffer(GL_ARRAY_BUFFER, vboNormalHandle);
		glNormalPointer(GL_FLOAT, 0, 0L);
		glBindBuffer(GL_ARRAY_BUFFER, vboTexCoordHandle);
		glTexCoordPointer(2, GL_FLOAT, 0, 0L);

        // Render the model.
		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_NORMAL_ARRAY);
		glDrawArrays(GL_TRIANGLES, 0, getFaces().size() * 3);
		glDisableClientState(GL_VERTEX_ARRAY);
		glDisableClientState(GL_NORMAL_ARRAY);
	}

	public void dispose() {
		glDeleteBuffers(vboVertexHandle);
		glDeleteBuffers(vboNormalHandle);
		glDeleteBuffers(vboTexCoordHandle);
	}
}
