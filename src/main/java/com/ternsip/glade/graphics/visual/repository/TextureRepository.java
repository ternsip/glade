package com.ternsip.glade.graphics.visual.repository;

import com.ternsip.glade.common.Utils;
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

import static com.ternsip.glade.common.Utils.loadResourceToByteBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12C.glTexSubImage3D;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengl.GL42.glTexStorage3D;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

/**
 * There are two types of atlases:
 * - GPU 3d array of textures
 * - Combined images in atlas directories, it all have special parent directory
 */
@Slf4j
public class TextureRepository {

    public final static int MIPMAP_LEVELS = 5;
    public final static File MISSING_TEXTURE = new File("tools/missing.jpg");
    public final static String[] EXTENSIONS = {"jpg", "png", "bmp", "jpeg"};
    public final static int[] ATLAS_RESOLUTIONS = new int[]{16, 32, 64, 128, 256, 512, 1024, 2048, 4096};
    public final static String ATLASES_PARENT_FOLDER = "atlases";

    private final int[] atlases;
    private final Map<File, Texture> fileToTexture;
    private final Map<File, AtlasDecoder> directoryToAtlasDecoder;

    public TextureRepository() {

        ArrayList<Image> images = Utils.getResourceListing(EXTENSIONS)
                .stream()
                .map(Image::new)
                .collect(Collectors.toCollection(ArrayList::new));

        Map<File, ImageAtlas> atlasParentFolderToAtlasImage = generateAtlasMapping(images);
        images.addAll(atlasParentFolderToAtlasImage.values());

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

        directoryToAtlasDecoder = new HashMap<>();
        fileToTexture.forEach((file, texture) -> {
            if (atlasParentFolderToAtlasImage.containsKey(file)) {
                ImageAtlas imageAtlas = atlasParentFolderToAtlasImage.get(file);
                directoryToAtlasDecoder.put(file, new AtlasDecoder(file, texture, imageAtlas.getFileToAtlasFragment()));
            }
        });

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

    public AtlasDecoder getAtlasDecoder(File directory) {
        if (!directoryToAtlasDecoder.containsKey(directory)) {
            throw new IllegalArgumentException(String.format("Atlas %s has not been found", directory));
        }
        return directoryToAtlasDecoder.get(directory);
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

    private Map<File, ImageAtlas> generateAtlasMapping(Collection<Image> images) {
        Map<File, Image> pathToImageForAtlas = images.stream()
                .filter(e -> Utils.isSubDirectoryPresent(e.getFile(), ATLASES_PARENT_FOLDER))
                .collect(Collectors.toMap(Image::getFile, e -> e, (a, b) -> a, HashMap::new));
        Map<File, Collection<File>> atlasParentFolderToFiles = Utils.combineByParentDirectory(pathToImageForAtlas.keySet());
        Map<File, Collection<Image>> atlasParentFolderToImages = atlasParentFolderToFiles.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream().map(pathToImageForAtlas::get).collect(Collectors.toList()),
                        (a, b) -> a,
                        HashMap::new
                ));
        return atlasParentFolderToImages.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> ImageAtlas.buildImageAtlas(e.getValue(), e.getKey()), (a, b) -> a, HashMap::new));
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

    @Getter
    private static class ImageAtlas extends Image {

        private final Map<File, AtlasFragment> fileToAtlasFragment;

        private ImageAtlas(
                File file,
                int width,
                int height,
                byte[] data,
                Map<File, AtlasFragment> fileToAtlasFragment
        ) {
            super(file, width, height, data);
            this.fileToAtlasFragment = fileToAtlasFragment;
        }

        static ImageAtlas buildImageAtlas(Collection<Image> images, File file) {
            Map<File, AtlasFragment> fileToAtlasFragment = new HashMap<>();
            int maxWidth = 0;
            int maxHeight = 0;
            for (Image image : images) {
                maxWidth = Math.max(maxWidth, image.getWidth());
                maxHeight = Math.max(maxHeight, image.getHeight());
            }
            int rowImages = (int) Math.ceil(Math.sqrt(images.size()));
            int columnImages = (int) Math.ceil(Math.sqrt(images.size()));
            int finalWidth = maxWidth * rowImages;
            int finalHeight = maxHeight * columnImages;
            byte[] finalImageBytes = new byte[finalWidth * finalHeight * COMPONENT_RGBA];
            int imageNumber = 0;
            float fragmentWidthNormalized = 1f / rowImages;
            float fragmentHeightNormalized = 1f / columnImages;
            for (Image image : images) {
                int imageRow = imageNumber % rowImages;
                int imageColumn = imageNumber / rowImages;
                byte[] imageBytes = image.getData();
                for (int y = 0; y < image.getHeight(); ++y) {
                    int offset = (finalWidth * (y + imageColumn * maxHeight) + imageRow * maxWidth) * COMPONENT_RGBA;
                    int lineSize = image.getWidth() * COMPONENT_RGBA;
                    System.arraycopy(imageBytes, lineSize * y, finalImageBytes, offset, lineSize);
                }
                float widthNormalized = (float) image.getWidth() / finalWidth;
                float heightNormalized = (float) image.getHeight() / finalHeight;
                AtlasFragment atlasFragment = new AtlasFragment(
                        fragmentWidthNormalized * imageRow, fragmentHeightNormalized * imageColumn,
                        fragmentWidthNormalized * imageRow + widthNormalized, fragmentHeightNormalized * imageColumn + heightNormalized
                );
                fileToAtlasFragment.put(image.getFile(), atlasFragment);
                imageNumber++;
            }
            return new ImageAtlas(file, finalWidth, finalHeight, finalImageBytes, fileToAtlasFragment);
        }

    }

    @RequiredArgsConstructor
    @Getter
    public static class Texture {

        private final int atlasNumber;
        private final int layer;
        private final Vector2f maxUV;

    }

    @RequiredArgsConstructor
    @Getter
    public static class AtlasDecoder {

        private final File atlasDirectory;
        private final Texture texture;
        private final Map<File, AtlasFragment> fileToAtlasFragment;

        public boolean isTextureExists(File file) {
            return getFileToAtlasFragment().containsKey(file);
        }

    }

    @RequiredArgsConstructor
    @Getter
    public static class AtlasFragment {

        private final float startU;
        private final float startV;
        private final float endU;
        private final float endV;

    }

}
