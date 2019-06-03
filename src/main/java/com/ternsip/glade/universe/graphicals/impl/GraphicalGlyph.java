package com.ternsip.glade.universe.graphicals.impl;


import com.ternsip.glade.graphics.general.Material;
import com.ternsip.glade.graphics.general.Mesh;
import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.general.Texture;
import com.ternsip.glade.universe.graphicals.base.GraphicalDefault;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.File;

import static com.ternsip.glade.Glade.UNIVERSE;

@RequiredArgsConstructor
@Getter
public class GraphicalGlyph extends GraphicalDefault {

    private final Matrix4fc viewMatrix = new Matrix4f();
    private final File font;
    private final char symbol;
    private final Vector4f color;

    public Model loadModel() {
        Mesh mesh = Graphical3DText.createTextMesh(String.valueOf(symbol), new Material(new Texture(color, font)));
        return new Model(new Mesh[]{mesh}, new Vector3f(0), new Vector3f(0), new Vector3f(1));
    }

    @Override
    protected Matrix4fc getViewMatrix() {
        return viewMatrix;
    }

    @Override
    protected Matrix4fc getProjectionMatrix() {
        return UNIVERSE.getCamera().getSpriteProjectionMatrix();
    }

    @Override
    public int getPriority() {
        return 1000;
    }

    @Override
    public boolean isGraphicalInsideFrustum() {
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
