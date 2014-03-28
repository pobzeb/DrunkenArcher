package com.sudosoftware.game.shader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.sudosoftware.game.Game;
import com.sudosoftware.game.utils.MatrixUtil;

public class ShaderProgram {
	private int programID;
	private int vertexShaderID;
	private int fragmentShaderID;

	public ShaderProgram(String vertShader, String fragShader) {
		// Create the program, shader and Fragment ids.
		this.programID = glCreateProgram();
		this.vertexShaderID = glCreateShader(GL_VERTEX_SHADER);
		this.fragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);

		// Build the shader.
		glShaderSource(vertexShaderID, vertShader);
		glCompileShader(vertexShaderID);
		if (glGetShaderi(vertexShaderID, GL_COMPILE_STATUS) == GL_FALSE) {
			System.err.println("Unable to create vertex shader: ");
			System.err.println(glGetShaderInfoLog(vertexShaderID, glGetShaderi(vertexShaderID, GL_INFO_LOG_LENGTH)));
			dispose();
			Game.end();
		}

		// Build the fragment.
		glShaderSource(fragmentShaderID, fragShader);
		glCompileShader(fragmentShaderID);
		if (glGetShaderi(fragmentShaderID, GL_COMPILE_STATUS) == GL_FALSE) {
			System.err.println("Unable to create fragment shader: ");
			System.err.println(glGetShaderInfoLog(fragmentShaderID, glGetShaderi(fragmentShaderID, GL_INFO_LOG_LENGTH)));
			dispose();
			Game.end();
		}

		// Attach the shader and fragment to the program.
		glAttachShader(programID, vertexShaderID);
		glAttachShader(programID, fragmentShaderID);

		// Link the program and validate it.
		glLinkProgram(programID);
		glValidateProgram(programID);

		// Check for linking errors.
		if (glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE) {
			System.err.println("Unable to create link shader program.");
			dispose();
			Game.end();
		}
	}

	public void setUniform(String name, Matrix4f value) {
		glUniformMatrix4(glGetUniformLocation(programID, name), false, MatrixUtil.toFloatBuffer(value));
	}

	public void setUniform(String name, Vector3f value) {
		glUniform3f(glGetUniformLocation(programID, name), value.x, value.y, value.z);
	}

	public void setUniform(String name, float value) {
		glUniform1f(glGetUniformLocation(programID, name), value);
	}

	public void use() {
		glUseProgram(programID);
	}

	public void release() {
		glUseProgram(0);
	}

	public void dispose() {
		// Release the shader.
		release();

		// Detach the shader.
		glDetachShader(programID, vertexShaderID);
		glDetachShader(programID, fragmentShaderID);

		// Delete the shader.
		glDeleteShader(vertexShaderID);
		glDeleteShader(fragmentShaderID);

		// Delete the shader program.
		glDeleteProgram(programID);
	}
}
