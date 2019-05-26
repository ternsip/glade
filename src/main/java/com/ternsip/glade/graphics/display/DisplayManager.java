package com.ternsip.glade.graphics.display;

import com.ternsip.glade.graphics.entities.base.FigureRepository;
import com.ternsip.glade.graphics.general.TextureRepository;
import com.ternsip.glade.graphics.renderer.base.Renderer;
import com.ternsip.glade.universe.Universe;
import com.ternsip.glade.utils.Utils;
import lombok.Getter;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.Callback;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Display Manager initializes only one window and it should be initialized in main thread to stay cross-platform
 */
@Getter
public class DisplayManager {

    public static DisplayManager INSTANCE;

    public static final Vector3f BACKGROUND_COLOR = new Vector3f(1f, 0f, 0f);
    private static final int FPS_CAP = 120;

    private final ArrayList<Callback> callbacks = new ArrayList<>();
    private final TextureRepository textureRepository;
    private final DisplayCallbacks displayCallbacks = new DisplayCallbacks();
    private final DisplaySnapCollector displaySnapCollector = new DisplaySnapCollector();
    private final ModelRepository modelRepository = new ModelRepository();
    private final FigureRepository figureRepository = new FigureRepository();
    private long lastFrameTime;
    private float deltaTime;
    private float fps;
    private final long window;
    private Vector2i windowSize;
    private final List<Renderer> renders;

    public DisplayManager() {
        INSTANCE = this;
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
        registerMouseButtonCallback();

        registerDisplaySnapCollectorEvents();

        glfwSetWindowPos(window, (int) (mainDisplaySize.x() * 0.1), (int) (mainDisplaySize.y() * 0.1));

        // Create OpenGL context
        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        applySettings();

        handleResize(getWidth(), getHeight());

        textureRepository = new TextureRepository();
        textureRepository.bind();

        lastFrameTime = getCurrentTime();

        renders = instantiateRenders();

        loop();
        finish();
    }

    private void registerDisplaySnapCollectorEvents() {
        displayCallbacks.getCursorPosCallbacks().add((x, y, dx, dy) ->
                displaySnapCollector.getCursorPosEvents().add(new DisplaySnapCollector.CursorPosEvent(x, y, dx, dy))
        );
        displayCallbacks.getResizeCallbacks().add((w, h) ->
                displaySnapCollector.getResizeEvents().add(new DisplaySnapCollector.ResizeEvent(w, h))
        );
        displayCallbacks.getScrollCallbacks().add((dx, dy) ->
                displaySnapCollector.getScrollEvents().add(new DisplaySnapCollector.ScrollEvent(dx, dy))
        );
        displayCallbacks.getKeyCallbacks().add((k, c, a, m) ->
                displaySnapCollector.getKeyEvents().add(new DisplaySnapCollector.KeyEvent(k, c, a, m))
        );
    }

    private List<Renderer> instantiateRenders() {
        return new Reflections()
                .getSubTypesOf(Renderer.class)
                .stream()
                .map(Utils::createInstanceSilently)
                .sorted(Comparator.comparing(Renderer::getPriority))
                .collect(Collectors.toList());
    }

    private void applySettings() {
        // Disable vertical synchronization
        glfwSwapInterval(0);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        GLSettings.antialias(true);
        GLSettings.enableDepthTesting(true);
        GLSettings.goWireframe(false);

        glEnable(GL_DEPTH_TEST);
        //glEnable(GL_SAMPLE_ALPHA_TO_COVERAGE);
        glEnable(GL_MULTISAMPLE);
        //glEnable(GL_CULL_FACE);
        glClearColor(BACKGROUND_COLOR.x(), BACKGROUND_COLOR.y(), BACKGROUND_COLOR.z(), 1);
    }

    private void handleResize(int width, int height) {
        windowSize = new Vector2i(width, height);
        glViewport(0, 0, getWidth(), getHeight());
    }

    private void loop() {

        while (isWindowActive()) {

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Universe.INSTANCE.getLock().lock();
            try {
                renders.forEach(Renderer::render);
            } finally {
                Universe.INSTANCE.getLock().unlock();
            }

            // Calc fps
            long currentFrameTime = getCurrentTime();
            deltaTime = (currentFrameTime - lastFrameTime) / 1000f;
            fps = 1 / deltaTime;
            lastFrameTime = currentFrameTime;

            glfwSwapBuffers(window);
            glfwPollEvents();

        }

    }

    private boolean isWindowActive() {
        return !glfwWindowShouldClose(window);
    }

    private void finish() {
        modelRepository.finish();
        textureRepository.unbind();
        textureRepository.finish();
        glfwDestroyWindow(window);
        for (Callback callback : callbacks) {
            callback.free();
        }
        glfwTerminate();
    }

    public int getWidth() {
        return windowSize.x();
    }

    public int getHeight() {
        return windowSize.y();
    }

    private void registerScrollCallback() {
        GLFWScrollCallback scrollCallback = GLFWScrollCallback.create(
                (window, xOffset, yOffset) -> getDisplayCallbacks().getScrollCallbacks().forEach(e -> e.apply(xOffset, yOffset))
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
                getDisplayCallbacks().getCursorPosCallbacks().forEach(e -> e.apply(xPos, yPos, dx, dy));
            }
        }));
        callbacks.add(posCallback);
        glfwSetCursorPosCallback(window, posCallback);
    }

    private void registerKeyCallback() {
        GLFWKeyCallback keyCallback = GLFWKeyCallback.create(
                (window, key, scanCode, action, mods) -> getDisplayCallbacks().getKeyCallbacks().forEach(e -> e.apply(key, scanCode, action, mods))
        );
        callbacks.add(keyCallback);
        glfwSetKeyCallback(window, keyCallback);
    }

    private void registerMouseButtonCallback() {
        GLFWMouseButtonCallback mouseButtonCallback = GLFWMouseButtonCallback.create(
                (window, button, action, mods) -> getDisplayCallbacks().getMouseButtonCallbacks().forEach(e -> e.apply(button, action, mods))
        );
        callbacks.add(mouseButtonCallback);
        glfwSetMouseButtonCallback(window, mouseButtonCallback);
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
