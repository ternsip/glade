package com.ternsip.glade.graphics.visual.repository;

import com.ternsip.glade.graphics.display.Displayable;
import com.ternsip.glade.graphics.general.Texture;
import com.ternsip.glade.universe.parts.blocks.Block;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;

@Getter
public class TexturePackRepository implements Displayable {

    public static final File DEFAULT_BLOCK_ATLAS_DIRECTORY = new File("atlases/blocks");

    private final TextureCubeMap[] blocksTextures = new TextureCubeMap[Block.getSize()];

    private TextureRepository.AtlasDecoder blocksAtlasDecoder;

    public TexturePackRepository() {
        reloadBlocksTextureAltas(DEFAULT_BLOCK_ATLAS_DIRECTORY);
    }

    public TextureCubeMap getCubeMap(Block block) {
        return getBlocksTextures()[block.getIndex()];
    }

    public void reloadBlocksTextureAltas(File atlasDirectory) {

        blocksAtlasDecoder = getDisplayManager().getGraphicalRepository().getTextureRepository().getAtlasDecoder(atlasDirectory);

        for (int blockIndex = 0; blockIndex < blocksTextures.length; ++blockIndex) {

            File sideTop = null;
            File sideBottom = null;
            File sideLeft = null;
            File sideRight = null;
            File sideFront = null;
            File sideBack = null;

            Block block = Block.getBlockByIndex(blockIndex);
            String blockName = block.getName().toLowerCase();

            File fileSimple = new File(atlasDirectory, blockName + ".png");
            File fileSide = new File(atlasDirectory, blockName + "_side.png");
            File fileTopBottom = new File(atlasDirectory, blockName + "_top_bottom.png");
            File fileTop = new File(atlasDirectory, blockName + "_top.png");
            File fileBottom = new File(atlasDirectory, blockName + "_bottom.png");
            File fileLeft = new File(atlasDirectory, blockName + "_left.png");
            File fileRight = new File(atlasDirectory, blockName + "_right.png");
            File fileFront = new File(atlasDirectory, blockName + "_front.png");
            File fileBack = new File(atlasDirectory, blockName + "_back.png");

            if (blocksAtlasDecoder.isTextureExists(fileSimple)) {
                sideTop = fileSimple;
                sideBottom = fileSimple;
                sideLeft = fileSimple;
                sideRight = fileSimple;
                sideFront = fileSimple;
                sideBack = fileSimple;
            }

            if (blocksAtlasDecoder.isTextureExists(fileSide)) {
                sideLeft = fileSide;
                sideRight = fileSide;
                sideFront = fileSide;
                sideBack = fileSide;
            }

            if (blocksAtlasDecoder.isTextureExists(fileTopBottom)) {
                sideTop = fileTopBottom;
                sideBottom = fileTopBottom;
            }

            if (blocksAtlasDecoder.isTextureExists(fileTop)) {
                sideTop = fileTop;
            }

            if (blocksAtlasDecoder.isTextureExists(fileBottom)) {
                sideBottom = fileBottom;
            }

            if (blocksAtlasDecoder.isTextureExists(fileLeft)) {
                sideLeft = fileLeft;
            }

            if (blocksAtlasDecoder.isTextureExists(fileRight)) {
                sideRight = fileRight;
            }

            if (blocksAtlasDecoder.isTextureExists(fileFront)) {
                sideFront = fileFront;
            }

            if (blocksAtlasDecoder.isTextureExists(fileBack)) {
                sideBack = fileBack;
            }

            if (sideTop == null || sideBottom == null || sideLeft == null || sideRight == null || sideFront == null || sideBack == null) {
                String msg = String.format("Some textures are missing for block: %s", blockName);
                throw new IllegalArgumentException(msg);
            }

            blocksTextures[blockIndex] = new TextureCubeMap(
                    new Texture(blocksAtlasDecoder),
                    blocksAtlasDecoder.getFileToAtlasFragment().get(sideTop),
                    blocksAtlasDecoder.getFileToAtlasFragment().get(sideBottom),
                    blocksAtlasDecoder.getFileToAtlasFragment().get(sideLeft),
                    blocksAtlasDecoder.getFileToAtlasFragment().get(sideRight),
                    blocksAtlasDecoder.getFileToAtlasFragment().get(sideFront),
                    blocksAtlasDecoder.getFileToAtlasFragment().get(sideBack)
            );

        }

    }

    @RequiredArgsConstructor
    @Getter
    public static class TextureCubeMap {

        private final Texture atlasTexture;
        private final TextureRepository.AtlasFragment sideTop;
        private final TextureRepository.AtlasFragment sideBottom;
        private final TextureRepository.AtlasFragment sideLeft;
        private final TextureRepository.AtlasFragment sideRight;
        private final TextureRepository.AtlasFragment sideFront;
        private final TextureRepository.AtlasFragment sideBack;

    }

}
