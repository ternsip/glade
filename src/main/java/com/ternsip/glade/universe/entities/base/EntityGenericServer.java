package com.ternsip.glade.universe.entities.base;

import com.ternsip.glade.network.Connection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3f;

@RequiredArgsConstructor
@Getter
public class EntityGenericServer extends GraphicalEntityServer {

    private final EntityGeneric.EffigySupplier effigySupplier;
    private final Vector3f rotationSpeed;

    public EntityGenericServer(EntityGeneric.EffigySupplier effigySupplier) {
        this.effigySupplier = effigySupplier;
        this.rotationSpeed = new Vector3f(0);
    }

    @Override
    public EntityClient getEntityClient(Connection connection) {
        return new EntityGeneric(getEffigySupplier(), getRotationSpeed());
    }
}
