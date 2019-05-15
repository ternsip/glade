package com.ternsip.glade;

import com.ternsip.glade.entity.Camera;
import com.ternsip.glade.entity.Player;
import com.ternsip.glade.entity.Sun;
import com.ternsip.glade.renderer.MasterRenderer;
import com.ternsip.glade.utils.DisplayManager;
import org.joml.Vector2f;
import org.joml.Vector3f;

// TODO TURN ALL Vectors and Matrixes and Quaternions to constant interface (quatenrionfc/matrixfc)]
// TODO handle resize normally
public class Glade {

    public static final DisplayManager DISPLAY_MANAGER = new DisplayManager();

    public static void main(String[] args) {

        DISPLAY_MANAGER.createDisplay();

        Sun sun = new Sun(new Vector2f(0, 0), new Vector2f(20000, 20000), new Vector3f(1, 1, 1));

        Player player = new Player();
        Camera camera = new Camera(player);

        MasterRenderer renderer = new MasterRenderer(camera);
        renderer.processEntity(player);
        renderer.prepareTestScene();

        // TODO Check performance with runnable and without it
        DISPLAY_MANAGER.loop(() -> {
            player.move();
            camera.move();
            sun.move();
            renderer.render(sun, camera);

        });

        renderer.cleanUp();
        DISPLAY_MANAGER.closeDisplay();

    }
}
