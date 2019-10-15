package com.ternsip.glade.graphics.shader.base;

import com.ternsip.glade.common.logic.Maths;
import com.ternsip.glade.common.logic.Utils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glGetIntegeri_v;
import static org.lwjgl.opengl.GL43.*;
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
    private Vector3ic workgroupCounts = new Vector3i(1);

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
            IntBuffer workGroupsX = BufferUtils.createIntBuffer(1);
            IntBuffer workGroupsY = BufferUtils.createIntBuffer(1);
            IntBuffer workGroupsZ = BufferUtils.createIntBuffer(1);
            glGetIntegeri_v(GL_MAX_COMPUTE_WORK_GROUP_COUNT, 0, workGroupsX);
            glGetIntegeri_v(GL_MAX_COMPUTE_WORK_GROUP_COUNT, 1, workGroupsY);
            glGetIntegeri_v(GL_MAX_COMPUTE_WORK_GROUP_COUNT, 2, workGroupsZ);
            shader.setWorkgroupCounts(new Vector3i(workGroupsX.get(), workGroupsY.get(), workGroupsZ.get()));
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
        int rest = size;
        int x = Maths.clamp(1, workgroupCounts.x(), rest);
        rest = rest / x + (rest % x > 0 ? 1 : 0);
        int y = Maths.clamp(1, workgroupCounts.y(), rest);
        rest = rest / y + (rest % y > 0 ? 1 : 0);
        int z = Maths.clamp(1, workgroupCounts.z(), rest);
        rest = rest / z + (rest % z > 0 ? 1 : 0);
        if (rest != 1) {
            throw new IllegalArgumentException(String.format("Size %s can not be packet into GPU workgroup %s", size, workgroupCounts));
        }
        glDispatchCompute(x, y, z);
    }

    public void finish() {
        stop();
        glDeleteProgram(rasterProgramID);
        if (computeProgramID != -1) {
            glDeleteProgram(computeProgramID);
        }
    }

    public void stop() {
        glUseProgram(0);
        ACTIVE_PROGRAM_ID = -1;
    }

}
