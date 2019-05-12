package com.ternsip.glade;

import com.ternsip.glade.entity.*;
import com.ternsip.glade.renderer.MasterRenderer;
import com.ternsip.glade.universal.AssimpLoader;
import com.ternsip.glade.universal.Mesh;
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
// TODO add texture manual assign in case model dont have schema
public class Glade {

    public static final DisplayManager DISPLAY_MANAGER = new DisplayManager();


    @SneakyThrows
    public static void main(String[] args) {

        DISPLAY_MANAGER.createDisplay();

        Sun sun = new Sun(new Vector2f(0, 0), new Vector2f(20000, 20000), new Vector3f(1, 1, 1));

        Mesh cubeModel = Cube.generateMesh();
        Entity cube = new Entity(cubeModel, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));

        //Model teapotModel = AssimpLoader.loadModel(new File("models/others/teapot.obj"), new File("models/others/teapot.obj"), new File("models/house"));
        //teapotModel.setPosition(new Vector3f(-20f, 2, 2));
        //teapotModel.setScale(new Vector3f(10, 10, 10));
        //teapotModel.setRotation(new Vector3f(0, 0, -90));

        Model bottleModel = AssimpLoader.loadModel(Settings.builder().meshFile(new File("models/bottle/bottle.3ds")).build());
        bottleModel.setPosition(new Vector3f(-30f, 0, -20));
        bottleModel.setScale(new Vector3f(1f, 1f, 1f));
        bottleModel.setRotation(new Vector3f(0, 0, 0));

        Model zebraModel = AssimpLoader.loadModel(Settings.builder().meshFile(new File("models/zebra/ZebraLOD1.ms3d")).build());
        zebraModel.setPosition(new Vector3f(-20f, 0, -20));
        zebraModel.setScale(new Vector3f(0.25f, 0.25f, 0.25f));
        zebraModel.setRotation(new Vector3f(0, 0, 0));

        Model hagreedModel = AssimpLoader.loadModel(Settings.builder().meshFile(new File("models/bob/boblamp.md5mesh")).animationFile(new File("models/bob/boblamp.md5anim")).build());
        hagreedModel.setPosition(new Vector3f(20f, 2, 2));
        hagreedModel.setScale(new Vector3f(0.25f, 0.25f, 0.25f));
        hagreedModel.setRotation(new Vector3f(0, 0, -90)); // TODO BUG IF I PUT 180 ROTATION

        Model spiderModel = AssimpLoader.loadModel(Settings.builder().meshFile(new File("models/spider/spider.dae")).manualTexture(new File("models/spider/Spinnen_Bein_tex_2.jpg")).build());
        spiderModel.setPosition(new Vector3f(20f, 2, -20));
        spiderModel.setScale(new Vector3f(1, 1, 1));
        spiderModel.setRotation(new Vector3f(0, 0, -90));

        Model warriorModel = AssimpLoader.loadModel(Settings.builder().meshFile(new File("models/warrior/warrior.3ds")).build());
        warriorModel.setPosition(new Vector3f(-20f, 2, 2));
        warriorModel.setScale(new Vector3f(10, 10, 10));
        warriorModel.setRotation(new Vector3f(0, 0, -90));
        Model shipModel = AssimpLoader.loadModel(Settings.builder().meshFile(new File("models/ship/ship.3ds")).build());
        shipModel.setPosition(new Vector3f(-10f, 2, 2));
        shipModel.setRotation(new Vector3f(0, 0, -90));

        Model boyModel = AssimpLoader.loadModel(Settings.builder().meshFile(new File("models/boy/boy.dae")).build());
        boyModel.setPosition(new Vector3f(2f, 2, 2));
        boyModel.setRotation(new Vector3f(0, 0, -90));

        Model dude2Model = AssimpLoader.loadModel(Settings.builder().meshFile(new File("models/dude/dude.3ds")).build());
        dude2Model.setPosition(new Vector3f(-20f, 0, -20));
        dude2Model.setScale(new Vector3f(10f, 10f, 10f));
        dude2Model.setRotation(new Vector3f(0, 0, -90));

        Mesh roverModel = ResourceLoader.loadObjModel(new File("models/rover/rover.obj"), new File("models/rover/rover.png"));
        Rover rover = new Rover(boyModel, roverModel, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
        Camera camera = new Camera(rover);

        //Model horror = AssimpLoader.loadModel(new File("models/horror/cloth_horror_model.dae"), new File("models/horror/anim/mii_hand_already_end.smd"), new File("models/horror/"));
        //horror.setPosition(new Vector3f(10f, 2, 2));
        //horror.setScale(new Vector3f(2, 2, 2));
        //horror.setRotation(new Vector3f(0, 0, -90));

        MasterRenderer renderer = new MasterRenderer(camera);
        //renderer.processEntity(rover);
        renderer.processEntity(cube);
        //renderer.processEntity(teapotModel);
        //renderer.processEntity(dude);
        renderer.processEntity(dude2Model);
        renderer.processEntity(zebraModel);
        renderer.processEntity(bottleModel);
        //renderer.processEntity(horror);
        renderer.processEntity(spiderModel);
        renderer.processEntity(boyModel);
        //renderer.processEntity(shipModel);
        //renderer.processEntity(skeletonModel);
        //renderer.processEntity(microwaveModel);
        //renderer.processEntity(spiderModel);
        renderer.processEntity(hagreedModel);
        renderer.processEntity(warriorModel);

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
