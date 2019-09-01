package com.ternsip.glade.graphics.visual.repository;

import com.ternsip.glade.graphics.interfaces.Graphical;
import com.ternsip.glade.graphics.general.Texture;
import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.blocks.BlockSide;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;

@Getter
public class TexturePackRepository implements Graphical {

    public static final File DEFAULT_BLOCK_ATLAS_DIRECTORY = new File("atlases/blocks");

    private final TextureCubeMap[] blocksTextures = new TextureCubeMap[Block.getSize()];

    public TexturePackRepository() {
        reloadBlocksTextureAtlas(DEFAULT_BLOCK_ATLAS_DIRECTORY);
    }

    public void reloadBlocksTextureAtlas(File atlasDirectory) {

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

            if (getGraphics().getTextureRepository().isTextureExists(fileSimple)) {
                sideTop = fileSimple;
                sideBottom = fileSimple;
                sideLeft = fileSimple;
                sideRight = fileSimple;
                sideFront = fileSimple;
                sideBack = fileSimple;
            }

            if (getGraphics().getTextureRepository().isTextureExists(fileSide)) {
                sideLeft = fileSide;
                sideRight = fileSide;
                sideFront = fileSide;
                sideBack = fileSide;
            }

            if (getGraphics().getTextureRepository().isTextureExists(fileTopBottom)) {
                sideTop = fileTopBottom;
                sideBottom = fileTopBottom;
            }

            if (getGraphics().getTextureRepository().isTextureExists(fileTop)) {
                sideTop = fileTop;
            }

            if (getGraphics().getTextureRepository().isTextureExists(fileBottom)) {
                sideBottom = fileBottom;
            }

            if (getGraphics().getTextureRepository().isTextureExists(fileLeft)) {
                sideLeft = fileLeft;
            }

            if (getGraphics().getTextureRepository().isTextureExists(fileRight)) {
                sideRight = fileRight;
            }

            if (getGraphics().getTextureRepository().isTextureExists(fileFront)) {
                sideFront = fileFront;
            }

            if (getGraphics().getTextureRepository().isTextureExists(fileBack)) {
                sideBack = fileBack;
            }

            if (sideTop == null || sideBottom == null || sideLeft == null || sideRight == null || sideFront == null || sideBack == null) {
                String msg = String.format("Some textures are missing for block: %s", blockName);
                throw new IllegalArgumentException(msg);
            }

            blocksTextures[blockIndex] = new TextureCubeMap(
                    new Texture(sideTop),
                    new Texture(sideBottom),
                    new Texture(sideLeft),
                    new Texture(sideRight),
                    new Texture(sideFront),
                    new Texture(sideBack)
            );

        }

    }

    public TextureCubeMap getCubeMap(Block block) {
        return getBlocksTextures()[block.getIndex()];
    }

    @RequiredArgsConstructor
    @Getter
    public static class TextureCubeMap {

        private final Texture sideTop;
        private final Texture sideBottom;
        private final Texture sideLeft;
        private final Texture sideRight;
        private final Texture sideFront;
        private final Texture sideBack;

        public Texture getTextureByBlockSide(BlockSide side) {
            if (side == BlockSide.TOP) {
                return sideTop;
            }
            if (side == BlockSide.BOTTOM) {
                return sideBottom;
            }
            if (side == BlockSide.LEFT) {
                return sideLeft;
            }
            if (side == BlockSide.RIGHT) {
                return sideRight;
            }
            if (side == BlockSide.FRONT) {
                return sideFront;
            }
            if (side == BlockSide.BACK) {
                return sideBack;
            }
            throw new IllegalArgumentException(String.format("Unknown block side %s", side));
        }

    }

}
