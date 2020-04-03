package com.ternsip.glade.universe.parts.player;

import com.ternsip.glade.universe.entities.impl.EntityPlayer;
import com.ternsip.glade.universe.entities.impl.EntityPlayerServer;
import org.joml.Vector3f;

public class JumpAction extends BaseAction {

    @Override
    public void applyOnServer(EntityPlayerServer player) {
        if (player.isOnTheGround()) {
            player.getCurrentVelocity().add(new Vector3f(0, player.getJumpPower(), 0));
        }
    }

    @Override
    public void applyOnClient(EntityPlayer player) {

    }

}
