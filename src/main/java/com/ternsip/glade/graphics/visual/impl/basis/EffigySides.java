package com.ternsip.glade.graphics.visual.impl.basis;

import com.ternsip.glade.common.logic.Maths;
import com.ternsip.glade.graphics.general.Mesh;
import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.shader.impl.ChunkShader;
import com.ternsip.glade.graphics.visual.base.Effigy;
import com.ternsip.glade.graphics.visual.base.LightSource;
import com.ternsip.glade.graphics.visual.base.SideConstructor;
import com.ternsip.glade.universe.common.Light;
import com.ternsip.glade.universe.entities.impl.EntitySun;
import com.ternsip.glade.universe.parts.chunks.BlocksUpdate;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

@Getter
@Setter
public class EffigySides extends Effigy<ChunkShader> {

    public static final long TIME_PERIOD_MILLISECONDS = 60_000L;
    public static final float TIME_PERIOD_DIVISOR = 1000f;

    private final SideConstructor sideConstructor = new SideConstructor();

    public void applyBlockUpdate(BlocksUpdate blocksUpdate) {
        getSideConstructor().applyChanges(blocksUpdate);
    }

    @Override
    public Matrix4fc getTransformationMatrix() {
        Matrix4fc rotMatrix = Maths.getRotationQuaternion(getAdjustedRotation()).get(new Matrix4f());
        return new Matrix4f().translate(getAdjustedPosition()).mul(rotMatrix).scale(getAdjustedScale());
    }

    @Override
    public void render() {
        getShader().start();
        getShader().getProjectionMatrix().load(getProjectionMatrix());
        getShader().getViewMatrix().load(getViewMatrix());
        getShader().getTransformationMatrix().load(getTransformationMatrix());
        getShader().getTime().load((System.currentTimeMillis() % TIME_PERIOD_MILLISECONDS) / TIME_PERIOD_DIVISOR);
        getShader().getSun().load(getSun());
        getShader().getSamplers().loadDefault();
        for (Mesh mesh : getSideConstructor().getMeshes()) {
            mesh.render();
        }
        getShader().stop();
    }

    @Override
    public Model loadModel() {
        return new Model();
    }

    @Override
    public boolean deleteModelOnFinish() {
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
        EntitySun sun = getUniverse().getEntityClientRepository().getSun();
        return new LightSource(sun.getPositionInterpolated(), sun.getColor(), sun.getIntensity());
    }

}
