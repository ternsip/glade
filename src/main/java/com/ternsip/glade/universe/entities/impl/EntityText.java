package com.ternsip.glade.universe.entities.impl;


import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.entities.base.MultiEntity;
import lombok.Getter;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.File;

@Getter
public class EntityText extends MultiEntity {

    public EntityText(File font, String text, Vector3f position, Vector3f scale, Vector4f color) {
        super(generateEntities(font, text, position, scale, color));
    }

    private static Entity[] generateEntities(
            File font,
            String text,
            Vector3f position,
            Vector3f scale,
            Vector4f color
    ) {
        Entity[] entities = new Entity[text.length()];
        for (int i = 0; i < text.length(); ++i) {
            entities[i] = new EntityGlyph(font, text.charAt(i), color);
            entities[i].setPosition(new Vector3f(i * EntityGlyph.DEFAULT_SCALE * scale.x(), 0, 0).add(position));
            entities[i].setScale(scale);
        }
        return entities;
    }

}
