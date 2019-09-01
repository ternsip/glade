package com.ternsip.glade.graphics.interfaces;

import com.ternsip.glade.common.logic.LazyWrapper;
import com.ternsip.glade.graphics.visual.repository.ModelRepository;

public interface IModelRepository {

    LazyWrapper<ModelRepository> MODEL_REPOSITORY = new LazyWrapper<>(ModelRepository::new);

    default ModelRepository getModelRepository() {
        return MODEL_REPOSITORY.getObjective();
    }

}
