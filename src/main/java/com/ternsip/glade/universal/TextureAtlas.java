package com.ternsip.glade.universal;

import com.ternsip.glade.utils.Utils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;
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

    private final static int MIPMAP_LEVELS = 5;
    private final static File MISSING_TEXTURE = new File("tools/missing.jpg");
    private final static String[] EXTENSIONS = {"jpg", "png", "bmp", "jpeg"};
    private final static int[] ATLAS_RESOLUTIONS = new int[]{16, 32, 64, 128, 256, 512, 1024, 2048, 4096};

    private final int[] atlases;
    private final Map<File, Texture> fileToTexture;

    public TextureAtlas() {

        ArrayList<Image> images = Utils.getResourceListing(EXTENSIONS)
                .stream()
                .map(Image::new)
                .collect(Collectors.toCollection(ArrayList::new));

        Set<Image> usedImages = new HashSet<>();

        this.atlases = new int[ATLAS_RESOLUTIONS.length];
        this.fileToTexture = new HashMap<>();

        for (int atlasNumber = 0; atlasNumber < ATLAS_RESOLUTIONS.length; ++atlasNumber) {

            this.atlases[atlasNumber] = glGenTextures();

            glBindTexture(GL_TEXTURE_2D_ARRAY, this.atlases[atlasNumber]);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);

            final int atlasResolution = ATLAS_RESOLUTIONS[atlasNumber];

            ArrayList<Image> suitableImages = images
                    .stream()
                    .filter(image -> !usedImages.contains(image) && image.getWidth() <= atlasResolution && image.getHeight() <= atlasResolution)
                    .collect(Collectors.toCollection(ArrayList::new));

            usedImages.addAll(suitableImages);

            glTexStorage3D(GL_TEXTURE_2D_ARRAY, MIPMAP_LEVELS, GL_RGBA8, atlasResolution, atlasResolution, suitableImages.size());
            ByteBuffer cleanData = Utils.arrayToBuffer(new byte[atlasResolution * atlasResolution * 4]);

            for (int layer = 0; layer < suitableImages.size(); ++layer) {
                Image image = suitableImages.get(layer);
                cleanData.rewind();
                // set the whole texture to transparent (so min/mag filters don't find bad data off the edge of the actual image data)
                glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, 0, 0, layer, atlasResolution, atlasResolution, 1, GL_RGBA, GL_UNSIGNED_BYTE, cleanData);
                glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, 0, 0, layer, image.getWidth(), image.getHeight(), 1, GL_RGBA, GL_UNSIGNED_BYTE, image.getDataBuffer());
                Vector2f maxUV = new Vector2f(image.getWidth() / (float) atlasResolution, image.getHeight() / (float) atlasResolution);
                Texture texture = new Texture(atlasNumber, layer, maxUV);
                this.fileToTexture.put(image.getFile(), texture);
            }
            glGenerateMipmap(GL_TEXTURE_2D_ARRAY);
            glBindTexture(GL_TEXTURE_2D_ARRAY, 0);
        }

        images.forEach(image -> {
            if (!usedImages.contains(image)) {
                // TODO to logs
                System.out.println(String.format("Image %s has not been loaded into atlas because it exceeds maximal size", image.getFile()));
            }
        });

    }

    public Texture getTexture(File file) {
        if (!fileToTexture.containsKey(file)) {
            System.out.println(String.format("Texture %s has not been found", file)); // TODO TO LOGS
            return fileToTexture.get(MISSING_TEXTURE);
        }
        return fileToTexture.get(file);
    }

    public void bind() {
        for (int atlasNumber = 0; atlasNumber < ATLAS_RESOLUTIONS.length; ++atlasNumber) {
            glActiveTexture(GL_TEXTURE0 + atlasNumber);
            glBindTexture(GL_TEXTURE_2D_ARRAY, atlases[atlasNumber]);
        }
    }

    public void unbind() {
        for (int atlasNumber = 0; atlasNumber < ATLAS_RESOLUTIONS.length; ++atlasNumber) {
            glActiveTexture(GL_TEXTURE0 + atlasNumber);
            glBindTexture(GL_TEXTURE_2D_ARRAY, 0);
        }
    }

    public void cleanup() {
        for (int atlasNumber = 0; atlasNumber < ATLAS_RESOLUTIONS.length; ++atlasNumber) {
            glDeleteTextures(atlases[atlasNumber]);
        }
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
