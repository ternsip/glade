package com.ternsip.glade.graphics.general;

import com.ternsip.glade.common.logic.Utils;
import com.ternsip.glade.graphics.shader.base.AttributeData;
import com.ternsip.glade.graphics.shader.base.MeshAttributes;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL15;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static com.ternsip.glade.graphics.shader.base.ShaderProgram.INDICES;
import static com.ternsip.glade.graphics.shader.base.ShaderProgram.VERTICES;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

@Getter
@Setter
public class Mesh {

    public static final int MAX_VERTICES = 1 << 16;
    public static final float MIN_INTERNAL_SIZE = 0.01f;

    public static int VERTICES_ATTRIBUTE_POINTER_INDEX = 0;
    public static int NORMALS_ATTRIBUTE_POINTER_INDEX = 1;
    public static int COLORS_ATTRIBUTE_POINTER_INDEX = 2;
    public static int TEXTURES_ATTRIBUTE_POINTER_INDEX = 3;
    public static int WEIGHTS_ATTRIBUTE_POINTER_INDEX = 4;
    public static int BONES_ATTRIBUTE_POINTER_INDEX = 5;

    private final MeshAttributes meshAttributes;
    private final Material material;
    private final float normalizingScale;
    private final boolean dynamic;

    private final int vao;
    private final Map<AttributeData, Integer> vbos = new HashMap<>();
    private final AnimationData animationData;

    private int indicesCount;
    private int vertexCount;

    public Mesh(MeshAttributes meshAttributes, Material material) {
        this(meshAttributes, material, new AnimationData(), false);
    }

    public Mesh(MeshAttributes meshAttributes, Material material, boolean dynamic) {
        this(meshAttributes, material, new AnimationData(), dynamic);
    }

    public Mesh(MeshAttributes meshAttributes, Material material,AnimationData animationData) {
        this(meshAttributes, material, animationData, false);
    }

    public Mesh(MeshAttributes meshAttributes, Material material, AnimationData animationData, boolean dynamic) {
        this.animationData = animationData;
        this.dynamic = dynamic;
        this.meshAttributes = meshAttributes;
        this.vertexCount = meshAttributes.getVerticesBuffer().limit() / VERTICES.getNumberPerVertex();
        this.indicesCount = meshAttributes.getAttributeToBuffer().containsKey(INDICES) ? meshAttributes.getAttributeToBuffer().get(INDICES).limit() : 0;
        if (vertexCount == 0) {
            throw new IllegalArgumentException("Number of vertices should not be zero");
        }
        if (vertexCount > MAX_VERTICES) {
            throw new IllegalArgumentException(String.format("Number of vertices is more than the maximum: %s", vertexCount));
        }
        meshAttributes.getAttributeToBuffer().entrySet().removeIf(e -> e.getValue().limit() == 0);
        meshAttributes.getAttributeToBuffer().forEach((k, v) -> Utils.assertThat(k == INDICES || vertexCount == v.limit() / k.getNumberPerVertex()));

        this.normalizingScale = calculateNormalizingScale(Utils.bufferToArray(meshAttributes.getVerticesBuffer()));
        this.material = material;
        this.vao = glGenVertexArrays();

        assignBuffers();

        if (!dynamic) {
            meshAttributes.getAttributeToBuffer().clear();
        }

    }

    private static float calculateNormalizingScale(float[] vertices) {
        Vector3f lowestPoint = new Vector3f(Float.MAX_VALUE / 4);
        Vector3f highestPoint = new Vector3f(-Float.MAX_VALUE / 4);
        for (int i = 0; i < vertices.length / 3; ++i) {
            lowestPoint.set(
                    Math.min(lowestPoint.x(), vertices[i * 3]),
                    Math.min(lowestPoint.y(), vertices[i * 3 + 1]),
                    Math.min(lowestPoint.z(), vertices[i * 3 + 2])
            );
            highestPoint.set(
                    Math.max(highestPoint.x(), vertices[i * 3]),
                    Math.max(highestPoint.y(), vertices[i * 3 + 1]),
                    Math.max(highestPoint.z(), vertices[i * 3 + 2])
            );
        }
        Vector3f bounds = highestPoint.sub(lowestPoint, new Vector3f()).max(new Vector3f(MIN_INTERNAL_SIZE));
        return 2 / Math.max(bounds.x(), Math.max(bounds.y(), bounds.z()));
    }

    public void updateBuffers() {
        if (!isDynamic()) {
            throw new IllegalArgumentException("You can't update static meshes");
        }
        assignBuffers();
    }

    public void render() {

        if (getVertexCount() == 0) {
            return;
        }

        glBindVertexArray(vao);

        getVbos().keySet().forEach(attributeData -> {
            glEnableVertexAttribArray(attributeData.getIndex());
        });

        if (getIndicesCount() == 0) {
            glDrawArrays(GL_TRIANGLES, 0, getVertexCount());
        } else {
            glDrawElements(GL_TRIANGLES, getIndicesCount(), GL_UNSIGNED_INT, 0);
        }

        getVbos().keySet().forEach(attributeData -> {
            glDisableVertexAttribArray(attributeData.getIndex());
        });

        glBindVertexArray(0);

    }

    public void finish() {
        glDeleteVertexArrays(vao);
        getVbos().values().forEach(GL15::glDeleteBuffers);
    }

    private void assignBuffers() {
        glBindVertexArray(vao);
        getMeshAttributes().getAttributeToBuffer().forEach((attributeData, buffer) -> {
            int vbo = getVbos().computeIfAbsent(attributeData, e -> glGenBuffers());
            buffer.rewind();
            if (attributeData.getType() == AttributeData.ArrayType.ELEMENT_ARRAY) {
                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo);
                glBufferData(GL_ARRAY_BUFFER, NULL, isDynamic() ? GL_STREAM_DRAW : GL_STATIC_DRAW);
                glBufferData(GL_ELEMENT_ARRAY_BUFFER, (IntBuffer) buffer, isDynamic() ? GL_STREAM_DRAW : GL_STATIC_DRAW);
            }
            if (attributeData.getType() == AttributeData.ArrayType.FLOAT) {
                glBindBuffer(GL_ARRAY_BUFFER, vbo);
                glBufferData(GL_ARRAY_BUFFER, (FloatBuffer) buffer, isDynamic() ? GL_STREAM_DRAW : GL_STATIC_DRAW);
                glVertexAttribPointer(attributeData.getIndex(), attributeData.getNumberPerVertex(), GL_FLOAT, false, 0, 0);
                glBindBuffer(GL_ARRAY_BUFFER, 0);
            }
            if (attributeData.getType() == AttributeData.ArrayType.INT) {
                glBindBuffer(GL_ARRAY_BUFFER, vbo);
                glBufferData(GL_ARRAY_BUFFER, (IntBuffer) buffer, isDynamic() ? GL_STREAM_DRAW : GL_STATIC_DRAW);
                glVertexAttribIPointer(attributeData.getIndex(), attributeData.getNumberPerVertex(), GL_INT, 0, 0);
                glBindBuffer(GL_ARRAY_BUFFER, 0);
            }
        });
        glBindVertexArray(0);
    }

}
