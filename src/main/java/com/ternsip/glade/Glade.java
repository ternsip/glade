package com.ternsip.glade;

import com.ternsip.glade.entity.*;
import com.ternsip.glade.model.GLModel;
import com.ternsip.glade.model.parser.Model;
import com.ternsip.glade.model.parser.ModelObject;
import com.ternsip.glade.model.parser.Parser;
import com.ternsip.glade.renderer.MasterRenderer;
import com.ternsip.glade.utils.DisplayManager;
import lombok.SneakyThrows;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.File;

import static com.ternsip.glade.model.GLModel.SKIP_TEXTURE;
import static com.ternsip.glade.utils.Maths.PI;

// BE CAREFUL BUFFER FLIPS
public class Glade {

    public static final DisplayManager DISPLAY_MANAGER = new DisplayManager();

    @SneakyThrows
    public static void main(String[] args) {

        DISPLAY_MANAGER.createDisplay();

        Sun sun = new Sun(new Vector2f(0, 0), new Vector2f(20000, 20000), new Vector3f(1, 1, 1));

        MasterRenderer renderer = new MasterRenderer();

        GLModel roverModel = ResourceLoader.loadObjModel(new File("models/rover/rover.obj"), new File("models/rover/rover.png"));
        Rover rover = new Rover(roverModel, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));

        Camera camera = new Camera(rover);
        Model ship = Parser.load3dModel(new File("models/ship/ship.3ds"), new File("models/ship/ship.png"));

        GLModel cubeModel = Cube.generateGLModel();
        Entity cube = new Entity(cubeModel, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));

        GLModel houseModel = ResourceLoader.loadObjModel(new File("models/house/house.obj"), SKIP_TEXTURE);
        Entity house = new Entity(houseModel, new Vector3f(-20, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));

        GLModel dudeModel = ResourceLoader.loadObjModel(new File("models/dude/dude.obj"), new File("models/dude/dude.png"));
        Entity dude = new Entity(dudeModel, new Vector3f(-20, 0, -20), new Vector3f(0, 0, 0), new Vector3f(10, 10, 10));

        renderer.processEntity(rover);
        renderer.processEntity(cube);
        for (ModelObject o : ship.objects) {
            renderer.processEntity(new Entity(o.getGLModel(), new Vector3f(20, 0, 0), new Vector3f(0, 0, -4f*PI), new Vector3f(0.25f, 0.25f, 0.25f)));
        }
        renderer.processEntity(house);
        renderer.processEntity(dude);


        // TODO Check performance with runnable and without it
        DISPLAY_MANAGER.loop(() -> {
            rover.move();
            camera.move();
            sun.move();
            renderer.render(sun, camera);
            //ship.render(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 10);
        });

        renderer.cleanUp();
        DISPLAY_MANAGER.closeDisplay();

    }
}
