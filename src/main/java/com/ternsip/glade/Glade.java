package com.ternsip.glade;

import com.ternsip.glade.entity.*;
import com.ternsip.glade.renderer.MasterRenderer;
import com.ternsip.glade.universal.AssimpLoader;
import com.ternsip.glade.universal.Mesh;
import com.ternsip.glade.universal.Model;
import com.ternsip.glade.utils.DisplayManager;
import lombok.SneakyThrows;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.File;

// BE CAREFUL BUFFER FLIPS
// TODO CHECKOUT BUFFERS (FLOATBUFFER ETC.) BECAUSE THEY ARE BUGGED
// TODO TURN ALL Vectors and Matrixes and Quaternions to constant interface (quatenrionfc/matrixfc)
// TODO MemoryUtil.memFree(posBuffer); ??
// TODO add texture manual assign in case model dont have schema
// TODO JOINTS -> Bones rename
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

        Model bottleModel = AssimpLoader.loadModel(new File("models/bottle/bottle.3ds"), new File("models/bottle/bottle.3ds"), new File("models/bottle/"));
        bottleModel.setPosition(new Vector3f(-30f, 0, -20));
        bottleModel.setScale(new Vector3f(1f, 1f, 1f));
        bottleModel.setRotation(new Vector3f(0, 0, 0));

        Model zebraModel = AssimpLoader.loadModel(new File("models/zebra/ZebraLOD1.ms3d"), new File("models/zebra/ZebraLOD1.ms3d"), new File("models/zebra/"));
        zebraModel.setPosition(new Vector3f(-20f, 0, -20));
        zebraModel.setScale(new Vector3f(0.25f, 0.25f, 0.25f));
        zebraModel.setRotation(new Vector3f(0, 0, 0));

        Model hagreedModel = AssimpLoader.loadModel(new File("models/bob/boblamp.md5mesh"), new File("models/bob/boblamp.md5anim"), new File("models/bob/"));
        hagreedModel.setPosition(new Vector3f(20f, 2, 2));
        hagreedModel.setScale(new Vector3f(0.25f, 0.25f, 0.25f));
        hagreedModel.setRotation(new Vector3f(0, 0, -90)); // TODO BUG IF I PUT 180 ROTATION

        Model spiderModel = AssimpLoader.loadModel(new File("models/spider/spider.dae"), new File("models/spider/spider.dae"), new File("models/spider/textures"));
        spiderModel.setPosition(new Vector3f(20f, 2, -20));
        spiderModel.setScale(new Vector3f(1, 1, 1));
        spiderModel.setRotation(new Vector3f(0, 0, -90));

        Model warriorModel = AssimpLoader.loadModel(new File("models/warrior/warrior.3ds"), new File("models/warrior/warrior.3ds"), new File("models/warrior"));
        warriorModel.setPosition(new Vector3f(-20f, 2, 2));
        warriorModel.setScale(new Vector3f(10, 10, 10));
        warriorModel.setRotation(new Vector3f(0, 0, -90));
        Model shipModel = AssimpLoader.loadModel(new File("models/ship/ship.3ds"), new File("models/ship/ship.3ds"), new File("models/ship/"));
        shipModel.setPosition(new Vector3f(-10f, 2, 2));
        shipModel.setRotation(new Vector3f(0, 0, -90));
        Model microwaveModel = AssimpLoader.loadModel(new File("models/microwave/microwave.dae"), new File("models/microwave/microwave.dae"), new File("models/microwave/"));
        microwaveModel.setScale(new Vector3f(2, 2, 2));

        Model boyModel = AssimpLoader.loadModel(new File("models/boy/boy.dae"), new File("models/boy/boy.dae"), new File("models/boy/"));
        boyModel.setPosition(new Vector3f(2f, 2, 2));
        boyModel.setRotation(new Vector3f(0, 0, -90));

        Model dude2Model = AssimpLoader.loadModel(new File("models/dude/dude.3ds"), new File("models/dude/dude.3ds"), new File("models/dude2/"));
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
