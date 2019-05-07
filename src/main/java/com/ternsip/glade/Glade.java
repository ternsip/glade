package com.ternsip.glade;

import com.ternsip.glade.entity.*;
import com.ternsip.glade.model.RawModel;
import com.ternsip.glade.model.TexturedModel;
import com.ternsip.glade.model.parser.Model;
import com.ternsip.glade.model.parser.Parser;
import com.ternsip.glade.renderer.MasterRenderer;
import com.ternsip.glade.terrains.MultipleTerrain;
import com.ternsip.glade.terrains.Terrain;
import com.ternsip.glade.texture.ModelTexture;
import com.ternsip.glade.utils.DisplayManager;
import lombok.SneakyThrows;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.File;

// BE CAREFUL BUFFER FLIPS
public class Glade {

    public static final DisplayManager DISPLAY_MANAGER = new DisplayManager();

    @SneakyThrows
    public static void main(String[] args) {

        DISPLAY_MANAGER.createDisplay();
        Loader loader = new Loader();
        MultipleTerrain multipleTerrain = new MultipleTerrain(loader);

        Sun sun = new Sun(new Vector2f(0, 0), new Vector2f(20000, 20000), new Vector3f(1, 1, 1));

        MasterRenderer renderer = new MasterRenderer(loader);

        RawModel roverModel = OBJLoader.loadObjModel(new File("models/rover/rover.obj"), loader);

        TexturedModel roverTexturedModel = new TexturedModel(roverModel, new ModelTexture(Loader.loadTexturePNG(new File("models/dude/dude.png"))));

        // TODO SCALE = 10
        Rover rover = new Rover(roverTexturedModel, new Vector3f(0, 0, 0), 0, 0, 0, 1);

        Camera camera = new Camera(rover);
        Model ship = Parser.load3dModel(new File("models/ship/ship.3ds"), new File("models/ship/ship.png"));

        for (int i = 1; i < 5; i++) {
            float x = .0f;
            float z = .0f;

            if (i == 1)
                x = 2.0f;
            else if (i == 2)
                x = -2.0f;
            else if (i == 3)
                z = 2f;
            else
                z = -2f;

            TexturedModel cubeTexture = new TexturedModel(null, new ModelTexture(Loader.loadTexturePNG(new File("textures/cube" + i + ".png"))));
            Cube cube = new Cube(cubeTexture, rover.getPosition(), 0, 0, 0, 1, x, z, i);

            rover.addObserver(cube);
        }

        Entity entity = new Entity(VboCube.getRawModel(loader,  new File("models/others/stall.png")), new Vector3f(0, 0, 0), 0, 0, 0, 1);

        // TODO Check performance with runnable and without it
        DISPLAY_MANAGER.loop(() -> {
            rover.move(multipleTerrain);
            multipleTerrain.checkTerrain(rover.getPosition());
            camera.move();

            renderer.processEntity(rover);
            renderer.processEntity(entity);

            for (Terrain terrain : multipleTerrain.getTerrains()) {
                renderer.processTerrain(terrain);
            }

            sun.move();
            renderer.render(sun, camera);

            //ship.render(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 10);
        });


        renderer.cleanUp();
        loader.cleanUp();
        DISPLAY_MANAGER.closeDisplay();

    }
}
