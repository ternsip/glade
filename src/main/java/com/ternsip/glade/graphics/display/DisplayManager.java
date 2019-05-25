package com.ternsip.glade.graphics.display;

import com.ternsip.glade.graphics.general.TextureRepository;
import lombok.Getter;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.Callback;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.system.MemoryUtil.NULL;

@Component
@Getter
public class DisplayManager {

    public static final Vector3f BACKGROUND_COLOR = new Vector3f(1f, 0f, 0f);
    private static final int FPS_CAP = 120;
    public static DisplayManager INSTANCE;
    private ArrayList<Callback> callbacks = new ArrayList<>();
    private TextureRepository textureRepository;
    private DisplayEvents displayEvents = new DisplayEvents();
    private long lastFrameTime;
    private float deltaTime;
    private float fps;
    private long window;
    private Vector2i windowSize;

    public DisplayManager() {
        INSTANCE = this;
        displayEvents.getErrorCallbacks().add((e, d) -> GLFWErrorCallback.createPrint(System.err).invoke(e, d));
        displayEvents.getResizeCallbacks().add(this::handleResize);
        // TODO MOVE IN HOTKEY CLASS
        displayEvents.getKeyCallbacks().add((key, scanCode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                close();
            }
        });
        registerErrorCallback();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        Vector2i mainDisplaySize = getMainDisplaySize();
        windowSize = new Vector2i((int) (mainDisplaySize.x() * 0.8), (int) (mainDisplaySize.y() * 0.8));
        window = glfwCreateWindow(windowSize.x(), windowSize.y(), "Glade", NULL, NULL);
        if (window == NULL) {
            glfwTerminate();
            throw new RuntimeException("Failed to create the GLFW window");
        }
        registerScrollCallback();
        registerCursorPosCallback();
        registerKeyCallback();
        registerFrameBufferSizeCallback();
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
        glClearColor(BACKGROUND_COLOR.x(), BACKGROUND_COLOR.y(), BACKGROUND_COLOR.z(), 1);

        handleResize(getWidth(), getHeight());

        textureRepository = new TextureRepository();
        textureRepository.bind();

        lastFrameTime = getCurrentTime();
    }

    private void handleResize(int width, int height) {
        windowSize = new Vector2i(width, height);
        glViewport(0, 0, getWidth(), getHeight());
    }

    public void loop(Runnable runnable) {
        /* Loop until window gets closed */
        while (isWindowActive()) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            runnable.run();

            // Calc fps
            long currentFrameTime = getCurrentTime();
            deltaTime = (currentFrameTime - lastFrameTime) / 1000f;
            fps = 1 / deltaTime;
            lastFrameTime = currentFrameTime;
            //sSystem.out.println(deltaTime);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    public boolean isWindowActive() {
        return !glfwWindowShouldClose(window);
    }

    public void finish() {

        textureRepository.unbind();
        textureRepository.finish();

        // Release window
        glfwDestroyWindow(window);

        // Release all callbacks
        for (Callback callback : callbacks) {
            callback.free();
        }

        // Terminate GLFW
        glfwTerminate();
    }

    public int getWidth() {
        return windowSize.x();
    }

    public int getHeight() {
        return windowSize.y();
    }

    public boolean isKeyDown(int key) {
        return glfwGetKey(window, key) == GLFW_PRESS;
    }

    public boolean isMouseDown(int key) {
        return glfwGetMouseButton(window, key) == GLFW_PRESS;
    }

    private void registerScrollCallback() {
        GLFWScrollCallback scrollCallback = GLFWScrollCallback.create(
                (window, xOffset, yOffset) -> getDisplayEvents().getScrollCallbacks().forEach(e -> e.apply(xOffset, yOffset))
        );
        callbacks.add(scrollCallback);
        glfwSetScrollCallback(window, scrollCallback);
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
                getDisplayEvents().getCursorPosCallbacks().forEach(e -> e.apply(xPos, yPos, dx, dy));
            }
        }));
        callbacks.add(posCallback);
        glfwSetCursorPosCallback(window, posCallback);
    }

    private void registerKeyCallback() {
        GLFWKeyCallback keyCallback = GLFWKeyCallback.create(
                (window, key, scanCode, action, mods) -> getDisplayEvents().getKeyCallbacks().forEach(e -> e.apply(key, scanCode, action, mods))
        );
        callbacks.add(keyCallback);
        glfwSetKeyCallback(window, keyCallback);
    }

    private void registerErrorCallback() {
        GLFWErrorCallback errorCallback = GLFWErrorCallback.create(
                (error, description) -> getDisplayEvents().getErrorCallbacks().forEach(e -> e.apply(error, description))
        );
        callbacks.add(errorCallback);
        glfwSetErrorCallback(errorCallback);
    }

    private void registerFrameBufferSizeCallback() {
        GLFWFramebufferSizeCallback framebufferSizeCallback = GLFWFramebufferSizeCallback.create(
                (window, width, height) -> getDisplayEvents().getResizeCallbacks().forEach(e -> e.apply(width, height))
        );
        callbacks.add(framebufferSizeCallback);
        glfwSetFramebufferSizeCallback(window, framebufferSizeCallback);
    }

    public void close() {
        glfwSetWindowShouldClose(window, true);
    }

    public Vector2i getMainDisplaySize() {
        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        return new Vector2i(vidMode.width(), vidMode.height());
    }

    private long getCurrentTime() {
        return System.currentTimeMillis();
    }

}
