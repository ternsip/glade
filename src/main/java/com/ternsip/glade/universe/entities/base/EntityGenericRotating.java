package com.ternsip.glade.universe.entities.base;

import lombok.Getter;
import org.joml.Vector3f;

@Getter
public class EntityGenericRotating extends EntityGeneric {

    private final Vector3f rotationSpeed;

    public EntityGenericRotating() {
        this.rotationSpeed = null;
    }

    public EntityGenericRotating(EffigySupplier effigySupplier, Vector3f rotationSpeed) {
        super(effigySupplier);
        this.rotationSpeed = rotationSpeed;
    }

    @Override
    public void serverUpdate() {
        super.serverUpdate();
        setRotation(getRotation().add(getRotationSpeed(), new Vector3f()));
    }

}
