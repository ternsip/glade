package com.ternsip.glade.universe.entities.base;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3f;

import javax.annotation.Nullable;

@RequiredArgsConstructor
@Getter
public class EntityGenericServer extends GraphicalEntityServer {

    private final EntityGeneric.EffigySupplier effigySupplier;
    private final Vector3f rotationSpeed;

    public EntityGenericServer(EntityGeneric.EffigySupplier effigySupplier) {
        this.effigySupplier = effigySupplier;
        this.rotationSpeed = new Vector3f(0);
    }

    @Nullable
    @Override
    protected EntityClient produceEntityClient() {
        return new EntityGeneric(getEffigySupplier(), getRotationSpeed());
    }
}
