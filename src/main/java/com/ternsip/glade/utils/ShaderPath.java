package com.ternsip.glade.utils;

public class ShaderPath {
    private static String PATH = "/com/ternsip/glade/graphics/shader/";

    public static String getShaderPath(String fileName) {
        return PATH + fileName;
    }
}
