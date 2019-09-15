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
// TODO SHADOWING/LIGHTING
// TODO Animated Textures
// TODO It is worth it to use polar interpolation instead of linear for animated models
// TODO turn all  .collect(Collectors.toMap to hashmap creation
// TODO handle network block sides inconsistency (like non-existent sides or packet loss)
// TODO Add block placing on cursor
// TODO Add trees and structures and wand for selecting the area
// TODO Add fog and underwater fog effect
// TODO Add ambient sounds for underwater, forest, sky,
// TODO Add Particle effects
// TODO Sort effigies in shader type order in order to reduce bandwidth in CPU-GPU shader rebinding
// TODO Add camera collision with collidable
// TODO ?? Add Combine collidable and block traversal (name blocksRepository collidable)
// TODO Better collisions with playaer (push out from shape AABB)
// TODO Player inventory based on UI
// TODO Item repository (blocks, tools)
// TODO Add Free look camera
// TODO handle client-server shutdown without errors
// TODO Add multiplayer ability (right now its only one player)
// TODO add log messages to important processes
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
