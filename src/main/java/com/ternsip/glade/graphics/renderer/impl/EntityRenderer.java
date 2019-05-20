package com.ternsip.glade.graphics.renderer.impl;

import com.ternsip.glade.graphics.general.Mesh;
import com.ternsip.glade.graphics.renderer.base.Renderer;
import com.ternsip.glade.graphics.shader.base.ShaderProgram;
import com.ternsip.glade.graphics.shader.impl.EntityShader;
import com.ternsip.glade.universe.entities.base.Entity;
import org.joml.*;

import java.lang.Math;

import static com.ternsip.glade.Glade.UNIVERSE;

@SuppressWarnings("unused")
// TODO The speed can potentially be increased by allocating shader to each unique model (less number of re-writes)
// TODO DRAW OPAQUE first prior
public class EntityRenderer implements Renderer {

    private EntityShader shader = ShaderProgram.createShader(EntityShader.class);

    public void render() {
        Vector3fc camPos = UNIVERSE.getCamera().getPosition();
        Matrix4fc projection = UNIVERSE.getCamera().getEntityProjectionMatrix();
        Matrix4fc view = UNIVERSE.getCamera().createViewMatrix();
        Matrix4fc projectionViewMatrix = projection.mul(view, new Matrix4f());
        Vector3f sunDirection = UNIVERSE.getSun().getPosition().normalize();
        FrustumIntersection frustumIntersection = new FrustumIntersection(projectionViewMatrix);
        shader.start();
        UNIVERSE.getEntityRepository()
                .getEntities()
                .stream()
                .filter(e -> isEntityInsideFrustum(frustumIntersection, e))
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
        shader.getProjectionMatrix().load(UNIVERSE.getCamera().getEntityProjectionMatrix());
        shader.getViewMatrix().load(UNIVERSE.getCamera().createViewMatrix());
        shader.getLightDirection().load(UNIVERSE.getSun().getPosition().normalize());
        shader.getBoneTransforms().load(boneTransforms);
        shader.getTransformationMatrix().load(entity.getTransformationMatrix());
        for (Mesh mesh : entity.getAnimator().getModel().getMeshes()) {
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

    private boolean isEntityInsideFrustum(FrustumIntersection frustumIntersection, Entity entity) {
        Vector3fc scale = entity.getAdjustedScale();
        float delta = Math.max(Math.max(scale.x(), scale.y()), scale.z());
        return frustumIntersection.testSphere(entity.getPosition(), delta);
    }

    private long getUpdateIntervalMilliseconds(Entity entity) {
        Vector3fc scale = entity.getAdjustedScale();
        float maxScale = Math.max(Math.max(scale.x(), scale.y()), scale.z());
        double criterion = (UNIVERSE.getCamera().getPosition().distance(entity.getPosition()) / maxScale) / 10;
        return (long) (criterion * criterion * criterion);
    }

}
