package com.ternsip.glade.graphics.shader.base;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import static org.lwjgl.opengl.GL20C.glGetUniformLocation;

@Getter
@Setter
@Slf4j
public abstract class Uniform<T> {

    private int location;

    public void locate(int programID, String name) {
        int location = glGetUniformLocation(programID, name);
        if (location == -1) {
            log.warn("Uniform variable " + name + " not found in shader!");
        }
        setLocation(location);
    }

    protected abstract void load(T value);

}
