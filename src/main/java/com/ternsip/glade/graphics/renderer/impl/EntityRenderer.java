package com.ternsip.glade.graphics.renderer.impl;

import com.ternsip.glade.graphics.general.Mesh;
import com.ternsip.glade.graphics.renderer.base.Renderer;
import com.ternsip.glade.graphics.shader.base.ShaderProgram;
import com.ternsip.glade.graphics.shader.impl.EntityShader;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.utils.Maths;
import org.joml.*;

import java.lang.Math;

import static com.ternsip.glade.Glade.UNIVERSE;

@SuppressWarnings("unused")
public class EntityRenderer implements Renderer {

    private EntityShader shader = ShaderProgram.createShader(EntityShader.class);

    public void render() {
        Vector3fc camPos = UNIVERSE.getCamera().getPosition();
        Matrix4fc projectionViewMatrix = UNIVERSE.getCamera().getProjectionViewMatrix();
        shader.start();
        UNIVERSE.getEntityRepository()
                .getEntities()
                .stream()
                .filter(e -> isEntityInsideFrustum(projectionViewMatrix, e))
                .sorted((o1, o2) -> Float.compare(o2.getPosition().distanceSquared(camPos), o1.getPosition().distanceSquared(camPos)))
                .forEach(this::render);
        shader.stop();
    }

    public void finish() {
        shader.finish();
    }

    @Override
    public int getPriority() {
        return 1;
    }

    private void render(Entity entity) {
        entity.getAnimator().update(getUpdateIntervalMilliseconds(entity));
        Matrix4f[] boneTransforms = entity.getAnimator().getBoneTransforms();
        shader.getAnimated().load(boneTransforms.length > 0);
        shader.getProjectionMatrix().load(UNIVERSE.getCamera().getProjectionMatrix());
        shader.getViewMatrix().load(UNIVERSE.getCamera().createViewMatrix());
        shader.getLightDirection().load(UNIVERSE.getSun().getPosition().normalize().negate());
        shader.getBoneTransforms().load(boneTransforms);
        shader.getTransformationMatrix().load(entity.getTransformationMatrix());
        for (Mesh mesh : entity.getAnimator().getModel().getMeshes()) {
            shader.getTextureMap().load(mesh.getMaterial().getTextureMap());
            shader.getDiffuseMap().load(mesh.getMaterial().getDiffuseMap());
            shader.getSpecularMap().load(mesh.getMaterial().getSpecularMap());
            shader.getAmbientMap().load(mesh.getMaterial().getAmbientMap());
            shader.getEmissiveMap().load(mesh.getMaterial().getEmissiveMap());
            shader.getHeightMap().load(mesh.getMaterial().getHeightMap());
            shader.getNormalsMap().load(mesh.getMaterial().getNormalsMap());
            shader.getShininessMap().load(mesh.getMaterial().getShininessMap());
            shader.getOpacityMap().load(mesh.getMaterial().getOpacityMap());
            shader.getDisplacementMap().load(mesh.getMaterial().getDisplacementMap());
            shader.getLightMap().load(mesh.getMaterial().getLightMap());
            shader.getReflectionMap().load(mesh.getMaterial().getReflectionMap());
            mesh.render();
        }
    }

    private boolean isEntityInsideFrustum(Matrix4fc projectionViewMatrix, Entity entity) {
        float d = 20.25f; // TODO make it dependent on entity size
        Vector4fc pClip = Maths.mul(projectionViewMatrix, new Vector4f(entity.getPosition(), 1));
        return Math.abs(pClip.x()) < (pClip.w() + d) &&
                Math.abs(pClip.y()) < (pClip.w() + d) &&
                -d < pClip.z() &&
                pClip.z() < (pClip.w() + d);
    }

    private long getUpdateIntervalMilliseconds(Entity entity) {
        float maxScale = Math.max(Math.max(entity.getScale().x(), entity.getScale().y()), entity.getScale().z());
        double criterion = (UNIVERSE.getCamera().getPosition().distance(entity.getPosition()) / maxScale) / 150;
        return (long) (criterion * criterion);
    }

}
