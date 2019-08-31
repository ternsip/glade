package com.ternsip.glade.graphics.display;

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
