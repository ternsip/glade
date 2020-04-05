package com.ternsip.glade.graphics.visual.impl.basis;

import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.general.ShaderBuffer;
import com.ternsip.glade.graphics.shader.impl.LightMassShader;
import com.ternsip.glade.graphics.visual.base.Effigy;
import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.chunks.ChangeBlocksRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.util.concurrent.ConcurrentLinkedDeque;

import static com.ternsip.glade.universe.parts.chunks.BlocksClientRepository.*;

@Slf4j
@RequiredArgsConstructor
@Getter
@Setter
public class EffigyLightMass extends Effigy<LightMassShader> {

    private final ConcurrentLinkedDeque<ChangeBlocksRequest> changeBlocksRequests;

    private ShaderBuffer skyBuffer = new ShaderBuffer(new int[VIEW_DISTANCE * VIEW_DISTANCE * VIEW_DISTANCE]);
    private ShaderBuffer emitBuffer = new ShaderBuffer(new int[VIEW_DISTANCE * VIEW_DISTANCE * VIEW_DISTANCE]);
    private ShaderBuffer selfEmitBuffer = new ShaderBuffer(new int[VIEW_DISTANCE * VIEW_DISTANCE * VIEW_DISTANCE]);
    private ShaderBuffer opacityBuffer = new ShaderBuffer(new int[VIEW_DISTANCE * VIEW_DISTANCE * VIEW_DISTANCE]);
    private ShaderBuffer heightBuffer = new ShaderBuffer(new int[VIEW_DISTANCE * VIEW_DISTANCE]);

    public void finish() {
        getSkyBuffer().finish();
        getEmitBuffer().finish();
        getSelfEmitBuffer().finish();
        getOpacityBuffer().finish();
        getHeightBuffer().finish();
    }

    public void updateBuffers(ChangeBlocksRequest changeBlocksRequest) {
        Vector3ic start = changeBlocksRequest.getStart();
        Vector3ic endExcluding = changeBlocksRequest.getEndExcluding();
        for (int x = start.x(); x < endExcluding.x(); ++x) {
            for (int z = start.z(); z < endExcluding.z(); ++z) {
                for (int y = 0; y < SIZE_Y; ++y) {
                    int index = (int) INDEXER.getIndexLooping(x, y, z);
                    int heightIndex = (int) INDEXER.getIndexLooping(x, 0, z);
                    Block block = getUniverseClient().getBlocksClientRepository().getBlock(x, y, z);
                    selfEmitBuffer.getData()[index] = block.getEmitLight();
                    opacityBuffer.getData()[index] = block.getLightOpacity();
                    if (block != Block.AIR) {
                        heightBuffer.getData()[heightIndex] = y + 1;
                    }
                }
            }
        }
        selfEmitBuffer.updateBuffer();
        heightBuffer.updateBuffer();
        opacityBuffer.updateBuffer();
    }

    //public void applyChanges(BlockSidesUpdateClientPacket blockSidesUpdateClientPacket) {
//
    //    blockSidesUpdateClientPacket.getBlocksToChange().getPositionToBlock().forEach((pos, block) -> {
    //        long index = (int) INDEXER.getIndexLooping(pos);
    //        int x = INDEXER.getX(index);
    //        int y = INDEXER.getY(index);
    //        int z = INDEXER.getZ(index);
    //        blocks[x][y][z] = block;
    //        selfEmitBuffer.getData()[(int) index] = block.getEmitLight();
    //        opacityBuffer.getData()[(int) index] = block.getLightOpacity();
    //    });
//
    //}

    @Override
    public void render() {
        if (!changeBlocksRequests.isEmpty()) {
            ChangeBlocksRequest changeBlocksRequest = changeBlocksRequests.poll();
            updateBuffers(changeBlocksRequest);
            Vector3ic startLight = new Vector3i(changeBlocksRequest.getStart()).sub(new Vector3i(MAX_LIGHT_LEVEL - 1)).max(new Vector3i(0));
            Vector3ic endLightExcluding = new Vector3i(changeBlocksRequest.getEndExcluding()).add(new Vector3i(MAX_LIGHT_LEVEL - 1)).min(SIZE);
            Vector3ic lightSize = new Vector3i(endLightExcluding).sub(startLight);
            getShader().start();
            getShader().getSkyBuffer().load(getSkyBuffer());
            getShader().getEmitBuffer().load(getEmitBuffer());
            getShader().getSelfEmitBuffer().load(getSelfEmitBuffer());
            getShader().getOpacityBuffer().load(getOpacityBuffer());
            getShader().getHeightBuffer().load(getHeightBuffer());
            getShader().getStartX().load(startLight.x());
            getShader().getStartY().load(startLight.y());
            getShader().getStartZ().load(startLight.z());
            getShader().getSizeX().load(lightSize.x());
            getShader().getSizeY().load(lightSize.y());
            getShader().getSizeZ().load(lightSize.z());
            for (int i = 0; i < MAX_LIGHT_LEVEL; ++i) {
                getShader().compute(lightSize.x() * lightSize.y() * lightSize.z());
            }
            //glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT);
            //glMemoryBarrier(GL_ALL_BARRIER_BITS);
            getShader().stop();
        }
    }

    @Override
    public Model loadModel() {
        return Model.builder().build();
    }

    @Override
    public boolean deleteModelOnFinish() {
        // TODO remove this method
        return true;
    }

    @Override
    public boolean isGraphicalInsideFrustum() {
        return true;
    }

    @Override
    public Class<LightMassShader> getShaderClass() {
        return LightMassShader.class;
    }

    @Override
    public Object getModelKey() {
        return this;
    }


}
