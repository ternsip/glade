package com.ternsip.glade.graphics.renderer.impl;

import com.ternsip.glade.graphics.general.Mesh;
import com.ternsip.glade.graphics.renderer.base.Renderer;
import com.ternsip.glade.graphics.shader.base.ShaderProgram;
import com.ternsip.glade.graphics.shader.impl.EntityShader;
import com.ternsip.glade.universe.common.Camera;
import com.ternsip.glade.universe.entities.base.Entity;
import org.joml.*;

import java.lang.Math;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ternsip.glade.Glade.UNIVERSE;

@SuppressWarnings("unused")
// TODO The speed can potentially be increased by allocating shader to each unique model (less number of re-writes)
// TODO DRAW OPAQUE first prior
public class EntityRenderer implements Renderer {

    private EntityShader shader = ShaderProgram.createShader(EntityShader.class);

    public void render() {
        Vector3fc camPos = UNIVERSE.getCamera().getPosition();
        Matrix4fc projection = UNIVERSE.getCamera().getEntityProjectionMatrix();
        Matrix4fc view = UNIVERSE.getCamera().getFullViewMatrix();
        Matrix4fc projectionViewMatrix = projection.mul(view, new Matrix4f());
        Vector3f sunDirection = UNIVERSE.getSun().getPosition().normalize();
        FrustumIntersection frustumIntersection = new FrustumIntersection(projectionViewMatrix);
        shader.start();
        HashMap<Entity, Float> distanceToEntity = UNIVERSE.getEntityRepository()
                .getEntities()
                .stream()
                .filter(e -> isEntityInsideFrustum(frustumIntersection, e))
                .collect(
                        Collectors.toMap(
                                e -> e,
                                e -> e.isFrontal() ? -1 : e.getAdjustedPosition().distanceSquared(camPos),
                                (a, b) -> a,
                                HashMap::new
                        )
                );
        distanceToEntity.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEach(k -> render(k.getKey()));
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
        Camera camera = UNIVERSE.getCamera();
        Matrix4fc projection = entity.isSprite() ? camera.getSpriteProjectionMatrix() : camera.getEntityProjectionMatrix();
        Matrix4fc view = entity.isSprite() ? new Matrix4f() : camera.getFullViewMatrix();
        shader.getAnimated().load(boneTransforms.length > 0);
        shader.getProjectionMatrix().load(projection);
        shader.getViewMatrix().load(view);
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
        if (entity.isFrontal()) {
            return true;
        }
        Vector3fc scale = entity.getAdjustedScale();
        float delta = Math.max(Math.max(scale.x(), scale.y()), scale.z()) * 1.5f;
        return frustumIntersection.testSphere(entity.getAdjustedPosition(), delta);
    }

    private long getUpdateIntervalMilliseconds(Entity entity) {
        Vector3fc scale = entity.getAdjustedScale();
        float maxScale = Math.max(Math.max(scale.x(), scale.y()), scale.z());
        double criterion = (UNIVERSE.getCamera().getPosition().distance(entity.getAdjustedPosition()) / maxScale) / 10;
        return (long) (criterion * criterion * criterion);
    }

}
