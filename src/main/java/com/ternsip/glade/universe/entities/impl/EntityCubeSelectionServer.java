package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.impl.test.EffigyCube;
import com.ternsip.glade.network.Connection;
import com.ternsip.glade.universe.entities.base.EntityClient;
import com.ternsip.glade.universe.entities.base.EntityGeneric;
import com.ternsip.glade.universe.entities.base.GraphicalEntityServer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
// TODO works wrong way for multiple players
public class EntityCubeSelectionServer extends GraphicalEntityServer {

    @Override
    public EntityClient getEntityClient(Connection connection) {
        return new EntityGeneric(EffigyCube::new);
    }
}
