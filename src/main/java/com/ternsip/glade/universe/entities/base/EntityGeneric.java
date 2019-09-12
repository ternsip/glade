package com.ternsip.glade.universe.entities.base;

import com.ternsip.glade.graphics.visual.base.Effigy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Getter
public class EntityGeneric extends Entity {

    private final EffigySupplier effigySupplier;

    public EntityGeneric() {
        this.effigySupplier = null;
    }

    @Override
    public Effigy getEffigy() {
        return effigySupplier.get();
    }

    @FunctionalInterface
    public interface EffigySupplier extends Supplier<Effigy>, Serializable {
    }

}
