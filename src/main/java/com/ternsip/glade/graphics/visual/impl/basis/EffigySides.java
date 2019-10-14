package com.ternsip.glade.graphics.visual.impl.basis;

import com.ternsip.glade.common.logic.Maths;
import com.ternsip.glade.common.logic.Timer;
import com.ternsip.glade.graphics.general.Mesh;
import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.shader.impl.ChunkShader;
import com.ternsip.glade.graphics.visual.base.Effigy;
import com.ternsip.glade.graphics.visual.base.LightSource;
import com.ternsip.glade.graphics.visual.base.SideConstructor;
import com.ternsip.glade.universe.common.Light;
import com.ternsip.glade.universe.entities.impl.EntityCameraEffects;
import com.ternsip.glade.universe.entities.impl.EntitySun;
import com.ternsip.glade.universe.parts.chunks.ChangeBlocksRequest;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import static com.ternsip.glade.universe.parts.chunks.BlocksRepository.MAX_LIGHT_LEVEL;

@Getter
@Setter
public class EffigySides extends Effigy<ChunkShader> {

    public static final long TIME_PERIOD_MILLISECONDS = 60_000L;
    public static final float TIME_PERIOD_DIVISOR = 1000f;

    private final SideConstructor sideConstructor = new SideConstructor();
    private final Timer updateTimer = new Timer(3000);
    private boolean dirty = false;

    public void applyBlockUpdate(ChangeBlocksRequest blocksUpdate) {
        getSideConstructor().applyChanges(blocksUpdate);
        setDirty(true);
    }

    @Override
    public Matrix4fc getTransformationMatrix() {
        Matrix4fc rotMatrix = Maths.getRotationQuaternion(getAdjustedRotation()).get(new Matrix4f());
        return new Matrix4f().translate(getAdjustedPosition()).mul(rotMatrix).scale(getAdjustedScale());
    }

    @Override
    public void render() {
        if (getUpdateTimer().isOver()) {
            getUpdateTimer().drop();
            if (isDirty()) {
                getSideConstructor().getSelfEmitBuffer().updateBuffers();
                getSideConstructor().getOpacityBuffer().updateBuffers();
                // Recalculate light
                getShader().startCompute();
                getShader().getSkyBuffer().load(getSideConstructor().getSkyBuffer());
                getShader().getEmitBuffer().load(getSideConstructor().getEmitBuffer());
                getShader().getSelfEmitBuffer().load(getSideConstructor().getSelfEmitBuffer());
                getShader().getOpacityBuffer().load(getSideConstructor().getOpacityBuffer());
                getShader().getHeightBuffer().load(getSideConstructor().getHeightBuffer());
                for (int i = 0; i < MAX_LIGHT_LEVEL; ++i) {
                    getShader().compute(SideConstructor.SIZE_X * SideConstructor.SIZE_Y * SideConstructor.SIZE_Z);
                    //glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT | GL_TEXTURE_FETCH_BARRIER_BIT);
                }
                getShader().stop();
                setDirty(false);
            }
        }
        getShader().startRaster();
        getShader().getSkyBuffer().load(getSideConstructor().getSkyBuffer());
        getShader().getEmitBuffer().load(getSideConstructor().getEmitBuffer());
        getShader().getProjectionMatrix().load(getProjectionMatrix());
        getShader().getViewMatrix().load(getViewMatrix());
        getShader().getTransformationMatrix().load(getTransformationMatrix());
        getShader().getTime().load((System.currentTimeMillis() % TIME_PERIOD_MILLISECONDS) / TIME_PERIOD_DIVISOR);
        getShader().getSun().load(getSun());
        getShader().getSamplers().loadDefault();
        boolean isUnderwater = getUniverseClient().getEntityClientRepository().getEntityByClass(EntityCameraEffects.class).isUnderWater();
        getShader().getFogColor().load(isUnderwater ? getUniverseClient().getBalance().getUnderwaterFogColor() : getUniverseClient().getBalance().getFogColor());
        getShader().getFogDensity().load(isUnderwater ? getUniverseClient().getBalance().getUnderwaterFogDensity() : getUniverseClient().getBalance().getFogDensity());
        getShader().getFogGradient().load(isUnderwater ? getUniverseClient().getBalance().getUnderwaterFogGradient() : getUniverseClient().getBalance().getFogGradient());
        for (Mesh mesh : getSideConstructor().getMeshes()) {
            mesh.render();
        }
        getShader().stop();
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
    public boolean deleteShaderOnFinish() {
        return true;
    }

    @Override
    public boolean isGraphicalInsideFrustum() {
        return true;
    }

    @Override
    public Class<ChunkShader> getShaderClass() {
        return ChunkShader.class;
    }

    @Override
    public Object getModelKey() {
        return this;
    }

    public Light getSun() {
        EntitySun sun = getUniverseClient().getEntityClientRepository().getEntityByClass(EntitySun.class);
        return new LightSource(sun.getPositionInterpolated(), sun.getColor(), sun.getIntensity());
    }

    @Override
    public void finish() {
        super.finish();
        getSideConstructor().finish();
    }


}
