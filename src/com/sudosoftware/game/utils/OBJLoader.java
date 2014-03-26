package com.sudosoftware.game.utils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.TextureLoader;

import com.sudosoftware.game.model.Model;

public class OBJLoader {
	public static int createDisplayList(Model model) {
		int displayList = glGenLists(1);
		glNewList(displayList, GL_COMPILE);
			glMaterialf(GL_FRONT, GL_SHININESS, 120);
			glColor3f(0.4f,  0.27f, 0.17f);
			for (Model.Face face : model.getFaces()) {
				glBegin(GL_POLYGON);
				for (int i = 0; i < face.getVertexIndices().length; i++) {
					if (face.hasNormals()) {
						Vector3f n = model.getNormals().get(face.getNormalIndices()[i] - 1);
						glNormal3f(n.x, n.y, n.z);
					}
					Vector3f v = model.getVertices().get(face.getVertexIndices()[i] - 1);
					glVertex3f(v.x, v.y, v.z);
				}
				glEnd();
			}
		glEndList();
		return displayList;
	}

	private static FloatBuffer reserveData(int size) {
		return BufferUtils.createFloatBuffer(size);
	}

	private static float[] asFloats(Vector3f v) {
		return new float[]{v.x, v.y, v.z};
	}

	private static float[] asFloats(Vector2f v) {
		return new float[]{v.x, v.y};
	}

	public static void createVBO(Model model) {
		// Build the buffers.
		FloatBuffer vertices = reserveData(model.getFaces().size() * 9);
		FloatBuffer normals = reserveData(model.getFaces().size() * 9);
		FloatBuffer texCoords = reserveData(model.getFaces().size() * 6);
		for (Model.Face face : model.getFaces()) {
			vertices.put(asFloats(model.getVertices().get(face.getVertexIndices()[0] - 1)));
			vertices.put(asFloats(model.getVertices().get(face.getVertexIndices()[1] - 1)));
			vertices.put(asFloats(model.getVertices().get(face.getVertexIndices()[2] - 1)));
			if (model.hasNormals() && face.hasNormals()) {
				normals.put(asFloats(model.getNormals().get(face.getNormalIndices()[0] - 1)));
				normals.put(asFloats(model.getNormals().get(face.getNormalIndices()[1] - 1)));
				normals.put(asFloats(model.getNormals().get(face.getNormalIndices()[2] - 1)));
			}
			if (model.hasTexCoords() && face.hasTexCoords()) {
				texCoords.put(asFloats(model.getTexCoords().get(face.getTexCoordIndices()[0] - 1)));
				texCoords.put(asFloats(model.getTexCoords().get(face.getTexCoordIndices()[1] - 1)));
			}
		}
		vertices.flip();
		normals.flip();
		texCoords.flip();

		// Load the buffers into OpenGL
		int vboVertexID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		int vboNormalID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboNormalID);
		glBufferData(GL_ARRAY_BUFFER, normals, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		int vboTexCoordID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboTexCoordID);
		glBufferData(GL_ARRAY_BUFFER, texCoords, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		// Set the buffer handles into the model.
		model.setVBOVertexHandle(vboVertexID);
		model.setVBONormalHandle(vboNormalID);
		model.setVBOTexCoordHandle(vboTexCoordID);
	}

	public static Vector3f parseVertex(String line) {
		String[] xyz = line.split(" ");
		float x = Float.valueOf(xyz[0]);
		float y = Float.valueOf(xyz[1]);
		float z = Float.valueOf(xyz[2]);
		return new Vector3f(x, y, z);
	}

	public static Vector3f parseNormal(String line) {
		String[] xyz = line.split(" ");
		float x = Float.valueOf(xyz[0]);
		float y = Float.valueOf(xyz[1]);
		float z = Float.valueOf(xyz[2]);
		return new Vector3f(x, y, z);
	}

	public static Model.Face parseFace(boolean hasNormals, String line) {
		String[] faceIndices = line.split(" ");
		int[] vertexIndicesArray = new int[faceIndices.length];
		for (int i = 0; i < faceIndices.length; i++) {
			vertexIndicesArray[i] = Integer.parseInt(faceIndices[i].split("/")[0]);
		}

		if (hasNormals) {
			int[] normalIndicesArray = new int[faceIndices.length];
			for (int i = 0; i < faceIndices.length; i++) {
				normalIndicesArray[i] = Integer.parseInt(faceIndices[i].split("/")[2]);
			}
			return new Model.Face(vertexIndicesArray, normalIndicesArray);
		}
		else {
			return new Model.Face(vertexIndicesArray);
		}
	}

	public static Model loadModel(File file) throws IOException {
		System.out.println("Loading Model: " + file.getParentFile().getAbsolutePath() + "/" + file.getName());
		BufferedReader reader = new BufferedReader(new FileReader(file));
		Model model = new Model();
		String line;
		while ((line = reader.readLine()) != null) {
			String prefix = line.split(" ")[0].trim();
			if (line.startsWith("#") || line.trim().length() == 0) {
				continue;
			}
			else if (prefix.equals("v")) {
				model.getVertices().add(parseVertex(line.replaceAll("v ",  "").trim()));
			}
			else if (prefix.equals("vn")) {
				model.getNormals().add(parseNormal(line.replaceAll("vn ",  "").trim()));
			}
			else if (prefix.equals("f")) {
				model.getFaces().add(parseFace(model.hasNormals(), line.replaceAll("f ",  "").trim()));
			}
			else {
				reader.close();
				throw new RuntimeException("OBJ file contains a line which cannot be parsed: " + line);
			}
		}

		reader.close();
		return model;
	}

	public static int createTexturedDisplayList(Model model) {
		int displayList = glGenLists(1);
		glNewList(displayList, GL_COMPILE);
			glBegin(GL_TRIANGLES);
				for (Model.Face face : model.getFaces()) {
					if (face.hasTexCoords()) {
						glMaterial(GL_FRONT, GL_DIFFUSE, BufferTools.asFlippedFloatBuffer(face.getMaterial().diffuseColor[0], face.getMaterial().diffuseColor[1], face.getMaterial().diffuseColor[2], 1));
						glMaterial(GL_FRONT, GL_AMBIENT, BufferTools.asFlippedFloatBuffer(face.getMaterial().ambientColor[0], face.getMaterial().ambientColor[1], face.getMaterial().ambientColor[2], 1));
						glMaterialf(GL_FRONT, GL_SHININESS, face.getMaterial().specularCoefficient);
					}
					if (face.hasNormals()) {
						Vector3f n1 = model.getNormals().get(face.getNormalIndices()[0] - 1);
						glNormal3f(n1.x, n1.y, n1.z);
					}
					if (face.hasTexCoords()) {
						Vector2f t1 = model.getTexCoords().get(face.getTexCoordIndices()[0] - 1);
						glTexCoord2f(t1.x, t1.y);
					}
					Vector3f v1 = model.getVertices().get(face.getVertexIndices()[0] - 1);
					glVertex3f(v1.x, v1.y, v1.z);
					if (face.hasNormals()) {
						Vector3f n2 = model.getNormals().get(face.getNormalIndices()[1] - 1);
						glNormal3f(n2.x, n2.y, n2.z);
					}
					if (face.hasTexCoords()) {
						Vector2f t2 = model.getTexCoords().get(face.getTexCoordIndices()[1] - 1);
						glTexCoord2f(t2.x, t2.y);
					}
					Vector3f v2 = model.getVertices().get(face.getVertexIndices()[1] - 1);
					glVertex3f(v2.x, v2.y, v2.z);
					if (face.hasNormals()) {
						Vector3f n3 = model.getNormals().get(face.getNormalIndices()[2] - 1);
						glNormal3f(n3.x, n3.y, n3.z);
					}
					if (face.hasTexCoords()) {
						Vector2f t3 = model.getTexCoords().get(face.getTexCoordIndices()[2] - 1);
						glTexCoord2f(t3.x, t3.y);
					}
					Vector3f v3 = model.getVertices().get(face.getVertexIndices()[2] - 1);
					glVertex3f(v3.x, v3.y, v3.z);
				}
			glEnd();
		glEndList();
		return displayList;
	}

	public static Model loadTexturedModel(File file) throws IOException {
		System.out.println("Loading Textured Model: " + file.getParentFile().getAbsolutePath() + "/" + file.getName());
		BufferedReader reader = new BufferedReader(new FileReader(file));
		Model model = new Model();
		Model.Material currentMaterial = new Model.Material();
		String line;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("#") || line.trim().length() == 0) {
				continue;
			}
			else if (line.startsWith("mtllib ")) {
				String materialFileName = line.replaceAll("mtllib ", "").trim();
				System.out.println("Loading Material: " + file.getParentFile().getAbsolutePath() + "/" + materialFileName);
				try {
					File materialFile = new File(file.getParentFile().getAbsolutePath() + "/" + materialFileName);
					BufferedReader materialReader = new BufferedReader(new FileReader(materialFile));
					String materialLine;
					Model.Material parseMaterial = new Model.Material();
					String parseMaterialName = "";
					while ((materialLine = materialReader.readLine()) != null) {
						if (materialLine.startsWith("#")) {
							continue;
						}
						else if (materialLine.startsWith("newmtl ")) {
							if (!parseMaterialName.equals("")) {
								model.getMaterials().put(parseMaterialName, parseMaterial);
							}
							parseMaterialName = materialLine.replaceAll("newmtl ",  "").trim();
							parseMaterial = new Model.Material();
						}
						else if (materialLine.startsWith("Ns ")) {
							parseMaterial.specularCoefficient = Float.valueOf(materialLine.replaceAll("Ns ",  "").trim());
						}
						else if (materialLine.startsWith("Ka ")) {
							String[] rgb = materialLine.replaceAll("Ka ",  "").trim().split(" ");
							parseMaterial.ambientColor[0] = Float.valueOf(rgb[0]);
							parseMaterial.ambientColor[1] = Float.valueOf(rgb[1]);
							parseMaterial.ambientColor[2] = Float.valueOf(rgb[2]);
						}
						else if (materialLine.startsWith("Ks ")) {
							String[] rgb = materialLine.replaceAll("Ks ",  "").trim().split(" ");
							parseMaterial.specularColor[0] = Float.valueOf(rgb[0]);
							parseMaterial.specularColor[1] = Float.valueOf(rgb[1]);
							parseMaterial.specularColor[2] = Float.valueOf(rgb[2]);
						}
						else if (materialLine.startsWith("Kd ")) {
							String[] rgb = materialLine.replaceAll("Kd ",  "").trim().split(" ");
							parseMaterial.diffuseColor[0] = Float.valueOf(rgb[0]);
							parseMaterial.diffuseColor[1] = Float.valueOf(rgb[1]);
							parseMaterial.diffuseColor[2] = Float.valueOf(rgb[2]);
						}
						else if (materialLine.startsWith("map_Kd")) {
							parseMaterial.texture = null;
							String texFileName = materialLine.replaceAll("map_Kd ",  "").trim();
							System.out.println("Loading Texture: " + file.getParentFile().getAbsolutePath() + "/" + texFileName);
							try {
								parseMaterial.texture = TextureLoader.getTexture(texFileName.substring(texFileName.lastIndexOf(".")), new FileInputStream(new File(file.getParentFile().getAbsolutePath() + "/" + texFileName)));
							}
							catch (FileNotFoundException e) {
								e.printStackTrace();
							}
							catch (IOException e) {
								e.printStackTrace();
							}
							catch (Exception e) {
								e.printStackTrace();
							}
						}
						else {
							System.err.println("[MTL] Unknown Line: " + materialLine);
						}
					}
	
					model.getMaterials().put(parseMaterialName, parseMaterial);
					materialReader.close();
				}
				catch (Exception e) {
					System.err.println("Error loading material file: " + file.getName());
					e.printStackTrace();
				}
			}
			else if (line.startsWith("usemtl ")) {
				currentMaterial = model.getMaterials().get(line.replaceAll("usemtl ",  "").trim());
			}
			else if (line.startsWith("v ")) {
				String[] xyz = line.replaceAll("v ",  "").trim().split(" ");
				float x = Float.valueOf(xyz[0]);
				float y = Float.valueOf(xyz[1]);
				float z = Float.valueOf(xyz[2]);
				model.getVertices().add(new Vector3f(x, y, z));
			}
			else if (line.startsWith("vn ")) {
				String[] xyz = line.replaceAll("vn ",  "").trim().split(" ");
				float x = Float.valueOf(xyz[0]);
				float y = Float.valueOf(xyz[1]);
				float z = Float.valueOf(xyz[2]);
				model.getNormals().add(new Vector3f(x, y, z));
			}
			else if (line.startsWith("vt ")) {
				String[] st = line.replaceAll("vt ",  "").trim().split(" ");
				float s = Float.valueOf(st[0]);
				float t = Float.valueOf(st[1]);
				model.getTexCoords().add(new Vector2f(s, t));
			}
			else if (line.startsWith("f ")) {
				String[] faceIndices = line.replaceAll("f ",  "").trim().split(" ");
				int[] vertexIndicesArray = {
						Integer.parseInt(faceIndices[0].split("/")[0]),
						Integer.parseInt(faceIndices[1].split("/")[0]),
						Integer.parseInt(faceIndices[2].split("/")[0])};
				int[] texCoordIndicesArray = {-1, -1, -1};
				if (model.hasTexCoords()) {
					try {
						texCoordIndicesArray[0] = Integer.parseInt(faceIndices[0].split("/")[1]);
						texCoordIndicesArray[1] = Integer.parseInt(faceIndices[1].split("/")[1]);
						texCoordIndicesArray[2] = Integer.parseInt(faceIndices[2].split("/")[1]);
					}
					catch (Exception e) {
						System.err.println(e.getMessage());
					}
				}
				int[] normalIndicesArray = {0, 0, 0};
				if (model.hasNormals()) {
					normalIndicesArray[0] = Integer.parseInt(faceIndices[0].split("/")[2]);
					normalIndicesArray[1] = Integer.parseInt(faceIndices[1].split("/")[2]);
					normalIndicesArray[2] = Integer.parseInt(faceIndices[2].split("/")[2]);
				}
				model.getFaces().add(new Model.Face(vertexIndicesArray, normalIndicesArray, texCoordIndicesArray, currentMaterial));
			}
			else if (line.startsWith("s ")) {
				boolean enableSmoothShading = !line.contains("off");
				model.setSmoothShadingEnabled(enableSmoothShading);
			}
			else {
				System.err.println("[OBJ] Unknown Line: " + line);
			}
		}
		reader.close();

		// Build the VBOs.
		createVBO(model);

		// Return the model.
		return model;
	}
}
