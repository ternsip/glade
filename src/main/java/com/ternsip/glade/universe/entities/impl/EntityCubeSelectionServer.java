package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.network.Connection;
import com.ternsip.glade.universe.entities.base.EntityClient;
import com.ternsip.glade.universe.entities.base.GraphicalEntityServer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.ObjectOutputStream;

@RequiredArgsConstructor
@Getter
// TODO works wrong way for multiple players
public class EntityCubeSelectionServer extends GraphicalEntityServer {

    @Override
    public EntityClient getEntityClient(Connection connection) {
        return new EntityCubeSelectionClient();
    }

    @Override
    public void writeToStream(ObjectOutputStream oos) throws Exception {
    }

}
