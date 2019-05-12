package com.ternsip.glade;

import com.ternsip.glade.entity.*;
import com.ternsip.glade.model.Mesh;
import com.ternsip.glade.renderer.MasterRenderer;
import com.ternsip.glade.universal.AnimGameItem;
import com.ternsip.glade.universal.AssimpLoader;
import com.ternsip.glade.utils.DisplayManager;
import lombok.SneakyThrows;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.File;

import static com.ternsip.glade.universal.AssimpLoader.FLAG_ALLOW_ORIGINS_WITHOUT_BONES;

// BE CAREFUL BUFFER FLIPS
// TODO CHECKOUT BUFFERS (FLOATBUFFER ETC.) BECAUSE THEY ARE BUGGED
// TODO TURN ALL Vectors and Matrixes and Quaternions to constant interface (quatenrionfc/matrixfc)
// TODO MemoryUtil.memFree(posBuffer); ??
// TODO add texture manual assign in case model dont have schema
public class Glade {

    public static final DisplayManager DISPLAY_MANAGER = new DisplayManager();


    @SneakyThrows
    public static void main(String[] args) {

        DISPLAY_MANAGER.createDisplay();

        Sun sun = new Sun(new Vector2f(0, 0), new Vector2f(20000, 20000), new Vector3f(1, 1, 1));

        Mesh cubeModel = Cube.generateMesh();
        Entity cube = new Entity(cubeModel, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));

        //AnimGameItem teapotModel = AssimpLoader.loadAnimGameItem(new File("models/others/teapot.obj"), new File("models/others/teapot.obj"), new File("models/house"));
        //teapotModel.setPosition(new Vector3f(-20f, 2, 2));
        //teapotModel.setScale(new Vector3f(10, 10, 10));
        //teapotModel.setRotation(new Vector3f(0, 0, -90));

        //Mesh dudeModel = ResourceLoader.loadObjModel(new File("models/dude/dude.obj"), new File("models/dude/dude.png"));
        //Entity dude = new Entity(dudeModel, new Vector3f(-20, 0, -20), new Vector3f(0, 0, 0), new Vector3f(10, 10, 10));

        //AnimatedModel spiderModel = AnimatedModelLoader.loadEntity(new File("models/spider/spider.dae"), new File("models/spider/Spinnen_Bein_tex_COLOR_.png"), new File("models/spider/spider.dae"));
        // AnimatedModel boyModel = AnimatedModelLoader.loadEntity(new File("models/boy/boy.dae"), new File("models/boy/boy.png"), new File("models/boy/boy.dae"));
        //AnimatedModel lampModel = AnimatedModelLoader.loadEntity(new File("models/lamp/lamp.dae"), new File("models/boy/boy.png"), new File("models/lamp/lamp.dae"));
        //AnimatedModel skeletonModel = AnimatedModelLoader.loadEntity(new File("models/skeleton/skeleton.dae"), new File("models/boy/boy.png"), new File("models/skeleton/skeleton.dae"));
        //AnimatedModel microwaveModel = AnimatedModelLoader.loadEntity(new File("models/microwave/microwave.dae"), new File("models/microwave/microwave_col.png"), new File("models/microwave/microwave.dae"));

        AnimGameItem hagreedModel = AssimpLoader.loadAnimGameItem(new File("models/bob/boblamp.md5mesh"), new File("models/bob/boblamp.md5anim"), new File("models/bob/"), FLAG_ALLOW_ORIGINS_WITHOUT_BONES);
        hagreedModel.setPosition(new Vector3f(20f, 2, 2));
        hagreedModel.setScale(new Vector3f(0.25f, 0.25f, 0.25f));
        hagreedModel.setRotation(new Vector3f(0, 0, -179)); // TODO BUG IF I PUT 180 ROTATION

        //AnimGameItem spiderModel = AssimpLoader.loadAnimGameItem(new File("models/spider/spider.dae"), new File("models/spider/spider.dae"), new File("models/spider/"));
        //AnimGameItem spiderModel2 = AssimpLoader.loadAnimGameItem(new File("models/spider2/spider.3ds"), new File("models/spider2/spider.3ds"), new File("models/spider2/textures"));
        AnimGameItem warriorModel = AssimpLoader.loadAnimGameItem(new File("models/warrior/warrior.3ds"), new File("models/warrior/warrior.3ds"), new File("models/warrior/textures"));
        warriorModel.setPosition(new Vector3f(-20f, 2, 2));
        warriorModel.setScale(new Vector3f(10, 10, 10));
        warriorModel.setRotation(new Vector3f(0, 0, -90));
        AnimGameItem shipModel = AssimpLoader.loadAnimGameItem(new File("models/ship/ship.3ds"), new File("models/ship/ship.3ds"), new File("models/ship/"));
        shipModel.setPosition(new Vector3f(-10f, 2, 2));
        shipModel.setRotation(new Vector3f(0, 0, -90));
        AnimGameItem microwaveModel = AssimpLoader.loadAnimGameItem(new File("models/microwave/microwave.dae"), new File("models/microwave/microwave.dae"), new File("models/microwave/"));
        microwaveModel.setScale(new Vector3f(2, 2, 2));

        AnimGameItem boyModel = AssimpLoader.loadAnimGameItem(new File("models/boy/boy.dae"), new File("models/boy/boy.dae"), new File("models/boy/"));
        boyModel.setPosition(new Vector3f(2f, 2, 2));
        boyModel.setRotation(new Vector3f(0, 0, -90));

        AnimGameItem horseModel = AssimpLoader.loadAnimGameItem(new File("models/house/horse.3ds"), new File("models/house/horse.3ds"), new File("models/house/"));
        horseModel.setPosition(new Vector3f(2f, 2, 2));
        horseModel.setRotation(new Vector3f(0, 0, -90));

        AnimGameItem dude2Model = AssimpLoader.loadAnimGameItem(new File("models/dude/dude.3ds"), new File("models/dude/dude.3ds"), new File("models/dude2/"));
        dude2Model.setPosition(new Vector3f(-20f, 0, -20));
        dude2Model.setScale(new Vector3f(10f, 10f, 10f));
        dude2Model.setRotation(new Vector3f(0, 0, -90));

        AnimGameItem repModel = AssimpLoader.loadAnimGameItem(new File("models/zebra/ZebraLOD1.ms3d"), new File("models/zebra/ZebraLOD1.ms3d"), new File("models/zebra/"), FLAG_ALLOW_ORIGINS_WITHOUT_BONES);
        repModel.setPosition(new Vector3f(-20f, 0, -20));
        repModel.setScale(new Vector3f(0.25f, 0.25f, 0.25f));
        repModel.setRotation(new Vector3f(0, 0, 0));

        Mesh roverModel = ResourceLoader.loadObjModel(new File("models/rover/rover.obj"), new File("models/rover/rover.png"));
        Rover rover = new Rover(boyModel, roverModel, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
        Camera camera = new Camera(rover);

        //AnimGameItem horror = AssimpLoader.loadAnimGameItem(new File("models/horror/cloth_horror_model.dae"), new File("models/horror/anim/mii_hand_already_end.smd"), new File("models/horror/"));
        //horror.setPosition(new Vector3f(10f, 2, 2));
        //horror.setScale(new Vector3f(2, 2, 2));
        //horror.setRotation(new Vector3f(0, 0, -90));

        MasterRenderer renderer = new MasterRenderer(camera);
        renderer.processEntity(rover);
        renderer.processEntity(cube);
        //renderer.processEntity(teapotModel);
        //renderer.processEntity(dude);
        renderer.processEntity(dude2Model);
        renderer.processEntity(repModel);
        //renderer.processEntity(horror);
        //renderer.processEntity(spiderModel2);
        renderer.processEntity(boyModel);
        //renderer.processEntity(horseModel);
        //renderer.processEntity(shipModel);
        //renderer.processEntity(lampModel);
        //renderer.processEntity(skeletonModel);
        //renderer.processEntity(microwaveModel);
        //renderer.processEntity(spiderModel);
        renderer.processEntity(hagreedModel);
        //renderer.processEntity(warriorModel);

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
