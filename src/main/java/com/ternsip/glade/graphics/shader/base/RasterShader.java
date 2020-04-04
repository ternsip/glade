package com.ternsip.glade.graphics.shader.base;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;
import java.util.Collection;

import static org.lwjgl.opengl.GL20.*;

@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class RasterShader extends Shader {

    public static final AttributeData INDICES = new AttributeData(0, "indices", 3, AttributeData.ArrayType.ELEMENT_ARRAY);
    public static final AttributeData VERTICES = new AttributeData(1, "position", 3, AttributeData.ArrayType.FLOAT);

    @Override
    protected void construct() {
        int vertexShaderID = loadShader((File)findHeader("VERTEX_SHADER"), GL_VERTEX_SHADER);
        int fragmentShaderID = loadShader((File)findHeader("FRAGMENT_SHADER"), GL_FRAGMENT_SHADER);
        Collection<AttributeData> attributeData = collectAttributeData();
        int programID = glCreateProgram();
        glAttachShader(programID, vertexShaderID);
        glAttachShader(programID, fragmentShaderID);
        bindAttributes(programID, attributeData);
        glLinkProgram(programID);
        glDetachShader(programID, vertexShaderID);
        glDetachShader(programID, fragmentShaderID);
        glDeleteShader(vertexShaderID);
        glDeleteShader(fragmentShaderID);
        locateInputs(programID);
        glValidateProgram(programID);
        setProgramID(programID);
    }


}
