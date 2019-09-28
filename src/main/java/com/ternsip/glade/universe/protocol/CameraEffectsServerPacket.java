package com.ternsip.glade.universe.protocol;

import com.ternsip.glade.network.Connection;
import com.ternsip.glade.network.ServerPacket;
import com.ternsip.glade.universe.parts.blocks.Block;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector3ic;

@RequiredArgsConstructor
@Getter
public class CameraEffectsServerPacket extends ServerPacket {

    private final Vector3f cameraPosition;

    @Override
    public void apply(Connection connection) {
        Vector3ic camPos = new Vector3i((int) cameraPosition.x(), (int) cameraPosition.y(), (int) cameraPosition.z());
        boolean underWater = getUniverseServer().getBlocksRepository().isBlockExists(camPos) && getUniverseServer().getBlocksRepository().getBlock(camPos) == Block.WATER;
        getUniverseServer().getServer().send(new CameraEffectsClientPacket(underWater), connection);
    }

}
