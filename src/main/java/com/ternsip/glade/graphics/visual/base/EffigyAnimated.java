package com.ternsip.glade.graphics.visual.base;

import com.ternsip.glade.graphics.general.Animation;
import com.ternsip.glade.graphics.general.Mesh;
import com.ternsip.glade.graphics.shader.impl.AnimationShader;
import com.ternsip.glade.universe.common.Light;
import com.ternsip.glade.universe.entities.impl.EntitySun;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

@Getter
@Setter
public abstract class EffigyAnimated extends Effigy<AnimationShader> {

    @Getter(lazy = true)
    private final Animation animation = new Animation(getModel());

    @Override
    public void render() {
        getShader().start();
        updateAnimation();
        Matrix4f[] boneTransforms = getAnimation().getBoneTransforms();
        Matrix4fc projection = getProjectionMatrix();
        Matrix4fc view = getViewMatrix();
        getShader().getAnimated().load(getAnimation().isAnimated());
        getShader().getProjectionMatrix().load(projection);
        getShader().getViewMatrix().load(view);
        getShader().getSun().load(getSun());
        getShader().getBoneTransforms().load(boneTransforms);
        getShader().getTransformationMatrix().load(getTransformationMatrix());
        for (Mesh mesh : getAnimation().getModel().getMeshes()) {
            getShader().getDiffuseMap().load(mesh.getMaterial().getDiffuseMap());
            getShader().getSpecularMap().load(mesh.getMaterial().getSpecularMap());
            getShader().getAmbientMap().load(mesh.getMaterial().getAmbientMap());
            getShader().getEmissiveMap().load(mesh.getMaterial().getEmissiveMap());
            getShader().getHeightMap().load(mesh.getMaterial().getHeightMap());
            getShader().getNormalsMap().load(mesh.getMaterial().getNormalsMap());
            getShader().getShininessMap().load(mesh.getMaterial().getShininessMap());
            getShader().getOpacityMap().load(mesh.getMaterial().getOpacityMap());
            getShader().getDisplacementMap().load(mesh.getMaterial().getDisplacementMap());
            getShader().getLightMap().load(mesh.getMaterial().getLightMap());
            getShader().getReflectionMap().load(mesh.getMaterial().getReflectionMap());
            mesh.render();
        }
        getShader().stop();
    }

    @Override
    public Class<AnimationShader> getShaderClass() {
        return AnimationShader.class;
    }

    public Light getSun() {
        EntitySun sun = getUniverseClient().getEntityClientRepository().getEntityByClass(EntitySun.class);
        return new LightSource(sun.getPositionInterpolated(), sun.getColor(), sun.getIntensity() * getSkyIntensity());
    }

    public float getSkyIntensity() {
        return 1;
    }

    private void updateAnimation() {
        getAnimation().update();
    }

}
