package com.ternsip.glade.graphics.interfaces;

import com.ternsip.glade.common.logic.LazyWrapper;
import com.ternsip.glade.graphics.visual.repository.TextureRepository;

public interface ITextureRepository {

    LazyWrapper<TextureRepository> TEXTURE_REPOSITORY = new LazyWrapper<>(TextureRepository::new);

    default TextureRepository getTextureRepository() {
        return TEXTURE_REPOSITORY.getObjective();
    }

}
