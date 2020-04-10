package com.ternsip.glade.graphics.visual.impl.basis;

import com.ternsip.glade.common.logic.Indexer;
import com.ternsip.glade.common.logic.Indexer2D;
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

import static com.ternsip.glade.universe.parts.chunks.BlocksRepositoryBase.SIZE;
import static com.ternsip.glade.universe.parts.chunks.BlocksRepositoryBase.SIZE_Y;


@Slf4j
@RequiredArgsConstructor
@Getter
@Setter
public class EffigyLightMass extends Effigy<LightMassShader> {

    public static final int VIEW_AREA_X = 256;
    public static final int VIEW_AREA_Y = SIZE_Y;
    public static final int VIEW_AREA_Z = 256;
    public static final Indexer VISIBILITY_INDEXER = new Indexer(VIEW_AREA_X, SIZE_Y, VIEW_AREA_Z);
    public static final Indexer2D HEIGHTS_INDEXER = new Indexer2D(VIEW_AREA_X, VIEW_AREA_Z);
    public static final byte MAX_LIGHT_LEVEL = 15;

    private final ShaderBuffer skyBuffer = new ShaderBuffer(new int[(int) VISIBILITY_INDEXER.getVolume()]);
    private final ShaderBuffer emitBuffer = new ShaderBuffer(new int[(int) VISIBILITY_INDEXER.getVolume()]);
    private final ShaderBuffer selfEmitBuffer = new ShaderBuffer(new int[(int) VISIBILITY_INDEXER.getVolume()]);
    private final ShaderBuffer opacityBuffer = new ShaderBuffer(new int[(int) VISIBILITY_INDEXER.getVolume()]);
    private final ShaderBuffer heightBuffer = new ShaderBuffer(new int[(int) HEIGHTS_INDEXER.getVolume()]);

    public void updateBuffers(Vector3ic start, Vector3ic size) {
        Vector3ic endExcluding = new Vector3i(start).add(size);
        int minIndexMinor = Integer.MAX_VALUE;
        int maxIndexMinor = -1;
        int minIndexMajor = Integer.MAX_VALUE;
        int maxIndexMajor = -1;
        int minIndexHeightMinor = Integer.MAX_VALUE;
        int maxIndexHeightMinor = -1;
        int minIndexHeightMajor = Integer.MAX_VALUE;
        int maxIndexHeightMajor = -1;
        int startIndex = (int) VISIBILITY_INDEXER.getIndexLooping(start.x(), start.y(), start.z());
        int startHeightIndex = (int) HEIGHTS_INDEXER.getIndexLooping(start.x(), start.z());
        for (int x = start.x(); x < endExcluding.x(); ++x) {
            for (int z = start.z(); z < endExcluding.z(); ++z) {
                for (int y = start.y(); y < endExcluding.y(); ++y) {
                    int index = (int) VISIBILITY_INDEXER.getIndexLooping(x, y, z);
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
                int heightIndex = (int) HEIGHTS_INDEXER.getIndexLooping(x, z);
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
        if (maxIndexMinor != -1) {
            selfEmitBuffer.updateSubBuffer(minIndexMinor, maxIndexMinor + 1 - minIndexMinor);
            opacityBuffer.updateSubBuffer(minIndexMinor, maxIndexMinor + 1 - minIndexMinor);
            heightBuffer.updateSubBuffer(minIndexHeightMinor, maxIndexHeightMinor + 1 - minIndexHeightMinor);
        }
        selfEmitBuffer.updateSubBuffer(minIndexMajor, maxIndexMajor + 1 - minIndexMajor);
        opacityBuffer.updateSubBuffer(minIndexMajor, maxIndexMajor + 1 - minIndexMajor);
        heightBuffer.updateSubBuffer(minIndexHeightMajor, maxIndexHeightMajor + 1 - minIndexHeightMajor);

    }

    @Override
    public void render() {
        if (!getUniverseClient().getBlocksClientRepository().getChangeBlocksRequestsLight().isEmpty()) {
            ChangeBlocksRequest changeBlocksRequest = getUniverseClient().getBlocksClientRepository().getChangeBlocksRequestsLight().poll();
            Vector3ic startLight = new Vector3i(changeBlocksRequest.getStart()).sub(new Vector3i(MAX_LIGHT_LEVEL - 1)).max(new Vector3i(0));
            Vector3ic endLightExcluding = new Vector3i(changeBlocksRequest.getEndExcluding()).add(new Vector3i(MAX_LIGHT_LEVEL - 1)).min(SIZE);
            Vector3ic lightSize = new Vector3i(endLightExcluding).sub(startLight).min(VISIBILITY_INDEXER.getSize());
            updateBuffers(startLight, lightSize);
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
                //glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT);
                //glMemoryBarrier(GL_ALL_BARRIER_BITS);
            }
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

    public void finish() {
        getSkyBuffer().finish();
        getEmitBuffer().finish();
        getSelfEmitBuffer().finish();
        getOpacityBuffer().finish();
        getHeightBuffer().finish();
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
