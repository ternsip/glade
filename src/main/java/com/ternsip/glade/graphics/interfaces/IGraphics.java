package com.ternsip.glade.graphics.interfaces;

import com.ternsip.glade.graphics.display.Graphics;

public interface IGraphics {

    Thread MAIN_THREAD = Thread.currentThread();
    Graphics GRAPHICS = new Graphics();

    static void run() {
        GRAPHICS.run();
    }

    default Graphics getGraphics() {
        if (Thread.currentThread() != MAIN_THREAD) {
            throw new IllegalArgumentException("You should call graphics only from the main thread");
        }
        return GRAPHICS;
    }

}
