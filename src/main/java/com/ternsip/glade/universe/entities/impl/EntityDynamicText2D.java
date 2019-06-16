package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.impl.basis.EffigyGlyph;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.entities.base.MultiEntity;
import lombok.Getter;
import lombok.Setter;
import org.joml.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class EntityDynamicText2D extends MultiEntity {

    private static final float TEXT_COMPRESSION = 0.8f;

    private File font = new File("fonts/default.png");
    private String text = "";
    private Vector2ic pos = new Vector2i(0);
    private Vector2ic maxChars = new Vector2i(1);
    private Vector4fc color = new Vector4f(0);
    private boolean visualReloadRequired = false;

    private static Collection<Entity> generateEntities(
            File font,
            String text,
            Vector3fc position,
            Vector3fc scale,
            Vector4fc color
    ) {
        Entity[] entities = new Entity[text.length()];
        for (int i = 0; i < text.length(); ++i) {
            int finalI = i;
            entities[i] = new EntityGeneric(() -> new EffigyGlyph(font, text.charAt(finalI), color));
            entities[i].setPosition(new Vector3f(i * scale.x() * TEXT_COMPRESSION, 0, 0).add(position));
            entities[i].setScale(scale);
            entities[i].register();
        }
        return Arrays.asList(entities);
    }

    private static Collection<Entity> generateEntities(
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

    public void changeText(String text, Vector2ic pos, Vector2ic maxChars, Vector4fc color) {
        if (text.equals(getText()) && pos.equals(getPos()) && maxChars.equals(getMaxChars()) && color.equals(getColor())) {
            return;
        }
        setText(text);
        setPos(pos);
        setMaxChars(maxChars);
        setColor(color);
        setVisualReloadRequired(true);
        reload();
    }

    @Override
    public Collection<Entity> loadEntities() {
        int number = 0;
        List<Entity> entities = new ArrayList<>();
        for (String token : getText().split(System.lineSeparator())) {
            entities.addAll(generateEntities(getFont(), token, new Vector2i(getPos().x(), getPos().y() + number), getMaxChars(), getColor()));
            ++number;
        }
        return entities;
    }
}
