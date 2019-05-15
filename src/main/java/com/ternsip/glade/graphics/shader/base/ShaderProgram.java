package com.ternsip.glade.graphics.shader.base;

import com.ternsip.glade.utils.Utils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static com.ternsip.glade.graphics.general.Mesh.*;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20C.*;

@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ShaderProgram {

    @SuppressWarnings("unused")
    private int programID;

    @SneakyThrows
    public static <T extends ShaderProgram> T createShader(Class<T> clazz) {
        Constructor<T> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        T shader = constructor.newInstance();
        int vertexShaderID = loadShader((File) findHeader(shader, "VERTEX_SHADER"), GL_VERTEX_SHADER);
        int fragmentShaderID = loadShader((File) findHeader(shader, "FRAGMENT_SHADER"), GL_FRAGMENT_SHADER);
        int programID = glCreateProgram();
        glAttachShader(programID, vertexShaderID);
        glAttachShader(programID, fragmentShaderID);
        bindAttributes(programID);
        glLinkProgram(programID);
        glDetachShader(programID, vertexShaderID);
        glDetachShader(programID, fragmentShaderID);
        glDeleteShader(vertexShaderID);
        glDeleteShader(fragmentShaderID);
        loadUniformLocations(shader, programID);
        glValidateProgram(programID);
        shader.setProgramID(programID);
        return shader;
    }

    @SneakyThrows
    private static Object findHeader(ShaderProgram instance, String fieldName) {
        for (Field field : instance.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) && field.getName().equals(fieldName)) {
                field.setAccessible(true);
                return field.get(instance);
            }
        }
        throw new IllegalArgumentException(String.format("Can't find filed %s", fieldName));
    }

    @SneakyThrows
    private static void loadUniformLocations(ShaderProgram instance, int programID) {
        for (Field field : instance.getClass().getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
                field.setAccessible(true);
                Object object = field.get(instance);
                if (object instanceof Uniform) {
                    Uniform uniform = (Uniform) object;
                    String fieldName = field.getName();
                    uniform.locate(programID, fieldName);
                }
            }
        }
    }

    private static void bindAttributes(int programID) {
        glBindAttribLocation(programID, VERTICES_ATTRIBUTE_POINTER_INDEX, "position");
        glBindAttribLocation(programID, TEXTURES_ATTRIBUTE_POINTER_INDEX, "textureCoordinates");
        glBindAttribLocation(programID, NORMALS_ATTRIBUTE_POINTER_INDEX, "normal");
        glBindAttribLocation(programID, BONES_ATTRIBUTE_POINTER_INDEX, "boneIndices");
        glBindAttribLocation(programID, WEIGHTS_ATTRIBUTE_POINTER_INDEX, "weights");
    }

    private static int loadShader(File file, int type) {
        int shaderID = glCreateShader(type);
        glShaderSource(shaderID, new String(Utils.loadResourceAsByteArray(file)));
        glCompileShader(shaderID);
        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            String error = glGetShaderInfoLog(shaderID, 1024);
            throw new IllegalArgumentException(String.format("Could not compile shader %s", error));
        }
        return shaderID;
    }

    public void start() {
        glUseProgram(programID);
    }

    public void stop() {
        glUseProgram(0);
    }

    public void finish() {
        stop();
        glDeleteProgram(programID);
    }

}
