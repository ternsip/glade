package com.ternsip.glade;

import com.ternsip.glade.entity.Camera;
import com.ternsip.glade.entity.Rover;
import com.ternsip.glade.entity.Sun;
import com.ternsip.glade.renderer.MasterRenderer;
import com.ternsip.glade.universal.AssimpLoader;
import com.ternsip.glade.universal.Model;
import com.ternsip.glade.universal.Settings;
import com.ternsip.glade.utils.DisplayManager;
import lombok.SneakyThrows;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.File;

// BE CAREFUL BUFFER FLIPS
// TODO CHECKOUT BUFFERS (FLOATBUFFER ETC.) BECAUSE THEY ARE BUGGED
// TODO TURN ALL Vectors and Matrixes and Quaternions to constant interface (quatenrionfc/matrixfc)
// TODO move sky on good shader
// TODO remove toRadians etc
public class Glade {

    public static final DisplayManager DISPLAY_MANAGER = new DisplayManager();


    @SneakyThrows
    public static void main(String[] args) {

        DISPLAY_MANAGER.createDisplay();

        Sun sun = new Sun(new Vector2f(0, 0), new Vector2f(20000, 20000), new Vector3f(1, 1, 1));

        Model boyModel = AssimpLoader.loadModel(Settings.builder().meshFile(new File("models/boy/boy.dae")).build());
        Rover rover = new Rover(boyModel);
        Camera camera = new Camera(rover);

        MasterRenderer renderer = new MasterRenderer(camera);
        renderer.processEntity(rover);
        renderer.prepareTestScene();

        // TODO Check performance with runnable and without it
        DISPLAY_MANAGER.loop(() -> {
            rover.move();
            camera.move();
            sun.move();
            renderer.render(sun, camera);

        });

        renderer.cleanUp();
        DISPLAY_MANAGER.closeDisplay();

    }
}
