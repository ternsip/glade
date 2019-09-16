package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.base.Effigy;
import com.ternsip.glade.graphics.visual.base.EffigyDummy;
import com.ternsip.glade.network.ServerSide;
import com.ternsip.glade.universe.entities.base.Entity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Getter
@Setter
@ServerSide
public class EntityServerPlayers extends Entity {

    private Set<EntityPlayer> players = ConcurrentHashMap.newKeySet();

    @Override
    public Effigy getEffigy() {
        return new EffigyDummy();
    }

    @Override
    public void serverUpdate() {
        super.serverUpdate();
    }

}
