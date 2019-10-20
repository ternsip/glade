package com.ternsip.glade.graphics.shader.base;

import com.ternsip.glade.graphics.general.ShaderBuffer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL43.*;

@Getter
@Setter
@Slf4j
public class BufferLayout extends Locatable {

    public void locate(int programID, String name) {
        int boxesResourceIndex = glGetProgramResourceIndex(programID, GL_SHADER_STORAGE_BLOCK, name);
        if (boxesResourceIndex == NOT_LOCATED) {
            log.debug("Buffer layout not found - {}", name);
        } else {
            IntBuffer props = BufferUtils.createIntBuffer(1);
            IntBuffer params = BufferUtils.createIntBuffer(1);
            props.put(0, GL_BUFFER_BINDING);
            glGetProgramResourceiv(programID, GL_SHADER_STORAGE_BLOCK, boxesResourceIndex, props, null, params);
            int location = params.get(0);
            setLocation(location);
        }
    }

    public void load(ShaderBuffer shaderBuffer) {
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, getLocation(), shaderBuffer.getSsbo());
    }
}
