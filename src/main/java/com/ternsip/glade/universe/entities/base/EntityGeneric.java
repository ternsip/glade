package com.ternsip.glade.universe.entities.base;

import com.ternsip.glade.graphics.visual.base.Effigy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3f;

import java.io.ObjectInputStream;
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

    @Override
    public void readFromStream(ObjectInputStream ois) throws Exception {
        float px = ois.readFloat();
        float py = ois.readFloat();
        float pz = ois.readFloat();
        float rx = ois.readFloat();
        float ry = ois.readFloat();
        float rz = ois.readFloat();
        float sx = ois.readFloat();
        float sy = ois.readFloat();
        float sz = ois.readFloat();
        boolean visible = ois.readBoolean();
        float skyIntensity = ois.readFloat();
        float emitIntensity = ois.readFloat();
        getVolumetricInterpolated().update(
                px, py, pz,
                getRotation().x(), getRotation().y(), getRotation().z(),
                sx, sy, sz,
                visible,
                skyIntensity,
                emitIntensity
        );
    }

}
