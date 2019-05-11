package com.ternsip.glade.universal;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TextureCache {

    private static TextureCache INSTANCE;

    private Map<String, Texture> texturesMap;

    private TextureCache() {
        texturesMap = new HashMap<>();
    }

    public static synchronized TextureCache getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TextureCache();
        }
        return INSTANCE;
    }

    public Texture getTexture(File file) {
        Texture texture = texturesMap.get(file.getPath());
        if (texture == null) {
            try {
                texture = new Texture(file);
                texturesMap.put(file.getPath(), texture);
            } catch (Exception e) {
                System.out.println(e.getMessage()); // TODO to logs
            }
        }
        return texture;
    }
}
