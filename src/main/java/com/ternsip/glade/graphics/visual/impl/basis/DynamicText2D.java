package com.ternsip.glade.graphics.visual.impl.basis;

import com.ternsip.glade.graphics.visual.base.Visual;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.joml.Vector4fc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
@Setter
public class DynamicText2D extends DynamicVisual {

    private final File font;

    private String text;
    private Vector2ic pos;
    private Vector2ic maxChars;
    private Vector4fc color;

    public void changeText(String text, Vector2ic pos, Vector2ic maxChars, Vector4fc color) {
        if (text.equals(getText()) && pos.equals(getPos()) && maxChars.equals(getMaxChars()) && color.equals(getColor())) {
            return;
        }
        setText(text);
        setPos(pos);
        setMaxChars(maxChars);
        setColor(color);
        reload();
    }

    @Override
    public List<Visual> createVisuals() {
        int number = 0;
        List<Visual> visuals = new ArrayList<>();
        for (String token : getText().split(System.lineSeparator())) {
            visuals.add(new Visual2DText(getFont(), token, new Vector2i(getPos().x(), getPos().y() + number), getMaxChars(), getColor()));
            ++number;
        }
        return visuals;
    }

}
