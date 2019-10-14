package com.ternsip.glade.graphics.shader.base;

import com.ternsip.glade.common.logic.Utils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL43.glDispatchCompute;
import static org.lwjgl.opengl.GL43C.GL_COMPUTE_SHADER;

@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public abstract class ShaderProgram {

    public static final AttributeData INDICES = new AttributeData(0, "indices", 3, AttributeData.ArrayType.ELEMENT_ARRAY);
    public static final AttributeData VERTICES = new AttributeData(1, "position", 3, AttributeData.ArrayType.FLOAT);
    public static int ACTIVE_PROGRAM_ID = -1;
    private int rasterProgramID = -1;
    private int computeProgramID = -1;

    @SneakyThrows
    public static <T extends ShaderProgram> T createShader(Class<T> clazz) {
        Constructor<T> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        T shader = constructor.newInstance();

        File vertexShaderFile = (File) findHeader(shader, "VERTEX_SHADER").orElseThrow(() -> new IllegalArgumentException("Can't find vertex shader"));
        File fragmentShaderFile = (File) findHeader(shader, "FRAGMENT_SHADER").orElseThrow(() -> new IllegalArgumentException("Can't find fragment shader"));
        int vertexShaderID = loadShader(vertexShaderFile, GL_VERTEX_SHADER);
        int fragmentShaderID = loadShader(fragmentShaderFile, GL_FRAGMENT_SHADER);
        Collection<AttributeData> attributeData = collectAttributeData(shader);
        int rasterProgramID = glCreateProgram();
        glAttachShader(rasterProgramID, vertexShaderID);
        glAttachShader(rasterProgramID, fragmentShaderID);
        bindAttributes(rasterProgramID, attributeData);
        glLinkProgram(rasterProgramID);
        glDetachShader(rasterProgramID, vertexShaderID);
        glDetachShader(rasterProgramID, fragmentShaderID);
        glDeleteShader(vertexShaderID);
        glDeleteShader(fragmentShaderID);
        loadInputLocations(shader, rasterProgramID);
        glValidateProgram(rasterProgramID);
        shader.setRasterProgramID(rasterProgramID);

        Optional computeShaderFile = findHeader(shader, "COMPUTE_SHADER");
        if (computeShaderFile.isPresent()) {
            int computeShaderID = loadShader((File) computeShaderFile.get(), GL_COMPUTE_SHADER);
            int computeProgramID = glCreateProgram();
            glAttachShader(computeProgramID, computeShaderID);
            glLinkProgram(computeProgramID);
            if (glGetProgrami(computeProgramID, GL_LINK_STATUS) == 0) { // TODO is this needed?
                String programLog = glGetProgramInfoLog(computeProgramID);
                throw new IllegalArgumentException(String.format("Could not link program reason: %s", programLog));
            }
            glDetachShader(computeProgramID, computeShaderID);
            glDeleteShader(computeShaderID);
            loadInputLocations(shader, computeProgramID);
            glValidateProgram(computeProgramID);
            shader.setComputeProgramID(computeProgramID);
        }

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
    private static Optional<Object> findHeader(ShaderProgram instance, String fieldName) {
        for (Field field : instance.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) && field.getName().equals(fieldName)) {
                field.setAccessible(true);
                return Optional.of(field.get(instance));
            }
        }
        return Optional.empty();
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
    private static void loadInputLocations(ShaderProgram instance, int programID) {
        for (Field field : instance.getClass().getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
                field.setAccessible(true);
                Object object = field.get(instance);
                if (object instanceof Locatable) {
                    Locatable locatable = (Locatable) object;
                    String fieldName = field.getName();
                    locatable.locate(programID, fieldName);
                }
            }
        }
    }

    public void startRaster() {
        if (ACTIVE_PROGRAM_ID != rasterProgramID) {
            glUseProgram(rasterProgramID);
            ACTIVE_PROGRAM_ID = rasterProgramID;
        }
    }

    public void startCompute() {
        if (ACTIVE_PROGRAM_ID != computeProgramID) {
            glUseProgram(computeProgramID);
            ACTIVE_PROGRAM_ID = computeProgramID;
        }
    }

    public void compute(int size) {
        glDispatchCompute(size, 1, 1); // TODO think about dimensions
    }

    public void compute(int x, int y, int z) {
        glDispatchCompute(x, y, z);
    }

    public void finish() {
        stop();
        glDeleteProgram(rasterProgramID);
        glDeleteProgram(computeProgramID);
    }

    public void stop() {
        // XXX Just simply do not unbind the shader program for optimisation purposes
        //glUseProgram(0);
    }

}
