package com.ternsip.glade;

import com.ternsip.glade.entity.*;
import com.ternsip.glade.model.GLModel;
import com.ternsip.glade.model.loader.animation.loaders.AnimatedModelLoader;
import com.ternsip.glade.model.loader.animation.model.AnimatedModel;
import com.ternsip.glade.model.parser.Model;
import com.ternsip.glade.model.parser.ModelObject;
import com.ternsip.glade.model.parser.Parser;
import com.ternsip.glade.renderer.MasterRenderer;
import com.ternsip.glade.utils.DisplayManager;
import com.ternsip.glade.utils.Utils;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.Assimp;
import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.nio.ByteBuffer;

import static com.ternsip.glade.model.GLModel.SKIP_TEXTURE;
import static com.ternsip.glade.utils.Maths.PI;
import static org.lwjgl.assimp.Assimp.aiImportFileEx;
import static org.lwjgl.assimp.Assimp.aiProcess_JoinIdenticalVertices;
import static org.lwjgl.assimp.Assimp.aiProcess_Triangulate;

// BE CAREFUL BUFFER FLIPS
// TODO CHECKOUT BUFFERS (FLOATBUFFER ETC.) BECAUSE THEY ARE BUGGED
// TODO TURN ALL Vectors and Matrixes and Quaternions to constant interface (quatenrionfc/matrixfc)
public class Glade {

    public static final DisplayManager DISPLAY_MANAGER = new DisplayManager();

    @SneakyThrows
    public static void main(String[] args) {

        DISPLAY_MANAGER.createDisplay();

        Sun sun = new Sun(new Vector2f(0, 0), new Vector2f(20000, 20000), new Vector3f(1, 1, 1));


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

        AnimatedModel spiderModel = AnimatedModelLoader.loadEntity(new File("models/spider/spider.dae"), new File("models/spider/Spinnen_Bein_tex_COLOR_.png"), new File("models/spider/spider.dae"));
        AnimatedModel boyModel = AnimatedModelLoader.loadEntity(new File("models/boy/boy.dae"), new File("models/boy/boy.png"), new File("models/boy/boy.dae"));
        //AnimatedModel lampModel = AnimatedModelLoader.loadEntity(new File("models/lamp/lamp.dae"), new File("models/boy/boy.png"), new File("models/lamp/lamp.dae"));
        //AnimatedModel skeletonModel = AnimatedModelLoader.loadEntity(new File("models/skeleton/skeleton.dae"), new File("models/boy/boy.png"), new File("models/skeleton/skeleton.dae"));
        //AnimatedModel microwaveModel = AnimatedModelLoader.loadEntity(new File("models/microwave/microwave.dae"), new File("models/microwave/microwave_col.png"), new File("models/microwave/microwave.dae"));


        // Assimp here
        byte[] _data = IOUtils.toByteArray(Utils.loadResourceAsStream(new File("models/spider/spider.dae")));
        ByteBuffer data = MemoryUtil.memCalloc(_data.length);
        data.put(_data);
        data.flip();

        AIScene scene = Assimp.aiImportFileFromMemory(
                data,
                Assimp.aiProcess_Triangulate |
                        Assimp.aiProcess_ValidateDataStructure,
                ""
        );
        scene.mNumMeshes();
        MemoryUtil.memFree(data);

        MasterRenderer renderer = new MasterRenderer(camera);
        renderer.processEntity(rover);
        renderer.processEntity(cube);
        for (ModelObject o : ship.objects) {
            renderer.processEntity(new Entity(o.getGLModel(), new Vector3f(20, 0, 0), new Vector3f(0, 0, -4f * PI), new Vector3f(0.25f, 0.25f, 0.25f)));
        }
        renderer.processEntity(house);
        renderer.processEntity(dude);
        renderer.processEntity(boyModel);
        //renderer.processEntity(lampModel);
        //renderer.processEntity(skeletonModel);
        //renderer.processEntity(microwaveModel);
        renderer.processEntity(spiderModel);

        // TODO Check performance with runnable and without it
        DISPLAY_MANAGER.loop(() -> {
            rover.move();
            camera.move();
            sun.move();
            renderer.render(sun, camera);

        });

        boyModel.delete();// TODO make in automatic

        renderer.cleanUp();
        DISPLAY_MANAGER.closeDisplay();

    }
}
