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

import static org.lwjgl.opengl.GL20.*;

@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Shader {

    private static int ACTIVE_PROGRAM_ID = -1;

    private int programID;

    protected abstract void construct();

    @SneakyThrows
    public static <T extends Shader> T createShader(Class<T> clazz) {
        Constructor<T> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        T shader = constructor.newInstance();
        shader.construct();
        return shader;
    }

    static int loadShader(File file, int type) {
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
    Object findHeader(String fieldName) {
        for (Field field : this.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) && field.getName().equals(fieldName)) {
                field.setAccessible(true);
                return field.get(this);
            }
        }
        throw new IllegalArgumentException(String.format("Can't find header %s", fieldName));
    }

    @SneakyThrows
    Collection<AttributeData> collectAttributeData() {
        Collection<AttributeData> attributeData = new ArrayList<>();
        for (Field field : this.getClass().getFields()) {
            if (Modifier.isStatic(field.getModifiers()) && field.getType() == AttributeData.class) {
                field.setAccessible(true);
                attributeData.add((AttributeData) field.get(this));
            }
        }
        return attributeData;
    }

    static void bindAttributes(int programID, Collection<AttributeData> attributeData) {
        for (AttributeData data : attributeData) {
            glBindAttribLocation(programID, data.getIndex(), data.getName());
        }
    }

    @SneakyThrows
    void locateInputs(int programID) {
        for (Field field : this.getClass().getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
                field.setAccessible(true);
                Object object = field.get(this);
                if (object instanceof Locatable) {
                    Locatable locatable = (Locatable) object;
                    String fieldName = field.getName();
                    locatable.locate(programID, fieldName);
                }
            }
        }
    }

    public void start() {
        // XXX Use caching for optimisation purposes
        if (ACTIVE_PROGRAM_ID != programID) {
            glUseProgram(programID);
            ACTIVE_PROGRAM_ID = programID;
        }
    }

    public void finish() {
        stop();
        glDeleteProgram(programID);
    }

    public void stop() {
        glUseProgram(0);
        ACTIVE_PROGRAM_ID = -1;
    }
}
