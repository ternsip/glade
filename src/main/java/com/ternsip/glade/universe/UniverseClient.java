package com.ternsip.glade.universe;

import com.ternsip.glade.common.logic.Threadable;
import com.ternsip.glade.universe.bindings.Bind;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.universe.entities.impl.EntitySides;
import com.ternsip.glade.universe.entities.impl.EntitySprite;
import com.ternsip.glade.universe.entities.impl.EntityStatistics2D;
import com.ternsip.glade.universe.entities.ui.EntityUIMenu;
import com.ternsip.glade.universe.interfaces.*;
import com.ternsip.glade.universe.protocol.ConsoleMessagePacket;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.File;

@Getter
@Setter
public class UniverseClient implements Threadable, IUniverseServer, INetworkClient, IBindings, ISoundRepository, IEntityClientRepository, IEventSnapReceiver {

    @Override
    public void init() {
        spawnEntities();
        startClient();
    }

    @Override
    public void update() {
        getEventSnapReceiver().update();
        getEntityClientRepository().update();
        getClient().getNetworkClientEventReceiver().update(); // TODO thing about updating it on network side (origin)
    }

    @SneakyThrows
    @Override
    public void finish() {
        getBindings().finish();
        getEntityClientRepository().finish();
        stopClientThread();
    }

    public void startClient() {
        getClient().connect("localhost", 6789);
    }

    public void stop() {
        IUniverseServer.stopUniverseServerThread();
        IUniverseClient.stopUniverseClientThread();
    }

    private void spawnEntities() {
        EntityUIMenu entityUIMenu = new EntityUIMenu();
        entityUIMenu.register();
        entityUIMenu.toggle();
        getBindings().addBindCallback(Bind.TOGGLE_MENU, entityUIMenu::toggle);
        new EntityStatistics2D(new File("fonts/default.png"), new Vector4f(1, 1, 0, 1), true).register();

        Entity aim = new EntitySprite(new File("tools/aim.png"), true, true);
        aim.setScale(new Vector3f(0.01f));
        aim.register();
        getEntityClientRepository().setAim(aim);

        getBindings().addBindCallback(Bind.TEST_BUTTON, () -> getClient().send(new ConsoleMessagePacket("HELLO 123")));

        new EntitySides().register();
    }

}
