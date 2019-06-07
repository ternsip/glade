package com.ternsip.glade.universe.graphicals.impl;

import com.ternsip.glade.universe.graphicals.base.Visual;
import lombok.RequiredArgsConstructor;
import org.joml.Vector2i;
import org.joml.Vector4f;

import java.io.File;

@RequiredArgsConstructor
public class GraphicalDynamicText implements Visual {

    private final File font;

    private Graphical2DText fpsText;
    private String lastText;

    public void changeText(String text) {
        if (fpsText != null) {
            fpsText.finish();
        }
        if (text.equals(lastText)) {
            return;
        }
        fpsText = new Graphical2DText(
                font,
                text,
                new Vector2i(0, 0),
                new Vector2i(75, 75),
                new Vector4f(0, 1, 1, 1)
        );
        lastText = text;
    }

    @Override
    public void finish() {
        if (fpsText != null) {
            fpsText.finish();
        }
    }

}
