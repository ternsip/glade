package com.ternsip.glade.universe.entities.base;

import com.ternsip.glade.graphics.display.Graphics;
import com.ternsip.glade.universe.common.Universal;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;

@Getter
public abstract class MultiEntity implements Universal {

    public final Collection<Entity> entities = new ArrayList<>();

    public MultiEntity() {
        getUniverse().getEntityRepository().register(this);
    }

    public void reload() {
        getEntities().forEach(Entity::finish);
        getEntities().clear();
        getEntities().addAll(loadEntities());
    }

    public void update() {
    }

    public void graphicalUpdate(Graphics graphics) {
    }

    public abstract Collection<Entity> loadEntities();

    public void finish() {
        getUniverse().getEntityRepository().unregister(this);
        getEntities().forEach(Entity::finish);
    }

}
