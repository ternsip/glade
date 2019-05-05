package com.ternsip.glade;

import com.ternsip.glade.entity.Camera;
import com.ternsip.glade.entity.Cube;
import com.ternsip.glade.entity.Rover;
import com.ternsip.glade.entity.Sun;
import com.ternsip.glade.model.RawModel;
import com.ternsip.glade.model.TexturedModel;
import com.ternsip.glade.renderer.MasterRenderer;
import com.ternsip.glade.terrains.MultipleTerrain;
import com.ternsip.glade.terrains.Terrain;
import com.ternsip.glade.texture.ModelTexture;
import com.ternsip.glade.utils.DisplayManager;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.File;

// BE CAREFUL BUFFER FLIPS
public class MainGameLoop {
    public static void main(String[] args) {

        //SharedLibraryLoader.load();

        //System.setProperty("org.lwjgl.librarypath", new File("lib/native").getAbsolutePath());
        DisplayManager.createDisplay();
        Loader loader = new Loader();
        MultipleTerrain multipleTerrain = new MultipleTerrain(loader);

        Sun sun = new Sun(new Vector2f(0, 0), new Vector2f(20000, 20000), new Vector3f(1, 1, 1));

        MasterRenderer renderer = new MasterRenderer(loader);

        RawModel roverModel = OBJLoader.loadObjModel(new File("models/rover/rover.obj"), loader);

        TexturedModel roverTexturedModel = new TexturedModel(roverModel, new ModelTexture(Loader.loadTexturePNG(new File("models/rover/roverTexture.png"))));

        Rover rover = new Rover(roverTexturedModel, new Vector3f(4200, 60, 4200), 0, 0, 0, 1);

        Camera camera = new Camera(rover);

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

        // TODO Check performance with runnable and without it
        DisplayManager.loop(() -> {
            rover.move(multipleTerrain);
            multipleTerrain.checkTerrain(rover.getPosition());
            camera.move();

            renderer.processEntity(rover);

            for (Terrain terrain : multipleTerrain.getTerrains()) {
                renderer.processTerrain(terrain);
            }

            sun.move();
            renderer.render(sun, camera);
        });


        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();

    }
}
