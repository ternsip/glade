package com.ternsip.glade;

import com.ternsip.glade.graphics.interfaces.IGraphics;

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
// TODO Animated Textures
// TODO It is worth it to use polar interpolation instead of linear for animated models
// TODO turn all  .collect(Collectors.toMap to hashmap creation
public class Glade {

    public static void main(String[] args) {
        IGraphics.run();
    }

}
