package com.ternsip.glade.graphics.interfaces;

import com.ternsip.glade.common.logic.LazyWrapper;
import com.ternsip.glade.graphics.visual.repository.TexturePackRepository;

public interface ITexturePackRepository {

    LazyWrapper<TexturePackRepository> TEXTURE_PACK_REPOSITORY = new LazyWrapper<>(TexturePackRepository::new);

    default TexturePackRepository getTexturePackRepository() {
        return TEXTURE_PACK_REPOSITORY.getObjective();
    }

}
