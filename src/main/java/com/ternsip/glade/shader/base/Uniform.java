package com.ternsip.glade.shader.base;

import lombok.Getter;
import lombok.Setter;

import static org.lwjgl.opengl.GL20C.glGetUniformLocation;

@Getter
@Setter
public abstract class Uniform<T> {

    private int location;

    public void locate(int programID, String name) {
        int location = glGetUniformLocation(programID, name);
        if (location == -1) {
            throw new IllegalArgumentException("Uniform variable " + name + " not found in shader!");
        }
        setLocation(location);
    }

    protected abstract void load(T value);

}
