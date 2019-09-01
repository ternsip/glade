package com.ternsip.glade.universe.interfaces;

import com.ternsip.glade.common.logic.LazyWrapper;
import com.ternsip.glade.universe.entities.repository.EntityRepository;

public interface IEntityRepository {

    LazyWrapper<EntityRepository> ENTITY_REPOSITORY = new LazyWrapper<>(EntityRepository::new);

    default EntityRepository getEntityRepository() {
        return ENTITY_REPOSITORY.getObjective();
    }

}
