package com.ternsip.glade.universe.parts.player;

import com.ternsip.glade.universe.entities.impl.EntityPlayer;
import com.ternsip.glade.universe.entities.impl.EntityPlayerServer;
import org.joml.Vector3f;

public class TeleportFarAction extends BaseAction {

    @Override
    public void applyOnServer(EntityPlayerServer player) {
        player.setRotation(new Vector3f(0, 0, 0));
        player.setPosition(new Vector3f(512, 190, 512));
    }

    @Override
    public void applyOnClient(EntityPlayer player) {

    }

}
