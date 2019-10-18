package com.ternsip.glade.graphics.visual.repository;

import com.ternsip.glade.common.logic.Utils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.stream.Collectors;

import static com.ternsip.glade.common.logic.Utils.loadResourceToByteBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.glTexSubImage3D;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengl.GL42.glTexStorage3D;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

/**
 * There are two types of atlases:
 * - GPU 3d array of textures
 */
@Slf4j
public class TextureRepository {

    public final static int MIPMAP_LEVELS = 4;
    public final static File MISSING_TEXTURE = new File("tools/missing.jpg");
    public final static String[] EXTENSIONS = {"jpg", "png", "bmp", "jpeg"};
    public final static int[] ATLAS_RESOLUTIONS = new int[]{16, 32, 64, 128, 256, 512, 1024, 2048, 4096};

    private final int[] atlases;
    private final Map<File, Texture> fileToTexture;

    public TextureRepository() {

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

            glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_NEAREST);
            glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_CLAMP);
            glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_CLAMP);

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
                glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, 0, 0, layer, image.getWidth(), image.getHeight(), 1, GL_RGBA, GL_UNSIGNED_BYTE, Utils.arrayToBuffer(image.getData()));
                Vector2f maxUV = new Vector2f(image.getWidth() / (float) atlasResolution, image.getHeight() / (float) atlasResolution);
                Texture texture = new Texture(atlasNumber, layer, maxUV);
                this.fileToTexture.put(image.getFile(), texture);
            }
            glGenerateMipmap(GL_TEXTURE_2D_ARRAY);
            glBindTexture(GL_TEXTURE_2D_ARRAY, 0);
        }

        images.forEach(image -> {
            if (!usedImages.contains(image)) {
                log.error(String.format("Image %s has not been loaded into atlas because it exceeds maximal size", image.getFile()));
            }
        });

        bind();
    }

    public Texture getTexture(File file) {
        if (!fileToTexture.containsKey(file)) {
            log.warn(String.format("Texture %s has not been found", file));
            return fileToTexture.get(MISSING_TEXTURE);
        }
        return fileToTexture.get(file);
    }

    public boolean isTextureExists(File file) {
        return fileToTexture.containsKey(file);
    }

    public void finish() {
        unbind();
        for (int atlasNumber = 0; atlasNumber < ATLAS_RESOLUTIONS.length; ++atlasNumber) {
            glDeleteTextures(atlases[atlasNumber]);
        }
    }

    private void bind() {
        for (int atlasNumber = 0; atlasNumber < ATLAS_RESOLUTIONS.length; ++atlasNumber) {
            glActiveTexture(GL_TEXTURE0 + atlasNumber);
            glBindTexture(GL_TEXTURE_2D_ARRAY, atlases[atlasNumber]);
        }
    }

    private void unbind() {
        for (int atlasNumber = 0; atlasNumber < ATLAS_RESOLUTIONS.length; ++atlasNumber) {
            glActiveTexture(GL_TEXTURE0 + atlasNumber);
            glBindTexture(GL_TEXTURE_2D_ARRAY, 0);
        }
    }

    @RequiredArgsConstructor
    @Getter
    private static class Image {

        public static int COMPONENT_RGBA = 4;

        private final File file;
        private final int width;
        private final int height;
        private final byte[] data;

        Image(File file) {
            this.file = file;
            ByteBuffer imageData = loadResourceToByteBuffer(file);
            IntBuffer w = BufferUtils.createIntBuffer(1);
            IntBuffer h = BufferUtils.createIntBuffer(1);
            IntBuffer avChannels = BufferUtils.createIntBuffer(1);
            this.data = Utils.bufferToArray(stbi_load_from_memory(imageData, w, h, avChannels, COMPONENT_RGBA));
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
