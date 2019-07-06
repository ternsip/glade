package com.ternsip.glade.graphics.display;

import com.ternsip.glade.common.events.base.ErrorEvent;
import com.ternsip.glade.common.events.base.Event;
import com.ternsip.glade.common.events.display.*;
import com.ternsip.glade.universe.common.Universal;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.Callback;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_SAMPLE_ALPHA_TO_COVERAGE;
import static org.lwjgl.opengl.GL13C.GL_MULTISAMPLE;
import static org.lwjgl.system.MemoryUtil.NULL;

@Getter
@Setter
public class WindowData implements Universal, Graphical {

    public static final Vector4fc BACKGROUND_COLOR = new Vector4f(1f, 0f, 0f, 1f);

    private final ArrayList<Callback> callbacks = new ArrayList<>();
    private final FpsCounter fpsCounter = new FpsCounter();
    private final long window;
    private Vector2i windowSize;

    public WindowData() {

        getGraphics().getEventSnapReceiver().registerCallback(ErrorEvent.class, this::handleError);

        registerErrorEvent();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        Vector2i mainDisplaySize = getMainDisplaySize();
        this.windowSize = new Vector2i((int) (mainDisplaySize.x() * 0.8), (int) (mainDisplaySize.y() * 0.8));
        this.window = glfwCreateWindow(windowSize.x(), windowSize.y(), "Glade", NULL, NULL);
        if (window == NULL) {
            glfwTerminate();
            throw new RuntimeException("Failed to create the GLFW window");
        }

        registerScrollEvent();
        registerCursorPosEvent();
        registerKeyEvent();
        registerFrameBufferSizeEvent();
        registerMouseButtonEvent();

        glfwSetWindowPos(window, (int) (mainDisplaySize.x() * 0.1), (int) (mainDisplaySize.y() * 0.1));

        // Create OpenGL context
        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        // Disable vertical synchronization
        glfwSwapInterval(0);

        //glEnable(GL_BLEND);
        //glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        OpenGlSettings.antialias(true);
        OpenGlSettings.enableDepthTesting(true);
        OpenGlSettings.goWireframe(false);

        //glEnable(GL_DEPTH_TEST);
        glEnable(GL_SAMPLE_ALPHA_TO_COVERAGE);
        glEnable(GL_MULTISAMPLE);
        //glEnable(GL_CULL_FACE);
        glClearColor(BACKGROUND_COLOR.x(), BACKGROUND_COLOR.y(), BACKGROUND_COLOR.z(), BACKGROUND_COLOR.w());

        registerDisplayEvent(ResizeEvent.class, new ResizeEvent(getWidth(), getHeight()));

        getGraphics().getEventSnapReceiver().registerCallback(ResizeEvent.class, this::handleResize);
        // TODO MOVE IN HOTKEY CLASS
        getGraphics().getEventSnapReceiver().registerCallback(KeyEvent.class, e -> {
            if (e.getKey() == GLFW_KEY_ESCAPE && e.getAction() == GLFW_PRESS) {
                close();
            }
        });
    }

    public int getWidth() {
        return getWindowSize().x();
    }

    public int getHeight() {
        return getWindowSize().y();
    }

    public float getRatio() {
        return getWidth() / (float)getHeight();
    }

    public boolean isActive() {
        return !glfwWindowShouldClose(getWindow());
    }

    public void close() {
        glfwSetWindowShouldClose(getWindow(), true);
    }

    public void finish() {
        glfwDestroyWindow(getWindow());
        for (Callback callback : getCallbacks()) {
            callback.free();
        }
        glfwTerminate();
    }

    public void swapBuffers() {
        glfwSwapBuffers(getWindow());
    }

    public void pollEvents() {
        glfwPollEvents();
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void enableCursor() {
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
    }

    public void disableCursor() {
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }

    private Vector2i getMainDisplaySize() {
        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        return new Vector2i(vidMode.width(), vidMode.height());
    }

    private void registerErrorEvent() {
        GLFWErrorCallback errorCallback = GLFWErrorCallback.create(
                (error, description) -> registerDisplayEvent(ErrorEvent.class, new ErrorEvent(error, description))
        );
        getCallbacks().add(errorCallback);
        glfwSetErrorCallback(errorCallback);
    }

    private void registerScrollEvent() {
        GLFWScrollCallback scrollCallback = GLFWScrollCallback.create(
                (window, xOffset, yOffset) -> registerDisplayEvent(ScrollEvent.class, new ScrollEvent(xOffset, yOffset))
        );
        getCallbacks().add(scrollCallback);
        glfwSetScrollCallback(getWindow(), scrollCallback);
    }

    private void registerCursorPosEvent() {
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
                registerDisplayEvent(CursorPosEvent.class, new CursorPosEvent(xPos, yPos, dx, dy));
            }
        }));
        getCallbacks().add(posCallback);
        glfwSetCursorPosCallback(getWindow(), posCallback);
    }

    private void registerKeyEvent() {
        GLFWKeyCallback keyCallback = GLFWKeyCallback.create(
                (window, key, scanCode, action, mods) -> registerDisplayEvent(KeyEvent.class, new KeyEvent(key, scanCode, action, mods))
        );
        getCallbacks().add(keyCallback);
        glfwSetKeyCallback(getWindow(), keyCallback);
    }

    private void registerFrameBufferSizeEvent() {
        GLFWFramebufferSizeCallback framebufferSizeCallback = GLFWFramebufferSizeCallback.create(
                (window, width, height) -> registerDisplayEvent(ResizeEvent.class, new ResizeEvent(width, height))
        );
        getCallbacks().add(framebufferSizeCallback);
        glfwSetFramebufferSizeCallback(getWindow(), framebufferSizeCallback);
    }

    private void registerMouseButtonEvent() {
        GLFWMouseButtonCallback mouseButtonCallback = GLFWMouseButtonCallback.create(
                (window, button, action, mods) -> registerDisplayEvent(MouseButtonEvent.class, new MouseButtonEvent(button, action, mods))
        );
        getCallbacks().add(mouseButtonCallback);
        glfwSetMouseButtonCallback(getWindow(), mouseButtonCallback);
    }

    private <T extends Event> void registerDisplayEvent(Class<T> clazz, T event) {
        getGraphics().getEventSnapReceiver().registerEvent(clazz, event);
        getUniverse().getEventSnapReceiver().registerEvent(clazz, event);
    }

    private void handleError(ErrorEvent errorEvent) {
        GLFWErrorCallback.createPrint(System.err).invoke(errorEvent.getError(), errorEvent.getDescription());
    }

    private void handleResize(ResizeEvent resizeEvent) {
        setWindowSize(new Vector2i(resizeEvent.getWidth(), resizeEvent.getHeight()));
        glViewport(0, 0, getWidth(), getHeight());
    }

}
