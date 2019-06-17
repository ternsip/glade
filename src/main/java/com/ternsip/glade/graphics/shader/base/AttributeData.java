package com.ternsip.glade.graphics.shader.base;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

@RequiredArgsConstructor
@Getter
public class AttributeData {

    private final int index;
    private final String name;
    private final int numberPerVertex;
    private final ArrayType type;

    @RequiredArgsConstructor
    @Getter
    public enum ArrayType {

        ELEMENT_ARRAY(IntBuffer.class),
        INT(IntBuffer.class),
        FLOAT(FloatBuffer.class);

        private final Class<? extends Buffer> bufferClass;

    }

}