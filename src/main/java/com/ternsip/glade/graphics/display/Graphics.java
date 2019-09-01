package com.ternsip.glade.graphics.display;

import com.ternsip.glade.graphics.interfaces.*;
import com.ternsip.glade.universe.interfaces.Universal;
import lombok.Getter;

@Getter
public class Graphics implements Universal, IEventSnapReceiver, IFrameBuffers, IWindowData, ITextureRepository,
        IModelRepository, IShaderRepository, ICamera, ICameraController, IEffigyRepository, ITexturePackRepository,
        IAudioRepository {

    private final Thread rootThread = Thread.currentThread();

    public void run() {
        loop();
        finish();
    }

    public void loop() {
        while (getWindowData().isActive() && isUniverseThreadActive()) {
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
