package com.ternsip.glade.universe.interfaces;

import com.ternsip.glade.common.logic.LazyWrapper;
import com.ternsip.glade.universe.entities.repository.EntityServerRepository;

public interface IEntityServerRepository {

    LazyWrapper<EntityServerRepository> ENTITY_SERVER_REPOSITORY = new LazyWrapper<>(EntityServerRepository::new);

    default EntityServerRepository getEntityServerRepository() {
        return ENTITY_SERVER_REPOSITORY.getObjective();
    }

}
