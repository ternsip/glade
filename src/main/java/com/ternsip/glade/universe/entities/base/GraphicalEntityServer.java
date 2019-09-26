package com.ternsip.glade.universe.entities.base;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;
import org.joml.Vector3ic;

import java.io.ObjectOutputStream;

import static com.ternsip.glade.common.logic.Maths.round;
import static com.ternsip.glade.universe.parts.chunks.BlocksRepository.MAX_LIGHT_LEVEL;

@Getter
@Setter
public abstract class GraphicalEntityServer extends EntityServer {

    @Delegate
    private final Volumetric volumetric = new Volumetric();

    @Override
    public void update() {
        super.update();
        Vector3ic blockPos = round(getPosition());
        setSkyIntensity(getUniverseServer().getBlocksRepository().isBlockExists(blockPos) ? getUniverseServer().getBlocksRepository().getSkyLight(blockPos) / (float) MAX_LIGHT_LEVEL : 1);
    }

    @Override
    public void writeToStream(ObjectOutputStream oos) throws Exception {
        oos.writeFloat(getPosition().x());
        oos.writeFloat(getPosition().y());
        oos.writeFloat(getPosition().z());
        oos.writeFloat(getRotation().x());
        oos.writeFloat(getRotation().y());
        oos.writeFloat(getRotation().z());
        oos.writeFloat(getScale().x());
        oos.writeFloat(getScale().y());
        oos.writeFloat(getScale().z());
        oos.writeBoolean(isVisible());
        oos.writeFloat(getSkyIntensity());
        oos.writeFloat(getEmitIntensity());
    }

}
