package com.ternsip.glade.universe.interfaces;

import com.ternsip.glade.common.logic.LazyWrapper;
import com.ternsip.glade.universe.entities.repository.EntityClientRepository;

public interface IEntityClientRepository {

    LazyWrapper<EntityClientRepository> ENTITY_CLIENT_REPOSITORY = new LazyWrapper<>(EntityClientRepository::new);

    default EntityClientRepository getEntityClientRepository() {
        return ENTITY_CLIENT_REPOSITORY.getObjective();
    }

}
