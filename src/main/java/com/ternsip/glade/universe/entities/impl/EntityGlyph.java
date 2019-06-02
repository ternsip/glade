package com.ternsip.glade.universe.entities.impl;


import com.ternsip.glade.graphics.general.Material;
import com.ternsip.glade.graphics.general.Mesh;
import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.general.Texture;
import com.ternsip.glade.universe.entities.base.Entity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.File;

@RequiredArgsConstructor
@Getter
public class EntityGlyph extends Entity {

    private final File font;
    private final char symbol;
    private final Vector4f color;

    protected Model loadModel() {
        Mesh mesh = Entity3DText.createTextMesh(String.valueOf(symbol), new Material(new Texture(color, font)));
        return new Model(new Mesh[]{mesh}, new Vector3f(0), new Vector3f(0), new Vector3f(1));
    }

    @Override
    public boolean isSprite() {
        return true;
    }

    @Override
    public boolean isFrontal() {
        return true;
    }

    @Override
    public Object getModelKey() {
        return new GlyphKey(getFont(), getSymbol(), getColor());
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode
    private static class GlyphKey {
        private final File font;
        private final char symbol;
        private final Vector4f color;
    }

}
