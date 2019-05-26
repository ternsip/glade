package com.ternsip.glade.graphics.renderer.impl;

import com.ternsip.glade.graphics.renderer.base.Renderer;
import com.ternsip.glade.graphics.shader.base.ShaderProgram;
import com.ternsip.glade.graphics.shader.impl.SkyboxShader;
import com.ternsip.glade.universe.Universe;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;

@SuppressWarnings("unused")
@Getter
@RequiredArgsConstructor
public class SkyRenderer implements Renderer {


    private final SkyboxShader skyboxShader = ShaderProgram.createShader(SkyboxShader.class);

    @PostConstruct
    public void applyProjectionMatrix() {
        skyboxShader.start();
        skyboxShader.getProjectionMatrix().load(getUniverse().getCamera().getSkyProjectionMatrix());
        skyboxShader.stop();
    }

    private Universe getUniverse() {
        return Universe.INSTANCE;
    }

    public void render() {
        skyboxShader.start();
        skyboxShader.getSunVector().load(getUniverse().getFigureSky().getSkyPosition());
        skyboxShader.getViewMatrix().load(getUniverse().getCamera().getSkyViewMatrix());
        skyBox.render();
        skyboxShader.stop();
    }

    public void finish() {
        skyboxShader.finish();
        skyBox.finish();
    }

    @Override
    public int getPriority() {
        return 0;
    }

}
