package com.ternsip.glade.graphics.interfaces;

import com.ternsip.glade.common.logic.LazyWrapper;
import com.ternsip.glade.graphics.visual.repository.EffigyRepository;

public interface IEffigyRepository {

    LazyWrapper<EffigyRepository> EFFIGY_REPOSITORY = new LazyWrapper<>(EffigyRepository::new);

    default EffigyRepository getEffigyRepository() {
        return EFFIGY_REPOSITORY.getObjective();
    }

}
