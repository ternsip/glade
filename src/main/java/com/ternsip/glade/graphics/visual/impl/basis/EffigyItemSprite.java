package com.ternsip.glade.graphics.visual.impl.basis;


import com.ternsip.glade.graphics.interfaces.IGraphics;
import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.blocks.BlockSide;
import lombok.Getter;

import java.io.File;

import static com.ternsip.glade.universe.parts.items.ItemEmpty.EMPTY_ITEM_KEY;

@Getter
public class EffigyItemSprite extends EffigySprite {

    private static final ItemMapper ITEM_MAPPER = new ItemMapper();

    public EffigyItemSprite(Object key, boolean ortho, boolean useAspect) {
        super(ITEM_MAPPER.keyToFile(key), ortho, useAspect);
    }

    private static class ItemMapper implements IGraphics {

        private static final File NO_ITEM_FILE = new File("interface/no_item.png");

        File keyToFile(Object itemKey) {
            if (itemKey == EMPTY_ITEM_KEY) {
                return NO_ITEM_FILE;
            }
            if (itemKey instanceof File) {
                return (File) itemKey;
            }
            if (itemKey instanceof Block) {
                Block block = (Block) itemKey;
                return getGraphics().getTexturePackRepository().getCubeMap(block).getTextureByBlockSide(BlockSide.FRONT).getFile();
            }
            throw new IllegalArgumentException(String.format("Item sprite %s can not be retrieved", itemKey));
        }

    }

}
