package com.ternsip.glade.universe.parts.player;

import com.ternsip.glade.universe.entities.impl.EntityPlayer;
import com.ternsip.glade.universe.entities.impl.EntityPlayerServer;
import org.joml.Vector3f;

public class TeleportZeroAction extends BaseAction {

    @Override
    public void applyOnServer(EntityPlayerServer player) {
        player.setPosition(new Vector3f(-5, 10, -5));
    }

    @Override
    public void applyOnClient(EntityPlayer player) {

    }

}
