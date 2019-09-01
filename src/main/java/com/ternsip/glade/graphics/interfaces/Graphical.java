package com.ternsip.glade.graphics.interfaces;

import com.ternsip.glade.graphics.display.Graphics;

public interface Graphical {

    Graphics GRAPHICS = new Graphics();

    static void run() {
        GRAPHICS.run();
    }

    default Graphics getGraphics() {
        GRAPHICS.checkThreadSafety();
        return GRAPHICS;
    }

}
