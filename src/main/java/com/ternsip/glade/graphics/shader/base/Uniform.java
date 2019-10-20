package com.ternsip.glade.graphics.shader.base;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;

@Getter
@Setter
@Slf4j
public abstract class Uniform<T> extends Locatable {

    public void locate(int programID, String name) {
        int location = glGetUniformLocation(programID, name);
        if (location == NOT_LOCATED) {
            log.debug("Uniform variable not found - {}", name);
        } else {
            setLocation(location);
        }
    }

    protected abstract void load(T value);

}
