package com.ternsip.glade.universe.protocol;

import com.ternsip.glade.network.ClientPacket;
import com.ternsip.glade.network.Connection;
import com.ternsip.glade.universe.entities.impl.EntityStatistics2D;
import com.ternsip.glade.universe.parts.blocks.Block;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.joml.Vector3i;

@RequiredArgsConstructor
@Getter
public class EntityStatisticsClientPacket extends ClientPacket {

    private final Vector3i lookingAtBlockPosition;
    private final Block lookingAtBlock;

    @Override
    @SneakyThrows
    public void apply(Connection connection) {
        EntityStatistics2D entityStatistics = getUniverseClient().getEntityClientRepository().getEntityByClass(EntityStatistics2D.class);
        entityStatistics.getLookingAtBlockPosition().set(getLookingAtBlockPosition());
        entityStatistics.setLookingAtBlock(getLookingAtBlock());
    }

}
