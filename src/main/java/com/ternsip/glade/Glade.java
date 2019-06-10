package com.ternsip.glade;

import com.ternsip.glade.graphics.display.DisplayManager;
import com.ternsip.glade.universe.Universe;

// TODO TURN ALL Vectors and Matrixes and Quaternions to constant interface (quatenrionfc/matrixfc)]
// TODO SHADOWING ?
// TODO PHYSICAL COLLISIONS
// TODO read about MemoryStack for optimising buffer allocation
// TODO LookAt bug (collinear)
// TODO Animated Textures
// TODO Author rights
// TODO Multithreading (1 logic, 1 network, 1 graphical (including input))
public class Glade {

    public static final DisplayManager DISPLAY_MANAGER = new DisplayManager();
    public static final Universe UNIVERSE = new Universe();

    public static void main(String[] args) {

        new Thread(() -> {
            UNIVERSE.initialize();
            UNIVERSE.loop();
        }).start();

        DISPLAY_MANAGER.initialize();
        DISPLAY_MANAGER.loop();
        DISPLAY_MANAGER.finish();

    }
}
