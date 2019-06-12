package com.ternsip.glade;

import com.ternsip.glade.graphics.display.Graphical;
import com.ternsip.glade.graphics.display.Graphics;
import com.ternsip.glade.universe.Universe;
import com.ternsip.glade.universe.common.Universal;

// In case you have weird crashes checkout memory buffers (for instance that all of them rewind() after reading), try to avoid memory buffers
// TODO TURN ALL Vectors and Matrixes and Quaternions to constant interface (quatenrionfc/matrixfc)]
// TODO SHADOWING ?
// TODO PHYSICAL COLLISIONS
// TODO read about MemoryStack for optimising buffer allocation
// TODO LookAt bug (collinear)
// TODO Animated Textures
// TODO Author rights
// TODO Multithreading (1 logic, 1 network, 1 graphical (including input))
public class Glade {

    public static void main(String[] args) {

        new Thread(() -> {
            Universe universe = Universal.UNIVERSE;
            universe.spawnTestEntities();
            universe.loop();
            universe.finish();
        }).start();

        Graphics graphics = Graphical.GRAPHICS;
        graphics.loop();
        graphics.finish();

    }

}
