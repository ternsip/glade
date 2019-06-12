package com.ternsip.glade.graphics.display;

public interface Graphical {

    Graphics GRAPHICS = new Graphics();

    default Graphics getGraphics() {
        GRAPHICS.checkThreadSafety();
        return GRAPHICS;
    }

}
