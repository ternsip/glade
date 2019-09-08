package com.ternsip.glade.universe.entities.repository;

import com.ternsip.glade.universe.collisions.base.Obstacle;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.entities.impl.EntitySun;
import com.ternsip.glade.universe.protocol.EntitiesChangedClientPacket;
import com.ternsip.glade.universe.protocol.RegisterEntityPacket;
import com.ternsip.glade.universe.protocol.UnregisterEntityPacket;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntityServerRepository extends EntityRepository {

    private final FieldBuffer fieldBuffer = new FieldBuffer(true);

    private EntitySun sun = new EntitySun();

    public void register(Entity entity) {
        getUuidToEntity().put(entity.getUuid(), entity);
        if (entity instanceof Obstacle) {
            getUniverse().getCollisions().add((Obstacle) entity);
        }
        getUniverse().getServer().sendAll(new RegisterEntityPacket(entity));
    }

    public void unregister(Entity entity) {
        getUuidToEntity().remove(entity.getUuid());
        if (entity instanceof Obstacle) {
            getUniverse().getCollisions().remove((Obstacle) entity);
        }
        getUniverse().getServer().sendAll(new UnregisterEntityPacket(entity.getUuid()));
        getFieldBuffer().getUuidToFieldValues().remove(entity.getUuid());
    }

    public void update() {
        getUuidToEntity().values().forEach(Entity::serverUpdate);
        sendChanges();
    }

    public void sendChanges() {
        EntitiesChanges entitiesChanges = findEntitiesChanges();
        if (!entitiesChanges.isEmpty()) {
            getUniverse().getServer().sendAll(new EntitiesChangedClientPacket(entitiesChanges));
        }
    }

}
