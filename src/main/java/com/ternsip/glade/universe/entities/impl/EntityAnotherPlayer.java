package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.impl.test.EffigyBoy;
import com.ternsip.glade.universe.entities.base.GraphicalEntity;
import com.ternsip.glade.universe.parts.player.PlayerAnimation;
import lombok.Getter;
import lombok.Setter;

import java.io.ObjectInputStream;

@Getter
@Setter
public class EntityAnotherPlayer extends GraphicalEntity<EffigyBoy> {

    private PlayerAnimation playerAnimation = PlayerAnimation.IDLE;

    @Override
    public void update(EffigyBoy effigy) {
        super.update(effigy);
        if (!effigy.getAnimation().getAnimationTrack().getName().equalsIgnoreCase(getPlayerAnimation().name())) {
            effigy.getAnimation().play(getPlayerAnimation().name().toLowerCase());
        }
    }

    @Override
    public EffigyBoy getEffigy() {
        return new EffigyBoy();
    }

    @Override
    public void readFromStream(ObjectInputStream ois) throws Exception {
        super.readFromStream(ois);
        setPlayerAnimation((PlayerAnimation) ois.readObject());
    }
}
