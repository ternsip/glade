package com.ternsip.glade.graphics.interfaces;

import com.ternsip.glade.common.logic.LazyWrapper;
import com.ternsip.glade.graphics.visual.repository.ShaderRepository;

public interface IShaderRepository {

    LazyWrapper<ShaderRepository> SHADER_REPOSITORY = new LazyWrapper<>(ShaderRepository::new);

    default ShaderRepository getShaderRepository() {
        return SHADER_REPOSITORY.getObjective();
    }

}
