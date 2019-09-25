package com.ternsip.glade.universe.entities.base;

import lombok.Getter;

import java.util.UUID;

/**
 * Class should be thread safe
 */
@Getter
public abstract class EntityBase implements Transferable {

    private final UUID uuid = UUID.randomUUID();

    public abstract void register();

    public abstract void unregister();

    public abstract void update();

}
