package com.ternsip.glade.graphics.shader.base;

import com.ternsip.glade.common.logic.Utils;
import lombok.Getter;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static com.ternsip.glade.graphics.shader.base.RasterShader.VERTICES;

@Getter
public class MeshAttributes {

    private final Map<AttributeData, Buffer> attributeToBuffer = new HashMap<>();

    public MeshAttributes add(AttributeData attributeData, float[] array) {
        return add(attributeData, Utils.arrayToBuffer(array));
    }

    public MeshAttributes add(AttributeData attributeData, int[] array) {
        return add(attributeData, Utils.arrayToBuffer(array));
    }

    public MeshAttributes add(AttributeData attributeData, Buffer buffer) {
        if (!attributeData.getType().getBufferClass().isAssignableFrom(buffer.getClass())) {
            throw new IllegalArgumentException(String.format(
                    "You are trying to bind buffer with wrong type: %s, but needed %s",
                    buffer.getClass().getSimpleName(),
                    attributeData.getType().getBufferClass().getSimpleName()
            ));
        }
        if (getAttributeToBuffer().containsKey(attributeData)) {
            throw new IllegalArgumentException(String.format("Attribute already exists! %s", attributeData.getName()));
        }
        if (buffer.limit() % attributeData.getNumberPerVertex() > 0) {
            throw new IllegalArgumentException("Buffer number of elements is not multiple to number per vertex");
        }
        getAttributeToBuffer().put(attributeData, buffer);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T extends Buffer> T getBuffer(AttributeData attributeData) {
        if (!getAttributeToBuffer().containsKey(attributeData)) {
            throw new IllegalArgumentException("Attribute data does not exists!");
        }
        return (T) getAttributeToBuffer().get(attributeData);
    }

    public FloatBuffer getVerticesBuffer() {
        if (!getAttributeToBuffer().containsKey(VERTICES)) {
            throw new IllegalArgumentException("Vertices should always exist in mesh!");
        }
        return (FloatBuffer) getAttributeToBuffer().get(VERTICES);
    }

}
