package com.ternsip.glade.universe.parts.player;

import com.ternsip.glade.universe.entities.impl.EntityPlayerServer;
import org.joml.Vector3f;

public class RespawnAction extends BaseAction {

    @Override
    public void apply(EntityPlayerServer player) {
        player.setRotation(new Vector3f(0, 0, 0));
        player.setPosition(new Vector3f(50, 190, 50));
    }

}
