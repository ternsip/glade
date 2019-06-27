package com.ternsip.glade.graphics.visual.impl.basis;

import com.ternsip.glade.common.logic.Maths;
import com.ternsip.glade.graphics.general.Mesh;
import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.shader.impl.ChunkShader;
import com.ternsip.glade.graphics.visual.base.Effigy;
import com.ternsip.glade.graphics.visual.base.SideConstructor;
import com.ternsip.glade.universe.common.Light;
import com.ternsip.glade.universe.parts.chunks.BlocksUpdate;
import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import java.util.Set;

@Getter
public class EffigySides extends Effigy<ChunkShader> {

    private SideConstructor rigidSides = new SideConstructor();
    private SideConstructor translucentSides = new SideConstructor();
    private SideConstructor waterSides = new SideConstructor();

    public void applyBlockUpdate(BlocksUpdate blocksUpdate) {
        getRigidSides().applyChanges(blocksUpdate.getRigidSides());
        getTranslucentSides().applyChanges(blocksUpdate.getTranslucentSides());
        getWaterSides().applyChanges(blocksUpdate.getWaterSides());
    }

    @Override
    public Matrix4f getTransformationMatrix() {
        Matrix4fc rotMatrix = Maths.getRotationQuaternion(getAdjustedRotation()).get(new Matrix4f());
        return new Matrix4f().translate(getAdjustedPosition()).mul(rotMatrix).scale(getAdjustedScale());
    }

    @Override
    public void render(Set<Light> lights) {
        getShader().start();
        getShader().getProjectionMatrix().load(getProjectionMatrix());
        getShader().getViewMatrix().load(getViewMatrix());
        getShader().getTransformationMatrix().load(getTransformationMatrix());
        loadMeshesToShader(getRigidSides());
        loadMeshesToShader(getTranslucentSides());
        loadMeshesToShader(getWaterSides());
        getShader().stop();
    }

    private void loadMeshesToShader(SideConstructor sideConstructor) {
        for (Mesh mesh : sideConstructor.getMeshes()) {
            getShader().getDiffuseMap().load(mesh.getMaterial().getDiffuseMap());
            getShader().getSpecularMap().load(mesh.getMaterial().getSpecularMap());
            getShader().getAmbientMap().load(mesh.getMaterial().getAmbientMap());
            getShader().getEmissiveMap().load(mesh.getMaterial().getEmissiveMap());
            mesh.render();
        }
    }

    @Override
    public Model loadModel() {
        return new Model();
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public boolean isGraphicalInsideFrustum() {
        return true;
    }

    @Override
    public float getSquaredDistanceToCamera() {
        return 0;
    }

    @Override
    public Class<ChunkShader> getShaderClass() {
        return ChunkShader.class;
    }

    @Override
    public Object getModelKey() {
        return this;
    }

}
