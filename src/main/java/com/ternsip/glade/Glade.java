package com.ternsip.glade;

import com.ternsip.glade.entity.*;
import com.ternsip.glade.model.Mesh;
import com.ternsip.glade.renderer.MasterRenderer;
import com.ternsip.glade.universal.AnimGameItem;
import com.ternsip.glade.universal.AnimMeshesLoader;
import com.ternsip.glade.utils.DisplayManager;
import lombok.SneakyThrows;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.File;

import static com.ternsip.glade.model.Mesh.SKIP_TEXTURE;

// BE CAREFUL BUFFER FLIPS
// TODO CHECKOUT BUFFERS (FLOATBUFFER ETC.) BECAUSE THEY ARE BUGGED
// TODO TURN ALL Vectors and Matrixes and Quaternions to constant interface (quatenrionfc/matrixfc)
// TODO MemoryUtil.memFree(posBuffer); ??
public class Glade {

    public static final DisplayManager DISPLAY_MANAGER = new DisplayManager();

    @SneakyThrows
    public static void main(String[] args) {

        DISPLAY_MANAGER.createDisplay();

        Sun sun = new Sun(new Vector2f(0, 0), new Vector2f(20000, 20000), new Vector3f(1, 1, 1));

        Mesh cubeModel = Cube.generateMesh();
        Entity cube = new Entity(cubeModel, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));

        Mesh houseModel = ResourceLoader.loadObjModel(new File("models/house/house.obj"), SKIP_TEXTURE);
        Entity house = new Entity(houseModel, new Vector3f(-20, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));

        Mesh dudeModel = ResourceLoader.loadObjModel(new File("models/dude/dude.obj"), new File("models/dude/dude.png"));
        Entity dude = new Entity(dudeModel, new Vector3f(-20, 0, -20), new Vector3f(0, 0, 0), new Vector3f(10, 10, 10));

        //AnimatedModel spiderModel = AnimatedModelLoader.loadEntity(new File("models/spider/spider.dae"), new File("models/spider/Spinnen_Bein_tex_COLOR_.png"), new File("models/spider/spider.dae"));
        // AnimatedModel boyModel = AnimatedModelLoader.loadEntity(new File("models/boy/boy.dae"), new File("models/boy/boy.png"), new File("models/boy/boy.dae"));
        //AnimatedModel lampModel = AnimatedModelLoader.loadEntity(new File("models/lamp/lamp.dae"), new File("models/boy/boy.png"), new File("models/lamp/lamp.dae"));
        //AnimatedModel skeletonModel = AnimatedModelLoader.loadEntity(new File("models/skeleton/skeleton.dae"), new File("models/boy/boy.png"), new File("models/skeleton/skeleton.dae"));
        //AnimatedModel microwaveModel = AnimatedModelLoader.loadEntity(new File("models/microwave/microwave.dae"), new File("models/microwave/microwave_col.png"), new File("models/microwave/microwave.dae"));

        //AnimGameItem hagreedModel = AnimMeshesLoader.loadAnimGameItem(new File("models/bob/boblamp.md5mesh"), new File("models/bob/boblamp.md5anim"), new File("models/bob/"));
        AnimGameItem spiderModel = AnimMeshesLoader.loadAnimGameItem(new File("models/spider/spider.dae"), new File("models/spider/spider.dae"), new File("models/spider/"));
        AnimGameItem shipModel = AnimMeshesLoader.loadAnimGameItem(new File("models/ship/ship.3ds"), new File("models/ship/ship.3ds"), new File("models/ship/"));
        shipModel.setPosition(new Vector3f(-10f, 2, 2));
        shipModel.setRotation(new Vector3f(0, 0, -90));
        //AnimGameItem microwaveModel = AnimMeshesLoader.loadAnimGameItem(new File("models/microwave/microwave.dae"), new File("models/microwave/microwave.dae"), new File("models/microwave/"));
        //microwaveModel.setScale(new Vector3f(10, 10, 10));

        AnimGameItem boyModel = AnimMeshesLoader.loadAnimGameItem(new File("models/boy/boy.dae"), new File("models/boy/boy.dae"), new File("models/boy/"));
        boyModel.setPosition(new Vector3f(2f, 2, 2));
        boyModel.setRotation(new Vector3f(0, 0, -90));

        Mesh roverModel = ResourceLoader.loadObjModel(new File("models/rover/rover.obj"), new File("models/rover/rover.png"));
        Rover rover = new Rover(boyModel, roverModel, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
        Camera camera = new Camera(rover);

        MasterRenderer renderer = new MasterRenderer(camera);
        renderer.processEntity(rover);
        renderer.processEntity(cube);
        renderer.processEntity(house);
        renderer.processEntity(dude);
        renderer.processEntity(boyModel);
        renderer.processEntity(shipModel);
        //renderer.processEntity(lampModel);
        //renderer.processEntity(skeletonModel);
        //renderer.processEntity(microwaveModel);
        //renderer.processEntity(spiderModel);
        //renderer.processEntity(hagreedModel);

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
