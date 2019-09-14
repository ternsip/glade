package com.ternsip.glade.graphics.interfaces;

import com.ternsip.glade.graphics.display.Graphics;

public interface IGraphics {

    Graphics GRAPHICS = new Graphics();

    static void run() {
        GRAPHICS.run();
    }

    default Graphics getGraphics() {
        if (Thread.currentThread() != GRAPHICS.getRootThread()) {
            throw new IllegalArgumentException("It is not thread safe to get display not from the main thread");
        }
        return GRAPHICS;
    }

}
