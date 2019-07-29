package com.ternsip.glade.graphics.display;

import com.ternsip.glade.common.events.base.EventSnapReceiver;
import com.ternsip.glade.graphics.camera.Camera;
import com.ternsip.glade.graphics.camera.CameraController;
import com.ternsip.glade.graphics.camera.UniversalCameraController;
import com.ternsip.glade.graphics.visual.repository.*;
import com.ternsip.glade.universe.common.Universal;
import lombok.Getter;

public class Graphics implements Universal {

    @Getter
    private final Thread rootThread = Thread.currentThread();

    @Getter
    private final EventSnapReceiver eventSnapReceiver = new EventSnapReceiver();

    @Getter(lazy = true)
    private final WindowData windowData = new WindowData();

    @Getter(lazy = true)
    private final FrameBuffers frameBuffers = new FrameBuffers();

    @Getter(lazy = true)
    private final TextureRepository textureRepository = new TextureRepository();

    @Getter(lazy = true)
    private final ModelRepository modelRepository = new ModelRepository();

    @Getter(lazy = true)
    private final ShaderRepository shaderRepository = new ShaderRepository();

    @Getter(lazy = true)
    private final Camera camera = new Camera();

    @Getter(lazy = true)
    private final CameraController cameraController = new UniversalCameraController();

    @Getter(lazy = true)
    private final EffigyRepository effigyRepository = new EffigyRepository();

    @Getter(lazy = true)
    private final TexturePackRepository texturePackRepository = new TexturePackRepository();

    @Getter(lazy = true)
    private final AudioRepository audioRepository = new AudioRepository();

    public void run() {
        loop();
        finish();
    }

    public void loop() {
        while (getWindowData().isActive() && UNIVERSE_THREAD.isActive()) {
            getFrameBuffers().bindBuffer();
            getWindowData().clear();
            getEventSnapReceiver().update();
            getEffigyRepository().render();
            getWindowData().getFpsCounter().updateFps();
            getFrameBuffers().resolveBuffer();
            getWindowData().swapBuffers();
            getWindowData().pollEvents();
            getAudioRepository().update();
        }
    }

    public void finish() {
        getUniverse().getEventSnapReceiver().getApplicationActive().set(false);
        getModelRepository().finish();
        getShaderRepository().finish();
        getTextureRepository().finish();
        getWindowData().finish();
        getAudioRepository().finish();
    }

    public void checkThreadSafety() {
        if (Thread.currentThread() != getRootThread()) {
            throw new IllegalArgumentException("It is not thread safe to get display not from the main thread");
        }
    }

}
