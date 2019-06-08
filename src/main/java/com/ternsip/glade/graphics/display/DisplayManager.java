package com.ternsip.glade.graphics.display;

import com.ternsip.glade.graphics.general.TextureRepository;
import com.ternsip.glade.universe.graphicals.base.Camera;
import com.ternsip.glade.universe.graphicals.repository.GraphicalRepository;
import com.ternsip.glade.universe.graphicals.repository.ModelRepository;
import com.ternsip.glade.universe.graphicals.repository.ShaderRepository;
import lombok.Getter;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.Callback;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.system.MemoryUtil.NULL;

@Getter
public class DisplayManager {

    public static final Vector3f BACKGROUND_COLOR = new Vector3f(1f, 0f, 0f);

    private static final int FPS_CAP = 120;

    private Camera camera;
    private ArrayList<Callback> callbacks = new ArrayList<>();
    private TextureRepository textureRepository;
    private GraphicalRepository graphicalRepository = new GraphicalRepository();
    private ModelRepository modelRepository = new ModelRepository();
    private ShaderRepository shaderRepository = new ShaderRepository();
    private DisplayCallbacks displayCallbacks = new DisplayCallbacks();
    private DisplaySnapCollector displaySnapCollector = new DisplaySnapCollector();
    private long lastFrameTime;
    private float deltaTime;
    private float fps;
    private long window;
    private Vector2i windowSize;

    public void initialize() {
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

        camera = new Camera();

        lastFrameTime = getCurrentTime();
    }

    private void handleResize(int width, int height) {
        windowSize = new Vector2i(width, height);
        glViewport(0, 0, getWidth(), getHeight());
    }

    public void loop() {
        /* Loop until window gets closed */
        while (isActive()) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            getCamera().update();
            getGraphicalRepository().render();

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

    public boolean isActive() {
        return !glfwWindowShouldClose(window);
    }

    public void finish() {

        getModelRepository().finish();
        getShaderRepository().finish();

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
