package com.ternsip.glade.graphics.shader.base;

import com.ternsip.glade.common.logic.Maths;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.lwjgl.BufferUtils;

import java.io.File;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glGetIntegeri_v;
import static org.lwjgl.opengl.GL43.*;

@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ComputeShader extends Shader {

    private Vector3ic workgroupCounts = new Vector3i(1);

    @Override
    protected void construct() {
        int computeShaderID = loadShader((File) findHeader("COMPUTE_SHADER"), GL_COMPUTE_SHADER);
        int computeProgramID = glCreateProgram();
        glAttachShader(computeProgramID, computeShaderID);
        glLinkProgram(computeProgramID);
        if (glGetProgrami(computeProgramID, GL_LINK_STATUS) == 0) { // TODO is this needed?
            String programLog = glGetProgramInfoLog(computeProgramID);
            throw new IllegalArgumentException(String.format("Could not link program reason: %s", programLog));
        }
        glDetachShader(computeProgramID, computeShaderID);
        glDeleteShader(computeShaderID);
        locateInputs(computeProgramID);
        glValidateProgram(computeProgramID);
        setProgramID(computeProgramID);
        IntBuffer workGroupsX = BufferUtils.createIntBuffer(1);
        IntBuffer workGroupsY = BufferUtils.createIntBuffer(1);
        IntBuffer workGroupsZ = BufferUtils.createIntBuffer(1);
        glGetIntegeri_v(GL_MAX_COMPUTE_WORK_GROUP_COUNT, 0, workGroupsX);
        glGetIntegeri_v(GL_MAX_COMPUTE_WORK_GROUP_COUNT, 1, workGroupsY);
        glGetIntegeri_v(GL_MAX_COMPUTE_WORK_GROUP_COUNT, 2, workGroupsZ);
        setWorkgroupCounts(new Vector3i(workGroupsX.get(), workGroupsY.get(), workGroupsZ.get()));
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

}
