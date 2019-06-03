package com.ternsip.glade.universe.entities.impl;


import com.ternsip.glade.universe.entities.base.Graphical;
import com.ternsip.glade.universe.entities.base.MultiGraphical;
import lombok.Getter;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.File;

@Getter
public class GraphicalText extends MultiGraphical {

    private static final float TEXT_COMPRESSION = 0.75f;

    public GraphicalText(File font, String text, Vector3f position, Vector3f scale, Vector4f color) {
        super(generateEntities(font, text, position, scale, color));
    }

    public GraphicalText(File font, String text, Vector2i position, Vector2i maxChars, Vector4f color) {
        super(generateEntities(font, text, position, maxChars, color));
    }

    private static Graphical[] generateEntities(
            File font,
            String text,
            Vector2i position,
            Vector2i maxChars,
            Vector4f color
    ) {
        Vector3f scale3 = new Vector3f(2f / maxChars.x(), 2f / maxChars.y(), 1);
        Vector3f pos3 = new Vector3f(
                -1f + scale3.x() + position.x() * scale3.x(),
                -(-1f + scale3.y() + position.y() * scale3.y()),
                0
        );
        return generateEntities(font, text, pos3, scale3, color);
    }

    private static Graphical[] generateEntities(
            File font,
            String text,
            Vector3f position,
            Vector3f scale,
            Vector4f color
    ) {
        Graphical[] entities = new Graphical[text.length()];
        for (int i = 0; i < text.length(); ++i) {
            entities[i] = new GraphicalGlyph(font, text.charAt(i), color);
            entities[i].setPosition(new Vector3f(i * scale.x() * TEXT_COMPRESSION, 0, 0).add(position));
            entities[i].setScale(scale);
        }
        return entities;
    }

}
