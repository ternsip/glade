package com.ternsip.glade.utils;

import lombok.SneakyThrows;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Utils {

    public static InputStream loadResourceAsStream(File file) {
        InputStream in = Utils.class.getClassLoader().getResourceAsStream(file.getPath());
        if (in == null) {
            throw new IllegalArgumentException("Can't find file: " + file.getPath());
        }
        return in;
    }

    @SneakyThrows
    public static FileInputStream  loadResourceAsFileStream(File file) {
        URL resource = Utils.class.getClassLoader().getResource(file.getPath());
        if (resource == null) {
            throw new IllegalArgumentException("Can't find file: " + file.getPath());
        }
        File rFile = new File(resource.toURI());
        return new FileInputStream(rFile);
    }

    @SneakyThrows
    public static BufferedReader loadResourceAsBufferedReader(File file) {
        return new BufferedReader(new InputStreamReader(loadResourceAsStream(file), StandardCharsets.UTF_8));
    }


}
