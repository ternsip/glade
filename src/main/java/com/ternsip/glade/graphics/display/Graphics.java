package com.ternsip.glade.graphics.display;

import com.ternsip.glade.common.DisplayCallbacks;
import com.ternsip.glade.common.DisplaySnapReceiver;
import com.ternsip.glade.graphics.visual.repository.GraphicalRepository;
import com.ternsip.glade.universe.common.Universal;
import lombok.Getter;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.Callback;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.system.MemoryUtil.NULL;

@Getter
public class Graphics implements Universal {

    public static final Vector4fc BACKGROUND_COLOR  = new Vector4f(1f, 0f, 0f, 1f);

    private final Thread rootThread;
    private final ArrayList<Callback> callbacks;
    private final DisplayCallbacks displayCallbacks;
    private final WindowData windowData;

    @Getter(lazy = true)
    private final GraphicalRepository graphicalRepository = new GraphicalRepository();

    public Graphics() {
        rootThread = Thread.currentThread();
        callbacks = new ArrayList<>();
        displayCallbacks = new DisplayCallbacks();
        displayCallbacks.getErrorCallbacks().add((e, d) -> GLFWErrorCallback.createPrint(System.err).invoke(e, d));
        displayCallbacks.getResizeCallbacks().add(this::handleResize);
        // TODO MOVE IN HOTKEY CLASS
        displayCallbacks.getKeyCallbacks().add((key, scanCode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                close();
            }
        });
        registerErrorCallback();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        Vector2i mainDisplaySize = getMainDisplaySize();
        Vector2i windowSize = new Vector2i((int) (mainDisplaySize.x() * 0.8), (int) (mainDisplaySize.y() * 0.8));
        long window = glfwCreateWindow(windowSize.x(), windowSize.y(), "Glade", NULL, NULL);
        if (window == NULL) {
            glfwTerminate();
            throw new RuntimeException("Failed to create the GLFW window");
        }
        windowData = new WindowData(window, windowSize);
        registerScrollCallback();
        registerCursorPosCallback();
        registerKeyCallback();
        registerFrameBufferSizeCallback();
        registerMouseButtonCallback();

        registerDisplaySnapCollectorEvents();

        glfwSetWindowPos(window, (int) (mainDisplaySize.x() * 0.1), (int) (mainDisplaySize.y() * 0.1));

        // Create OpenGL context
        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        // Disable vertical synchronization
        glfwSwapInterval(0);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        OpenGlSettings.antialias(true);
        OpenGlSettings.enableDepthTesting(true);
        OpenGlSettings.goWireframe(false);

        glEnable(GL_DEPTH_TEST);
        //glEnable(GL_SAMPLE_ALPHA_TO_COVERAGE);
        glEnable(GL_MULTISAMPLE);
        //glEnable(GL_CULL_FACE);
        final Vector4fc BACKGROUND_COLOR  = new Vector4f(1f, 0f, 0f, 1f);
        glClearColor(BACKGROUND_COLOR.x(), BACKGROUND_COLOR.y(), BACKGROUND_COLOR.z(), BACKGROUND_COLOR.w());

        handleResize(getWindowData().getWidth(), getWindowData().getHeight());

    }

    private void handleResize(int width, int height) {
        getWindowData().setWindowSize(new Vector2i(width, height));
        glViewport(0, 0, getWindowData().getWidth(), getWindowData().getHeight());
    }

    public void loop() {
        while (isActive()) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            getGraphicalRepository().render();
            getWindowData().update();
            glfwSwapBuffers(getWindowData().getWindow());
            glfwPollEvents();
        }
    }

    public boolean isActive() {
        return !glfwWindowShouldClose(getWindowData().getWindow());
    }

    public void finish() {
        getUniverse().getDisplaySnapReceiver().getApplicationActive().set(false);
        getGraphicalRepository().finish();
        glfwDestroyWindow(getWindowData().getWindow());
        for (Callback callback : callbacks) {
            callback.free();
        }
        glfwTerminate();
    }

    public boolean isKeyDown(int key) {
        return glfwGetKey(getWindowData().getWindow(), key) == GLFW_PRESS;
    }

    public boolean isMouseDown(int key) {
        return glfwGetMouseButton(getWindowData().getWindow(), key) == GLFW_PRESS;
    }

    public void checkThreadSafety() {
        if (Thread.currentThread() != getRootThread()) {
            throw new IllegalArgumentException("It is not thread safe to get display not from the main thread");
        }
    }

    private void registerDisplaySnapCollectorEvents() {
        getDisplayCallbacks().getCursorPosCallbacks().add((x, y, dx, dy) ->
                getUniverse().getDisplaySnapReceiver().getCursorPosEvents().add(new DisplaySnapReceiver.CursorPosEvent(x, y, dx, dy))
        );
        getDisplayCallbacks().getResizeCallbacks().add((w, h) ->
                getUniverse().getDisplaySnapReceiver().getResizeEvents().add(new DisplaySnapReceiver.ResizeEvent(w, h))
        );
        getDisplayCallbacks().getScrollCallbacks().add((dx, dy) ->
                getUniverse().getDisplaySnapReceiver().getScrollEvents().add(new DisplaySnapReceiver.ScrollEvent(dx, dy))
        );
        getDisplayCallbacks().getKeyCallbacks().add((k, c, a, m) ->
                getUniverse().getDisplaySnapReceiver().getKeyEvents().add(new DisplaySnapReceiver.KeyEvent(k, c, a, m))
        );
        getDisplayCallbacks().getMouseButtonCallbacks().add((b, a, m) ->
                getUniverse().getDisplaySnapReceiver().getMouseButtonEvents().add(new DisplaySnapReceiver.MouseButtonEvent(b, a, m))
        );
    }

    private void registerScrollCallback() {
        GLFWScrollCallback scrollCallback = GLFWScrollCallback.create(
                (window, xOffset, yOffset) -> getDisplayCallbacks().getScrollCallbacks().forEach(e -> e.apply(xOffset, yOffset))
        );
        getCallbacks().add(scrollCallback);
        glfwSetScrollCallback(getWindowData().getWindow(), scrollCallback);
    }

    private void registerCursorPosCallback() {
        GLFWCursorPosCallback posCallback = GLFWCursorPosCallback.create((new GLFWCursorPosCallbackI() {
            private float dx;
            private float dy;
            private float prevX;
            private float prevY;

            @Override
            public void invoke(long window, double xPos, double yPos) {
                dx = (float) (xPos - prevX);
                dy = (float) (yPos - prevY);
                prevX = (float) xPos;
                prevY = (float) yPos;
                getDisplayCallbacks().getCursorPosCallbacks().forEach(e -> e.apply(xPos, yPos, dx, dy));
            }
        }));
        callbacks.add(posCallback);
        glfwSetCursorPosCallback(getWindowData().getWindow(), posCallback);
    }

    private void registerKeyCallback() {
        GLFWKeyCallback keyCallback = GLFWKeyCallback.create(
                (window, key, scanCode, action, mods) -> getDisplayCallbacks().getKeyCallbacks().forEach(e -> e.apply(key, scanCode, action, mods))
        );
        callbacks.add(keyCallback);
        glfwSetKeyCallback(getWindowData().getWindow(), keyCallback);
    }

    private void registerMouseButtonCallback() {
        GLFWMouseButtonCallback mouseButtonCallback = GLFWMouseButtonCallback.create(
                (window, button, action, mods) -> getDisplayCallbacks().getMouseButtonCallbacks().forEach(e -> e.apply(button, action, mods))
        );
        callbacks.add(mouseButtonCallback);
        glfwSetMouseButtonCallback(getWindowData().getWindow(), mouseButtonCallback);
    }

    private void registerErrorCallback() {
        GLFWErrorCallback errorCallback = GLFWErrorCallback.create(
                (error, description) -> getDisplayCallbacks().getErrorCallbacks().forEach(e -> e.apply(error, description))
        );
        callbacks.add(errorCallback);
        glfwSetErrorCallback(errorCallback);
    }

    private void registerFrameBufferSizeCallback() {
        GLFWFramebufferSizeCallback framebufferSizeCallback = GLFWFramebufferSizeCallback.create(
                (window, width, height) -> getDisplayCallbacks().getResizeCallbacks().forEach(e -> e.apply(width, height))
        );
        callbacks.add(framebufferSizeCallback);
        glfwSetFramebufferSizeCallback(getWindowData().getWindow(), framebufferSizeCallback);
    }

    public void close() {
        glfwSetWindowShouldClose(getWindowData().getWindow(), true);
    }

    public Vector2i getMainDisplaySize() {
        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        return new Vector2i(vidMode.width(), vidMode.height());
    }

}
