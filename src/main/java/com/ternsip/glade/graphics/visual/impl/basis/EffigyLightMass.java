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

import java.util.Arrays;
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
        int minIndexMinor = Integer.MAX_VALUE;
        int maxIndexMinor = -1;
        int minIndexMajor = Integer.MAX_VALUE;
        int maxIndexMajor = -1;
        int minIndexHeightMinor = Integer.MAX_VALUE;
        int maxIndexHeightMinor = -1;
        int minIndexHeightMajor = Integer.MAX_VALUE;
        int maxIndexHeightMajor = -1;
        int startIndex = (int) INDEXER.getIndexLooping(start.x(), start.y(), start.z());
        int startHeightIndex = (int) INDEXER_XZ.getIndexLooping(start.x(), start.z());
        for (int x = start.x(); x < endExcluding.x(); ++x) {
            for (int z = start.z(); z < endExcluding.z(); ++z) {
                for (int y = start.y(); y < endExcluding.y(); ++y) {
                    int index = (int) INDEXER.getIndexLooping(x, y, z);
                    Block block = getUniverseClient().getBlocksClientRepository().getBlock(x, y, z);
                    selfEmitBuffer.getData()[index] = block.getEmitLight();
                    opacityBuffer.getData()[index] = block.getLightOpacity();
                    if (index < startIndex) {
                        minIndexMinor = Math.min(minIndexMinor, index);
                        maxIndexMinor = Math.max(maxIndexMinor, index);
                    } else {
                        minIndexMajor = Math.min(minIndexMajor, index);
                        maxIndexMajor = Math.max(maxIndexMajor, index);
                    }
                }
                int heightIndex = (int) INDEXER_XZ.getIndexLooping(x, z);
                if (heightBuffer.getData()[heightIndex] <= endExcluding.y()) {
                    int yAir = endExcluding.y() - 1;
                    for (; yAir >= 0; --yAir) {
                        if (getUniverseClient().getBlocksClientRepository().getBlock(x, yAir, z) != Block.AIR) {
                            break;
                        }
                    }
                    heightBuffer.getData()[heightIndex] = yAir + 1;
                }
                if (heightIndex < startHeightIndex) {
                    minIndexHeightMinor = Math.min(minIndexHeightMinor, heightIndex);
                    maxIndexHeightMinor = Math.max(maxIndexHeightMinor, heightIndex);
                } else {
                    minIndexHeightMajor = Math.min(minIndexHeightMajor, heightIndex);
                    maxIndexHeightMajor = Math.max(maxIndexHeightMajor, heightIndex);
                }
            }
        }
        // TODO do not copy every time, update from origin instead using pointers, or use some kind of buffer
        if (maxIndexMinor != -1) {
            selfEmitBuffer.updateSubBuffer(minIndexMinor, Arrays.copyOfRange(selfEmitBuffer.getData(), minIndexMinor, maxIndexMinor + 1));
            opacityBuffer.updateSubBuffer(minIndexMinor, Arrays.copyOfRange(opacityBuffer.getData(), minIndexMinor, maxIndexMinor + 1));
            heightBuffer.updateSubBuffer(minIndexHeightMinor, Arrays.copyOfRange(heightBuffer.getData(), minIndexHeightMinor, maxIndexHeightMinor + 1));
        }
        selfEmitBuffer.updateSubBuffer(minIndexMajor, Arrays.copyOfRange(selfEmitBuffer.getData(), minIndexMajor, maxIndexMajor + 1));
        opacityBuffer.updateSubBuffer(minIndexMajor, Arrays.copyOfRange(opacityBuffer.getData(), minIndexMajor, maxIndexMajor + 1));
        heightBuffer.updateSubBuffer(minIndexHeightMajor, Arrays.copyOfRange(heightBuffer.getData(), minIndexHeightMajor, maxIndexHeightMajor + 1));

    }

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
