package com.ternsip.glade.model.loader.engine.shaders;

import com.ternsip.glade.utils.Utils;
import org.lwjgl.opengl.GL11;

import java.io.BufferedReader;
import java.io.File;

import static com.ternsip.glade.model.Mesh.*;
import static com.ternsip.glade.model.Mesh.JOINTS_ATTRIBUTE_POINTER_INDEX;
import static com.ternsip.glade.model.Mesh.WEIGHTS_ATTRIBUTE_POINTER_INDEX;
import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {

    private int programID;

    public ShaderProgram(File vertexFile, File fragmentFile) {
        int vertexShaderID = loadShader(vertexFile, GL_VERTEX_SHADER);
        int fragmentShaderID = loadShader(fragmentFile, GL_FRAGMENT_SHADER);
        programID = glCreateProgram();
        glAttachShader(programID, vertexShaderID);
        glAttachShader(programID, fragmentShaderID);
        bindAttributes();
        glLinkProgram(programID);
        glDetachShader(programID, vertexShaderID);
        glDetachShader(programID, fragmentShaderID);
        glDeleteShader(vertexShaderID);
        glDeleteShader(fragmentShaderID);
    }

    protected void storeAllUniformLocations(Uniform... uniforms) {
        for (Uniform uniform : uniforms) {
            uniform.storeUniformLocation(programID);
        }
        glValidateProgram(programID);
    }

    public void start() {
        glUseProgram(programID);
    }

    public void stop() {
        glUseProgram(0);
    }

    public void cleanUp() {
        stop();
        glDeleteProgram(programID);
    }

    private void bindAttributes() {
        glBindAttribLocation(programID, VERTICES_ATTRIBUTE_POINTER_INDEX, "in_position");
        glBindAttribLocation(programID, TEXTURES_ATTRIBUTE_POINTER_INDEX, "in_textureCoords");
        glBindAttribLocation(programID, NORMALS_ATTRIBUTE_POINTER_INDEX, "in_normal");
        glBindAttribLocation(programID, JOINTS_ATTRIBUTE_POINTER_INDEX, "in_jointIndices");
        glBindAttribLocation(programID, WEIGHTS_ATTRIBUTE_POINTER_INDEX, "in_weights");
    }

    private int loadShader(File file, int type) {
        StringBuilder shaderSource = new StringBuilder();
        try {
            BufferedReader reader = Utils.loadResourceAsBufferedReader(file);
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("//\n");
            }
            reader.close();
        } catch (Exception e) {
            System.err.println("Could not read file.");
            e.printStackTrace();
            System.exit(-1);
        }
        int shaderID = glCreateShader(type);
        glShaderSource(shaderID, shaderSource);
        glCompileShader(shaderID);
        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.out.println(glGetShaderInfoLog(shaderID, 500));
            System.err.println("Could not compile shader " + file);
            System.exit(-1);
        }
        return shaderID;
    }


}
