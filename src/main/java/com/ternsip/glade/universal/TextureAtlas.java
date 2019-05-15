package com.ternsip.glade.universal;

import com.ternsip.glade.utils.Utils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ternsip.glade.utils.Utils.ioResourceToByteBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12C.glTexSubImage3D;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengl.GL42.glTexStorage3D;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

public class TextureAtlas {

    private final static File MISSING_TEXTURE = new File("tools/missing.jpg");
    private final static String[] EXTENSIONS = {"jpg", "png", "bmp", "jpeg"};

    private final int atlasIndex;
    private final Map<File, Texture> fileToTexture;

    public TextureAtlas() {

        atlasIndex = glGenTextures();

        glBindTexture(GL_TEXTURE_2D_ARRAY, atlasIndex);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST); // GL_NEAREST_MIPMAP_LINEAR
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);

        ArrayList<Image> images = Utils.getResourceListing(EXTENSIONS)
                .stream()
                .map(Image::new)
                .collect(Collectors.toCollection(ArrayList::new));

        int maxWidth = 0;
        int maxHeight = 0;

        for (Image image : images) {
            maxWidth = Math.max(maxWidth, image.getWidth());
            maxHeight = Math.max(maxHeight, image.getHeight());
        }

        glTexStorage3D(GL_TEXTURE_2D_ARRAY, 3, GL_RGBA8, maxWidth, maxHeight, images.size());
        ByteBuffer cleanData = Utils.arrayToBuffer(new byte[maxWidth * maxHeight * 4]);

        fileToTexture = new HashMap<>();
        for (int layer = 0; layer < images.size(); ++layer) {
            Image image = images.get(layer);
            cleanData.rewind();
            // TODO level = mipmap level 0,1,2...
            // set the whole texture to transparent (so min/mag filters don't find bad data off the edge of the actual image data)
            glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, 0, 0, layer, maxWidth, maxHeight, 1, GL_RGBA, GL_UNSIGNED_BYTE, cleanData);
            glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, 0, 0, layer, image.getWidth(), image.getHeight(), 1, GL_RGBA, GL_UNSIGNED_BYTE, image.getDataBuffer());
            // TODO atlas numbers different sizes
            Vector2f maxUV = new Vector2f(image.getWidth() / (float) maxWidth, image.getHeight() / (float) maxHeight);
            Texture texture = new Texture(0, layer, maxUV);
            fileToTexture.put(image.getFile(), texture);
        }

        glGenerateMipmap(GL_TEXTURE_2D_ARRAY);
        glBindTexture(GL_TEXTURE_2D_ARRAY, 0);

    }

    public Texture getTexture(File file) {
        if (!fileToTexture.containsKey(file)) {
            System.out.println(String.format("Texture %s has not been found", file)); // TODO TO LOGS
            return fileToTexture.get(MISSING_TEXTURE);
        }
        return fileToTexture.get(file);
    }

    public void bind() {
        glActiveTexture(GL_TEXTURE0 + 0);
        glBindTexture(GL_TEXTURE_2D_ARRAY, atlasIndex);
        //glActiveTexture(GL_TEXTURE1);
        //glBindTexture(GL_TEXTURE_2D_ARRAY, atlasIndex);
        //glActiveTexture(GL_TEXTURE2);
        //glBindTexture(GL_TEXTURE_2D_ARRAY, atlasIndex);
    }

    public void unbind() {
        glActiveTexture(GL_TEXTURE0 + 0);
        glBindTexture(GL_TEXTURE_2D_ARRAY, 0);
        //glActiveTexture(GL_TEXTURE1);
        //glBindTexture(GL_TEXTURE_2D_ARRAY, 0);
        //glActiveTexture(GL_TEXTURE2);
        //glBindTexture(GL_TEXTURE_2D_ARRAY, 0);
    }

    public void cleanup() {
        glDeleteTextures(atlasIndex);
    }

    @Getter
    public static class Image {

        private final File file;
        private final int width;
        private final int height;
        private final ByteBuffer dataBuffer;

        Image(File file) {
            this.file = file;
            ByteBuffer imageData = ioResourceToByteBuffer(file);
            IntBuffer w = BufferUtils.createIntBuffer(1);
            IntBuffer h = BufferUtils.createIntBuffer(1);
            IntBuffer avChannels = BufferUtils.createIntBuffer(1);
            this.dataBuffer = stbi_load_from_memory(imageData, w, h, avChannels, 4);
            this.width = w.get();
            this.height = h.get();
        }
    }

    @RequiredArgsConstructor
    @Getter
    public static class Texture {

        private final int atlasNumber;
        private final int layer;
        private final Vector2f maxUV;

    }

}
