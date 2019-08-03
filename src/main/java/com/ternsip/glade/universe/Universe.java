package com.ternsip.glade.universe;

import com.ternsip.glade.common.events.base.EventSnapReceiver;
import com.ternsip.glade.common.logic.ThreadWrapper;
import com.ternsip.glade.common.logic.Threadable;
import com.ternsip.glade.common.logic.TimeNormalizer;
import com.ternsip.glade.graphics.visual.impl.basis.EffigyAxis;
import com.ternsip.glade.graphics.visual.impl.basis.EffigyDynamicText;
import com.ternsip.glade.graphics.visual.impl.test.*;
import com.ternsip.glade.network.NetworkClient;
import com.ternsip.glade.network.NetworkServer;
import com.ternsip.glade.network.requests.BlocksObserverChanged;
import com.ternsip.glade.universe.audio.SoundRepository;
import com.ternsip.glade.universe.bindings.Bind;
import com.ternsip.glade.universe.bindings.Bindings;
import com.ternsip.glade.universe.collisions.base.Collisions;
import com.ternsip.glade.universe.collisions.impl.ChunksObstacle;
import com.ternsip.glade.universe.collisions.impl.GroundObstacle;
import com.ternsip.glade.universe.common.Balance;
import com.ternsip.glade.universe.common.Universal;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.entities.impl.*;
import com.ternsip.glade.universe.entities.repository.EntityRepository;
import com.ternsip.glade.universe.entities.ui.EntityUIMenu;
import com.ternsip.glade.universe.parts.chunks.Blocks;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.File;

@Getter
@Setter
public class Universe implements Threadable {

    private final EventSnapReceiver eventSnapReceiver = new EventSnapReceiver();

    private final EntityRepository entityRepository = new EntityRepository();

    private final SoundRepository soundRepository = new SoundRepository();

    private final String name = "universe";

    private final Balance balance = new Balance();

    @Getter(lazy = true)
    private final Collisions collisions = new Collisions();

    @Getter(lazy = true)
    private final ThreadWrapper<Blocks> blocksThread = new ThreadWrapper<>(new Blocks());

    @Getter(lazy = true)
    private final Bindings bindings = new Bindings();

    @Getter(lazy = true)
    private final ThreadWrapper<NetworkServer> serverThread = new ThreadWrapper<>(new NetworkServer());

    @Getter(lazy = true)
    private final ThreadWrapper<NetworkClient> clientThread = new ThreadWrapper<>(new NetworkClient());

    private final TimeNormalizer timeNormalizer = new TimeNormalizer(1000L / getBalance().getTicksPerSecond());

    @Override
    public void init() {
        getServer().bind(6789);
        getClient().connect("localhost", 6789);
        spawnTestEntities();
    }

    @Override
    public void update() {
        if (!getEventSnapReceiver().isApplicationActive()) {
            stop();
        }
        getTimeNormalizer().drop();
        getEventSnapReceiver().update();
        getEntityRepository().update();
        getCollisions().update();
        getTimeNormalizer().rest();
    }

    public Blocks getBlocks() {
        return getBlocksThread().getObjective();
    }

    public NetworkClient getClient() {
        return getClientThread().getObjective();
    }

    public NetworkServer getServer() {
        return getServerThread().getObjective();
    }

    public void stop() {
        Universal.UNIVERSE_THREAD.stop();
    }

    @SneakyThrows
    @Override
    public void finish() {
        getBlocksThread().stop();
        getBindings().finish();
        getClient().stop();
        getClientThread().stop();
        getServer().stop();
        getServerThread().stop();
    }

    private void spawnTestEntities() {
        Entity aim = new EntitySprite(new File("tools/aim.png"), true, true);
        aim.setScale(new Vector3f(0.01f));
        aim.register();
        getEntityRepository().setAim(aim);

        EntitySun sun = new EntitySun();
        sun.register();
        getEntityRepository().setSun(sun);

        Entity cube = new EntityGeneric(() -> new EffigyCube());
        cube.register();

        Entity lamp = new EntityGeneric(() -> new EffigyLamp());
        lamp.register();
        lamp.register();
        lamp.setPosition(new Vector3f(-60f, 0, -60));
        lamp.setScale(new Vector3f(40, 40, 40));

        Entity bottle = new EntityGeneric(() -> new EffigyBottle());
        bottle.register();
        bottle.setPosition(new Vector3f(-30f, 0, -20));
        bottle.setScale(new Vector3f(5, 5, 5));

        Entity zebra = new EntityGeneric(() -> new EffigyZebra());
        zebra.register();
        zebra.setPosition(new Vector3f(-20f, 0, -20));
        zebra.setScale(new Vector3f(30, 30, 30));

        Entity wolf = new EntityGeneric(() -> new EffigyWolf());
        wolf.register();
        wolf.setPosition(new Vector3f(-140f, 0, -40));
        wolf.setScale(new Vector3f(30, 30, 30));

        Entity hagrid = new EntityGenericRotating(() -> new EffigyHagrid(), new Vector3f(0, 0.01f, 0));
        hagrid.register();
        hagrid.setPosition(new Vector3f(20f, 2, 2));
        hagrid.setScale(new Vector3f(15, 15, 15));

        Entity spider = new EntityGeneric(() -> new EffigySpider());
        spider.register();
        spider.setPosition(new Vector3f(20f, 2, -20));
        spider.setScale(new Vector3f(5, 5, 5));

        Entity warrior = new EntityGeneric(() -> new EffigyWarrior());
        warrior.register();
        warrior.setPosition(new Vector3f(-20f, 2, 2));
        warrior.setScale(new Vector3f(10, 10, 10));

        Entity dude = new EntityGeneric(() -> new EffigyDude());
        dude.register();
        dude.setPosition(new Vector3f(-20f, 0, -20));
        dude.setScale(new Vector3f(10f, 10f, 10f));

        new EntityGenericRotating(() -> new EffigyDynamicText(new File("fonts/default.png"), false, false, new Vector4f(0, 0, 1, 1), "Hello world!"), new Vector3f(0, 0.1f, 0)).register();
        new EntityGeneric(() -> new EffigyAxis()).register();

        EntityUIMenu entityUIMenu = new EntityUIMenu();
        entityUIMenu.register();

        new EntityStatistics2D(new File("fonts/default.png"), new Vector4f(1, 1, 0, 1), true).register();

        getCollisions().add(new GroundObstacle());
        getCollisions().add(new ChunksObstacle());

        for (int i = 0; i < 10; ++i) {
            for (int j = 0; j < 10; ++j) {
                Entity hagrid1 = new EntityGenericRotating(() -> new EffigyHagrid(), new Vector3f(0, 0.01f, 0));
                hagrid1.register();
                hagrid1.setPosition(new Vector3f(20f + 15 * i, 2, 2 + 15 * j));
                hagrid1.setScale(new Vector3f(15, 15, 15));
            }
        }

        getBindings().addBindCallback(Bind.TOGGLE_MENU, entityUIMenu::toggle);
        getBindings().addBindCallback(Bind.TEST_BUTTON, () -> getClient().send("HELLO 123"));
        getClient().registerCallback(String.class, (conn, str) -> System.out.println("Received message from srv " + str));
        getServer().registerCallback(String.class, (conn, str) -> System.out.println("Received message from client " + str));
        getServer().registerCallback(BlocksObserverChanged.class, (conn, boc) -> getBlocks().requestBlockUpdates(boc.getPrevPos(), boc.getNextPos(), boc.getPrevViewDistance(), boc.getNextViewDistance()));

        EntityPlayer entityPlayer = new EntityPlayer();
        entityPlayer.register();
        entityPlayer.setPosition(new Vector3f(50, 90, 50));
        entityPlayer.setScale(new Vector3f(1, 1, 1));
        getEntityRepository().setCameraTarget(entityPlayer);

        new EntitySides().register();
    }

}
