package com.ternsip.glade;

import com.google.common.collect.Sets;
import com.ternsip.glade.graphics.interfaces.IGraphics;
import com.ternsip.glade.universe.interfaces.IUniverseServer;

import java.util.Set;

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
 * - in case you want to debug errors - use debug mode
 * - be careful with @Delegate sometimes it breaks navigation or debugger, also recursive delegate does not allowed
 *
 * @author Ternsip
 */
// TODO TURN ALL Vectors and Matrixes and Quaternions to constant interface (quatenrionfc/matrixfc)]
// TODO SHADOWING/LIGHTING, Shadow mapping using fbo
// TODO Animated Textures
// TODO It is worth it to use polar interpolation instead of linear for animated models
// TODO turn all  .collect(Collectors.toMap to hashmap creation
// TODO handle network block sides inconsistency (like non-existent sides or packet loss)
// TODO Add block placing on cursor
// TODO Add ambient sounds for underwater, forest, sky,
// TODO Add Particle effects
// TODO Make camera collisions better (more precise)
// TODO ?? Add Combine collidable and block traversal (name blocksRepository collidable)
// TODO Better collisions with playaer (push out from shape AABB)
// TODO Extend Player inventory UI capabilities
// TODO Add Free look camera
// TODO add log messages to important processes
// TODO add packet receive limitation (implement inside network server)
// TODO make update dynamic (for better performance - do not call superfluous updates)
// TODO http://jbullet.advel.cz/
// TODO i've spotted that some gl-gen-buffers not finished after execution
// TODO produce exception when load crashed data from world-file
// TODO prevent saving malformed world
public class Glade {

    public static void main(String[] args) {
        Set<String> input = Sets.newHashSet(args);
        if (input.contains("--server")) {
            IUniverseServer.run();
        } else {
            IGraphics.run();
        }
    }

}
