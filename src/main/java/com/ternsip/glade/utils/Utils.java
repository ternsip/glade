package com.ternsip.glade.utils;

import com.ternsip.glade.shader.ShaderProgram;

import java.io.File;
import java.io.InputStream;

public class Utils {

    public static InputStream loadResourceAsStream(File file) {
        InputStream in = Utils.class.getClassLoader().getResourceAsStream(file.getPath());
        if (in == null) {
            throw new IllegalArgumentException("Can't find file: " + file.getPath());
        }
        return in;
    }


}
