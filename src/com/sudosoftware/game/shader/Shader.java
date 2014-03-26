package com.sudosoftware.game.shader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.sudosoftware.game.Game;
import com.sudosoftware.game.utils.FileUtil;
import com.sudosoftware.game.utils.MatrixUtil;

public class Shader {

	private int shaderID;
	private int vertexShaderID;
	private int fragmentShaderID;

	public Shader() {
		shaderID = glCreateProgram();
	}

	public void attachVertexShader(String name) {
		// Load the source.
		String vertexShaderSource = FileUtil.readFromFile(name);

		// Create the shader and set the source.
		vertexShaderID = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vertexShaderID, vertexShaderSource);

		// Compile the shader.
		glCompileShader(vertexShaderID);

		// Check for errors.
		if (glGetShaderi(vertexShaderID, GL_COMPILE_STATUS) == GL_FALSE) {
			System.err.println("Unable to create vertex shader: ");
			System.err.println(glGetShaderInfoLog(vertexShaderID, glGetShaderi(vertexShaderID, GL_INFO_LOG_LENGTH)));
			dispose();
			Game.end();
		}

		// Attach the shader.
		glAttachShader(shaderID, vertexShaderID);
	}

	public void attachFragmentShader(String name) {
		// Load the source.
		String fragmentShaderSource = FileUtil.readFromFile(name);

		// Create the shader and set the source.
		fragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fragmentShaderID, fragmentShaderSource);

		// Compile the shader.
		glCompileShader(fragmentShaderID);

		// Check for errors.
		if (glGetShaderi(fragmentShaderID, GL_COMPILE_STATUS) == GL_FALSE) {
			System.err.println("Unable to create fragment shader: ");
			System.err.println(glGetShaderInfoLog(fragmentShaderID, glGetShaderi(fragmentShaderID, GL_INFO_LOG_LENGTH)));
			dispose();
			Game.end();
		}

		// Attach the shader.
		glAttachShader(shaderID, fragmentShaderID);
	}

	public void link() {
		// Link this shader.
		glLinkProgram(shaderID);

		// Check for linking errors.
		if (glGetProgrami(shaderID, GL_LINK_STATUS) == GL_FALSE) {
			System.err.println("Unable to create link shader program.");
			dispose();
			Game.end();
		}
	}

	public void bind() {
		glUseProgram(shaderID);
	}

	public static void unbind() {
		glUseProgram(0);
	}

	public void dispose() {
		// Unbind the shader.
		unbind();

		// Detach the shaders.
		glDetachShader(shaderID, vertexShaderID);
		glDetachShader(shaderID, fragmentShaderID);

		// Delete the shaders.
		glDeleteShader(vertexShaderID);
		glDeleteShader(fragmentShaderID);

		// Delete the shader program.
		glDeleteProgram(shaderID);
	}

	public int getID() {
		return shaderID;
	}

	public void setUniform(String name, Matrix4f value) {
		glUniformMatrix4(glGetUniformLocation(shaderID, name), false, MatrixUtil.toFloatBuffer(value));
	}

	public void setUniform(String name, Vector3f value) {
		glUniform3f(glGetUniformLocation(shaderID, name), value.x, value.y, value.z);
	}

	public void setUniform(String name, float value) {
		glUniform1f(glGetUniformLocation(shaderID, name), value);
	}
}
