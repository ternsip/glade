package com.ternsip.glade.renderer.impl;

import com.ternsip.glade.renderer.base.Renderer;
import com.ternsip.glade.shader.base.ShaderProgram;
import com.ternsip.glade.shader.impl.EntityShader;
import com.ternsip.glade.universal.Mesh;
import com.ternsip.glade.universe.entities.base.Entity;
import com.ternsip.glade.utils.OpenGlUtils;
import org.joml.Matrix4f;

import static com.ternsip.glade.Glade.DISPLAY_MANAGER;
import static com.ternsip.glade.Glade.UNIVERSE;

@SuppressWarnings("unused")
public class EntityRenderer implements Renderer {

    private EntityShader shader = ShaderProgram.createShader(EntityShader.class);

    public void render() {
        UNIVERSE.getEntities().forEach(this::render);
    }

    public void finish() {
        shader.finish();
    }

    private void render(Entity entity) {
        shader.start();
        Matrix4f[] boneTransforms = entity.getAnimator().getBoneTransforms();
        shader.getAnimated().load(boneTransforms.length > 0);
        shader.getProjectionMatrix().load(UNIVERSE.getCamera().getProjectionMatrix());
        shader.getViewMatrix().load(UNIVERSE.getCamera().createViewMatrix());
        shader.getLightDirection().load(UNIVERSE.getSun().getPosition().normalize().negate());
        shader.getBoneTransforms().load(boneTransforms);
        shader.getTransformationMatrix().load(entity.getTransformationMatrix());
        OpenGlUtils.antialias(true); // TODO move to upper level
        OpenGlUtils.disableBlending();
        OpenGlUtils.enableDepthTesting(true);
        DISPLAY_MANAGER.getTextureRepository().bind();
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
        shader.stop();
        entity.getAnimator().update();
    }

}
