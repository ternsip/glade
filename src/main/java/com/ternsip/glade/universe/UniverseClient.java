package com.ternsip.glade.universe;

import com.ternsip.glade.common.events.base.Callback;
import com.ternsip.glade.common.events.network.OnConnectedToServer;
import com.ternsip.glade.common.logic.Threadable;
import com.ternsip.glade.graphics.visual.impl.basis.EffigyAxis;
import com.ternsip.glade.network.INetworkClientEventReceiver;
import com.ternsip.glade.universe.bindings.Bind;
import com.ternsip.glade.universe.entities.base.EntityGeneric;
import com.ternsip.glade.universe.entities.impl.EntityAim;
import com.ternsip.glade.universe.entities.impl.EntityCameraEffects;
import com.ternsip.glade.universe.entities.impl.EntitySides;
import com.ternsip.glade.universe.entities.impl.EntityStatistics2D;
import com.ternsip.glade.universe.entities.ui.EntityUIMenu;
import com.ternsip.glade.universe.entities.ui.UIInventory;
import com.ternsip.glade.universe.interfaces.*;
import com.ternsip.glade.universe.protocol.ConsoleMessageServerPacket;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.joml.Vector4f;

import java.io.File;

@Getter
@Setter
public class UniverseClient implements Threadable, INetworkClient, IBlocksRepositoryClient, IBindings, IBalance, ISoundRepository, IEntityClientRepository, IEventIOReceiver, INetworkClientEventReceiver {

    private final Callback<OnConnectedToServer> onConnectedToServerCallback = this::whenConnected;

    @Override
    public void init() {
        spawnMenu();
        getNetworkClientEventReceiver().registerCallback(OnConnectedToServer.class, getOnConnectedToServerCallback());
    }

    @Override
    public void update() {
        getEventIOReceiver().update();
        getEntityClientRepository().update();
        getNetworkClientEventReceiver().update();
    }

    @SneakyThrows
    @Override
    public void finish() {
        getNetworkClientEventReceiver().unregisterCallback(OnConnectedToServer.class, getOnConnectedToServerCallback());
        stopBlocksClientThread();
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

    private void spawnMenu() {
        EntityUIMenu entityUIMenu = new EntityUIMenu();
        entityUIMenu.register();
        getBindings().addBindCallback(Bind.TOGGLE_MENU, entityUIMenu::toggle);
        new UIInventory().register();
    }

    private void spawnEntities() {
        new EntityCameraEffects().register();
        new EntityStatistics2D(new File("fonts/default.png"), new Vector4f(1, 1, 0, 1), true).register();

        new EntityAim().register();

        new EntityGeneric(EffigyAxis::new).register();

        getBindings().addBindCallback(Bind.TEST_BUTTON, () -> getClient().send(new ConsoleMessageServerPacket("HELLO 123")));

        new EntitySides().register();
    }

    private void whenConnected(OnConnectedToServer onConnectedToServer) {
        spawnEntities();
    }

}
