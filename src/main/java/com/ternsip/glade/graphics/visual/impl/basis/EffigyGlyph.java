package com.ternsip.glade.graphics.visual.impl.basis;


import com.ternsip.glade.graphics.general.Material;
import com.ternsip.glade.graphics.general.Mesh;
import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.general.Texture;
import com.ternsip.glade.graphics.visual.base.EffigyAnimated;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4fc;

import java.io.File;
import java.util.Collections;

@RequiredArgsConstructor
@Getter
public class EffigyGlyph extends EffigyAnimated {

    private static final Matrix4fc EMPTY_MATRIX = new Matrix4f();
    private final File font;
    private final char symbol;
    private final Vector4fc color;

    public Model loadModel() {
        Mesh mesh = Effigy3DText.createTextMesh(String.valueOf(symbol), new Material(new Texture(color, font)));
        return new Model(Collections.singletonList(mesh), new Vector3f(0), new Vector3f(0), new Vector3f(1));
    }

    @Override
    public boolean isGraphicalInsideFrustum() {
        return true;
    }

    @Override
    public Object getModelKey() {
        return new GlyphKey(getFont(), getSymbol(), getColor());
    }

    @Override
    protected Matrix4fc getViewMatrix() {
        return EMPTY_MATRIX;
    }

    @Override
    protected Matrix4fc getProjectionMatrix() {
        return getGraphics().getCamera().getOrthoProjectionMatrix();
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode
    private static class GlyphKey {
        private final File font;
        private final char symbol;
        private final Vector4fc color;
    }

}
