package com.ternsip.glade.universe.graphicals.impl;

import com.ternsip.glade.universe.graphicals.base.Visual;
import lombok.RequiredArgsConstructor;
import org.joml.Vector2i;
import org.joml.Vector4f;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class GraphicalDynamicText implements Visual {

    private final File font;

    private List<Visual2DText> lastVisualTexts = new ArrayList<>();
    private String lastText;

    public void changeText(String text, Vector2i pos, Vector2i maxChars, Vector4f color) {
        if (text.equals(lastText)) {
            return;
        }
        finish();
        lastVisualTexts.clear();
        int number = 0;
        for (String token : text.split(System.lineSeparator())) {
            lastVisualTexts.add(new Visual2DText(font, token, new Vector2i(pos.x(), pos.y() + number), maxChars, color));
            ++number;
        }
        lastText = text;
    }

    @Override
    public void finish() {
        for (Visual2DText visual2DText : lastVisualTexts) {
            visual2DText.finish();
        }
    }

}
