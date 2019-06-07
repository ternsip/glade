package com.ternsip.glade.universe.graphicals.base;

import com.ternsip.glade.graphics.general.Animation;
import com.ternsip.glade.graphics.general.Mesh;
import com.ternsip.glade.graphics.shader.impl.AnimationShader;
import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;

import static com.ternsip.glade.Glade.DISPLAY_MANAGER;
import static com.ternsip.glade.Glade.UNIVERSE;

@Getter
public abstract class GraphicalAnimated extends Graphical<AnimationShader> {

    @Getter(lazy = true)
    private final Animation animation = new Animation(getModel());

    @Override
    public void render() {
        getShader().start();
        getAnimation().update(getUpdateIntervalMilliseconds());
        Matrix4f[] boneTransforms = getAnimation().getBoneTransforms();
        Matrix4fc projection = getProjectionMatrix();
        Matrix4fc view = getViewMatrix();
        getShader().getAnimated().load(boneTransforms.length > 0);
        getShader().getProjectionMatrix().load(projection);
        getShader().getViewMatrix().load(view);
        getShader().getLightDirection().load(UNIVERSE.getSun().getPosition().normalize());
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

    private long getUpdateIntervalMilliseconds() {
        Vector3fc scale = getAdjustedScale();
        float maxScale = Math.max(Math.max(scale.x(), scale.y()), scale.z());
        double criterion = (DISPLAY_MANAGER.getCamera().getPosition().distance(getAdjustedPosition()) / maxScale) / 10;
        return (long) (criterion * criterion * criterion);
    }

}
