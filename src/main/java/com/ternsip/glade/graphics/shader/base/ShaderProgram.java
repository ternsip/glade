package com.ternsip.glade.graphics.shader.base;

import com.ternsip.glade.common.logic.Utils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;

import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20C.*;

@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ShaderProgram {

    public static final AttributeData INDICES = new AttributeData(0, "indices", 3, AttributeData.ArrayType.ELEMENT_ARRAY);
    public static final AttributeData VERTICES = new AttributeData(1, "position", 3, AttributeData.ArrayType.FLOAT);
    private static int LAST_PROGRAM_ID = -100;
    @SuppressWarnings("unused")
    private int programID;

    @SneakyThrows
    public static <T extends ShaderProgram> T createShader(Class<T> clazz) {
        Constructor<T> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        T shader = constructor.newInstance();
        int vertexShaderID = loadShader((File) findHeader(shader, "VERTEX_SHADER"), GL_VERTEX_SHADER);
        int fragmentShaderID = loadShader((File) findHeader(shader, "FRAGMENT_SHADER"), GL_FRAGMENT_SHADER);
        Collection<AttributeData> attributeData = collectAttributeData(shader);
        int programID = glCreateProgram();
        glAttachShader(programID, vertexShaderID);
        glAttachShader(programID, fragmentShaderID);
        bindAttributes(programID, attributeData);
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

    private static int loadShader(File file, int type) {
        int shaderID = glCreateShader(type);
        glShaderSource(shaderID, new String(Utils.loadResourceAsByteArray(file)));
        glCompileShader(shaderID);
        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            String error = glGetShaderInfoLog(shaderID, 1024);
            throw new IllegalArgumentException(String.format("Could not compile shader %s - %s", file.getName(), error));
        }
        return shaderID;
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
    private static Collection<AttributeData> collectAttributeData(ShaderProgram instance) {
        Collection<AttributeData> attributeData = new ArrayList<>();
        for (Field field : instance.getClass().getFields()) {
            if (Modifier.isStatic(field.getModifiers()) && field.getType() == AttributeData.class) {
                field.setAccessible(true);
                attributeData.add((AttributeData) field.get(instance));
            }
        }
        return attributeData;
    }

    private static void bindAttributes(int programID, Collection<AttributeData> attributeData) {
        for (AttributeData data : attributeData) {
            glBindAttribLocation(programID, data.getIndex(), data.getName());
        }
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

    public void start() {
        // XXX Use caching for optimisation purposes
        if (LAST_PROGRAM_ID != programID) {
            glUseProgram(programID);
            LAST_PROGRAM_ID = programID;
        }
    }

    public void finish() {
        stop();
        glDeleteProgram(programID);
    }

    public void stop() {
        // XXX Just simply do not unbind the shader program for optimisation purposes
        //glUseProgram(0);
    }

}
