package com.ternsip.glade;

import com.ternsip.glade.graphics.renderer.base.MasterRenderer;
import com.ternsip.glade.universe.Universe;
import com.ternsip.glade.utils.DisplayManager;

// TODO TURN ALL Vectors and Matrixes and Quaternions to constant interface (quatenrionfc/matrixfc)]
// TODO Implement LOD
public class Glade {

    public static final DisplayManager DISPLAY_MANAGER = new DisplayManager();
    public static final Universe UNIVERSE = new Universe();
    public static final MasterRenderer MASTER_RENDERER = new MasterRenderer();

    public static void main(String[] args) {

        DISPLAY_MANAGER.initialize();
        UNIVERSE.initialize();
        MASTER_RENDERER.initialize();

        // TODO Check performance with runnable and without it
        DISPLAY_MANAGER.loop(() -> {
            UNIVERSE.update();
            MASTER_RENDERER.render();

        });

        UNIVERSE.finish();
        MASTER_RENDERER.finish();
        DISPLAY_MANAGER.finish();

    }
}
