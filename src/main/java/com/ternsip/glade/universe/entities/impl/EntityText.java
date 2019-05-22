package com.ternsip.glade.universe.entities.impl;


import com.ternsip.glade.graphics.general.*;
import com.ternsip.glade.universe.entities.base.Entity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.File;

@RequiredArgsConstructor
@Getter
public class EntityText extends Entity {

    private static final float DEFAULT_SCALE = 1 / 75f;
    private final File font;
    private final String text;

    protected Model loadModel() {
        Mesh mesh = Entity3DText.createTextMesh(text, new Material(new Texture(new Vector4f(0, 0, 1, 1), font)));
        Vector3f scale = new Vector3f(text.length() * DEFAULT_SCALE, 1 * DEFAULT_SCALE, 1);
        return new Model(new Mesh[]{mesh}, new Animation(), new Vector3f(0), new Vector3f(0), scale);
    }

    @Override
    protected boolean isModelUnique() {
        return true;
    }

    @Override
    public boolean isSprite() {
        return true;
    }

    @Override
    public boolean isFrontal() {
        return true;
    }

}
