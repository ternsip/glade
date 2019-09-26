package com.ternsip.glade.universe.protocol;

import com.ternsip.glade.network.Connection;
import com.ternsip.glade.network.ServerPacket;
import com.ternsip.glade.universe.entities.impl.EntityPlayerServer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.LineSegmentf;
import org.joml.Vector3f;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class PlayerStateServerPacket extends ServerPacket {

    private final UUID entityUuid;
    private final Vector3f moveEffort;
    private final LineSegmentf eyeSegment;
    private final Vector3f rotation;

    @Override
    public void apply(Connection connection) {
        EntityPlayerServer entityPlayer = (EntityPlayerServer) getUniverseServer().getEntityServerRepository().getEntityByUUID(getEntityUuid());
        entityPlayer.setMoveEffort(getMoveEffort());
        entityPlayer.setEyeSegment(getEyeSegment());
        entityPlayer.setRotation(getRotation());
    }

}
