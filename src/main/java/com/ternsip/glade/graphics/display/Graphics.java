package com.ternsip.glade.graphics.display;

import com.ternsip.glade.graphics.interfaces.*;
import com.ternsip.glade.universe.interfaces.IUniverseClient;
import lombok.Getter;

/**
 * Provides full control over user Input/Output channels
 * Uses OpenGL/OpenAl under the hood and maybe some other IO-libraries
 * In general words it is graphical representation of the client universe state
 */
@Getter
public class Graphics implements IUniverseClient, IEventSnapReceiver, IFrameBuffers, IWindowData, ITextureRepository,
        IModelRepository, IShaderRepository, ICamera, ICameraController, IEffigyRepository, ITexturePackRepository,
        IAudioRepository {

    private final Thread rootThread = Thread.currentThread();

    public void run() {
        loop();
        finish();
    }

    public void checkThreadSafety() {
        if (Thread.currentThread() != getRootThread()) {
            throw new IllegalArgumentException("It is not thread safe to get display not from the main thread");
        }
    }

    private void loop() {
        while (getWindowData().isActive() && isUniverseClientThreadActive()) {
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

    private void finish() {
        getUniverseClient().getEventSnapReceiver().getApplicationActive().set(false);
        getModelRepository().finish();
        getShaderRepository().finish();
        getTextureRepository().finish();
        getWindowData().finish();
        getAudioRepository().finish();
    }

}
