package com.ternsip.glade.universe.entities.base;

import com.ternsip.glade.graphics.visual.base.Effigy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3f;

import java.io.Serializable;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Getter
public class EntityGeneric extends GraphicalEntity {

    private final EffigySupplier effigySupplier;
    private final Vector3f rotationSpeed;

    public EntityGeneric() {
        effigySupplier = null;
        rotationSpeed = null;
    }

    public EntityGeneric(EffigySupplier effigySupplier) {
        this.effigySupplier = effigySupplier;
        this.rotationSpeed = new Vector3f(0);
    }

    @Override
    public Effigy getEffigy() {
        return effigySupplier.get();
    }

    @Override
    public void update() {
        super.update();
        setRotation(getRotation().add(getRotationSpeed(), new Vector3f()));
    }

    @FunctionalInterface
    public interface EffigySupplier extends Supplier<Effigy>, Serializable {
    }

}
