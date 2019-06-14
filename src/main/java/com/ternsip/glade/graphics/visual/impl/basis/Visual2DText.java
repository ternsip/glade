package com.ternsip.glade.graphics.visual.impl.basis;


import com.ternsip.glade.graphics.visual.base.Effigy;
import com.ternsip.glade.graphics.visual.base.MultiVisual;
import lombok.Getter;
import org.joml.Vector2ic;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4fc;

import java.io.File;

@Getter
public class Visual2DText extends MultiVisual {

    private static final float TEXT_COMPRESSION = 0.8f;

    public Visual2DText(File font, String text, Vector3fc position, Vector3fc scale, Vector4fc color) {
        super(generateEntities(font, text, position, scale, color));
    }

    public Visual2DText(File font, String text, Vector2ic position, Vector2ic maxChars, Vector4fc color) {
        super(generateEntities(font, text, position, maxChars, color));
    }

    private static Effigy[] generateEntities(
            File font,
            String text,
            Vector3fc position,
            Vector3fc scale,
            Vector4fc color
    ) {
        Effigy[] entities = new Effigy[text.length()];
        for (int i = 0; i < text.length(); ++i) {
            entities[i] = new EffigyGlyph(font, text.charAt(i), color);
            entities[i].setPosition(new Vector3f(i * scale.x() * TEXT_COMPRESSION, 0, 0).add(position));
            entities[i].setScale(scale);
        }
        return entities;
    }

    private static Effigy[] generateEntities(
            File font,
            String text,
            Vector2ic position,
            Vector2ic maxChars,
            Vector4fc color
    ) {
        Vector3f scale3 = new Vector3f(2f / maxChars.x(), 2f / maxChars.y(), 1);
        Vector3f pos3 = new Vector3f(
                -1f + scale3.x() + position.x() * scale3.x(),
                -(-1f + scale3.y() + position.y() * scale3.y()),
                0
        );
        return generateEntities(font, text, pos3, scale3, color);
    }

}
