package com.ternsip.glade.universe.protocol;

import com.ternsip.glade.network.Connection;
import com.ternsip.glade.network.ServerPacket;
import com.ternsip.glade.universe.parts.blocks.Block;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.joml.LineSegmentf;
import org.joml.Vector3i;
import org.joml.Vector3ic;

@RequiredArgsConstructor
@Getter
public class EntityStatisticsServerPacket extends ServerPacket {

    private final LineSegmentf eyeSegment;

    @Override
    @SneakyThrows
    public void apply(Connection connection) {
        Vector3ic pos = getUniverseServer().getBlocksServerRepository().traverse(getEyeSegment(), (b, p) -> b != Block.AIR);
        if (pos != null) {
            getUniverseServer().getServer().send(new EntityStatisticsClientPacket(new Vector3i(pos), getUniverseServer().getBlocksServerRepository().getBlock(pos)), connection);
        } else {
            getUniverseServer().getServer().send(new EntityStatisticsClientPacket(new Vector3i((int) getEyeSegment().aX, (int) getEyeSegment().aY, (int) getEyeSegment().aZ), Block.AIR), connection);
        }
    }

}
