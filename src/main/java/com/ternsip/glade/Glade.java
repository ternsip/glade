package com.ternsip.glade;

import com.ternsip.glade.graphics.display.Graphical;
import com.ternsip.glade.graphics.display.Graphics;
import com.ternsip.glade.universe.Universe;
import com.ternsip.glade.universe.common.Universal;

/**
 * The main entry point of the application
 * Initializes graphical, network and logic thread
 * Graphical thread should always be main by multi-platform purposes
 * <p>
 * In case you have GPU-dump crashes:
 * - checkout memory buffers (for instance that all of them rewind() after reading)
 * - try to avoid memory buffers if possible
 * - check memory buffers' explicit free calls
 * - check data that you send to GPU i.e. number of vertices/textures/indices/colors etc.
 *
 * @author Ternsip
 */
// TODO TURN ALL Vectors and Matrixes and Quaternions to constant interface (quatenrionfc/matrixfc)]
// TODO SHADOWING/LIGHTING
// TODO PHYSICAL COLLISIONS
// TODO Animated Textures
public class Glade {

    public static void main(String[] args) {

        new Thread(() -> {
            Universe universe = Universal.UNIVERSE;
            universe.initialize();
            universe.loop();
            universe.finish();
        }).start();

        Graphics graphics = Graphical.GRAPHICS;
        graphics.loop();
        graphics.finish();

    }

}
